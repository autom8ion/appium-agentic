package dev.appiumagentic.testsupport

import dev.appiumagentic.android.AndroidCapabilityBuilder
import dev.appiumagentic.core.driver.MobileDriverFactory
import dev.appiumagentic.core.driver.MobileSession
import dev.appiumagentic.core.driver.Platform
import dev.appiumagentic.ios.IosCapabilityBuilder
import io.appium.java_client.AppiumDriver
import io.appium.java_client.InteractsWithApps
import io.appium.java_client.android.AndroidDriver
import io.appium.java_client.ios.IOSDriver
import io.qameta.allure.Allure
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.api.extension.ParameterResolver
import org.junit.platform.commons.support.AnnotationSupport
import org.openqa.selenium.OutputType
import org.slf4j.LoggerFactory
import java.io.ByteArrayInputStream

/**
 * Backs [AppiumTest]: starts a [MobileSession] before each test, injects the driver into
 * constructor/method parameters, applies the test's [ResetApp] strategy and tears the session
 * down after, and attaches a screenshot + page source to Allure on failure. Session state lives
 * in the per-test [ExtensionContext.Store], so this is safe under JUnit 5 parallel execution
 * without any `ThreadLocal` bookkeeping.
 */
class AppiumSessionExtension : BeforeEachCallback, AfterEachCallback, ParameterResolver {

    private val log = LoggerFactory.getLogger(AppiumSessionExtension::class.java)

    private val driverFactory = MobileDriverFactory(
        mapOf(
            Platform.ANDROID to AndroidCapabilityBuilder,
            Platform.IOS to IosCapabilityBuilder,
        ),
    )

    override fun beforeEach(context: ExtensionContext) {
        val platform = resolvePlatform(context)
        val session = driverFactory.create(platform)
        store(context).put(SESSION_KEY, session)
    }

    override fun afterEach(context: ExtensionContext) {
        val session = store(context).remove(SESSION_KEY, MobileSession::class.java) ?: return
        // Capture here, not in TestWatcher.testFailed: JUnit runs afterEach first, so by then
        // the session below has already been closed and there's nothing left to capture.
        if (context.executionException.isPresent) captureFailureArtifacts(session)
        try {
            applyResetStrategy(context, session)
        } finally {
            session.close()
        }
    }

    override fun supportsParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): Boolean =
        parameterContext.parameter.type.let {
            it == AppiumDriver::class.java || it == AndroidDriver::class.java || it == IOSDriver::class.java
        }

    override fun resolveParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): Any {
        val session = store(extensionContext).get(SESSION_KEY, MobileSession::class.java)
            ?: error("No active MobileSession — is the test annotated with @AppiumTest(platform = ...)?")
        return session.driver
    }

    private fun resolvePlatform(context: ExtensionContext): Platform {
        val annotation = AnnotationSupport.findAnnotation(context.requiredTestMethod, AppiumTest::class.java)
            .or { AnnotationSupport.findAnnotation(context.requiredTestClass, AppiumTest::class.java) }
        return annotation.orElseThrow {
            IllegalStateException(
                "${context.requiredTestClass.name}#${context.requiredTestMethod.name} is missing " +
                    "@AppiumTest(platform = ...) on the class or method",
            )
        }.platform
    }

    private fun applyResetStrategy(context: ExtensionContext, session: MobileSession) {
        val strategy = AnnotationSupport.findAnnotation(context.requiredTestMethod, ResetApp::class.java)
            .map { it.strategy }
            .or { AnnotationSupport.findAnnotation(context.requiredTestClass, ResetApp::class.java).map { it.strategy } }
            .orElse(ResetStrategy.NEW_SESSION)
        val apps = session.driver as InteractsWithApps
        when (strategy) {
            ResetStrategy.NEW_SESSION -> Unit // session.close() below tears the whole session down
            ResetStrategy.RESET_STATE -> {
                apps.terminateApp(session.config.appId)
                apps.activateApp(session.config.appId)
            }
            ResetStrategy.REINSTALL -> {
                apps.removeApp(session.config.appId)
                apps.installApp(session.config.appPath)
                apps.activateApp(session.config.appId)
            }
        }
    }

    private fun captureFailureArtifacts(session: MobileSession) {
        runCatching {
            val screenshot = session.driver.getScreenshotAs(OutputType.BYTES)
            Allure.addAttachment("Screenshot", "image/png", ByteArrayInputStream(screenshot), "png")
        }.onFailure { log.warn("Failed to capture screenshot on test failure", it) }

        runCatching {
            Allure.addAttachment("Page source", session.driver.pageSource)
        }.onFailure { log.warn("Failed to capture page source on test failure", it) }
    }

    private fun store(context: ExtensionContext) = context.getStore(NAMESPACE)

    companion object {
        private val NAMESPACE = ExtensionContext.Namespace.create(AppiumSessionExtension::class.java)
        private const val SESSION_KEY = "mobileSession"
    }
}
