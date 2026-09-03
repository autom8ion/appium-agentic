package dev.appiumagentic.testsupport

import dev.appiumagentic.core.driver.Platform
import org.junit.jupiter.api.extension.ExtendWith

/**
 * Marks a test class or method as an Appium test, starting/injecting/tearing down a
 * [dev.appiumagentic.core.driver.MobileSession] for [platform] around it. Declare an
 * `AppiumDriver`/`AndroidDriver`/`IOSDriver` constructor or test-method parameter to receive
 * the driver.
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@ExtendWith(AppiumSessionExtension::class)
annotation class AppiumTest(val platform: Platform)
