package dev.appiumagentic.examples.ios

import dev.appiumagentic.core.driver.Platform
import dev.appiumagentic.examples.screens.ios.IosCatalogScreen
import dev.appiumagentic.examples.screens.ios.IosLoginScreen
import dev.appiumagentic.testsupport.AppiumTest
import dev.appiumagentic.testsupport.assertions.MobileAssertions.assertThat
import io.appium.java_client.ios.IOSDriver
import org.junit.jupiter.api.Test

@AppiumTest(platform = Platform.IOS)
class LoginTest {

    @Test
    fun `logging in with valid credentials shows the product catalog`(driver: IOSDriver) {
        val login = IosLoginScreen(driver)

        login.login(username = "bob@example.com", password = "10203040")

        val catalog = IosCatalogScreen(driver)
        assertThat(catalog.screen).isVisible()
    }
}
