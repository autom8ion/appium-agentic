package dev.appiumagentic.examples.ios

import dev.appiumagentic.core.driver.Platform
import dev.appiumagentic.examples.screens.ios.IosCatalogScreen
import dev.appiumagentic.examples.screens.ios.IosLoginScreen
import dev.appiumagentic.testsupport.AppiumTest
import dev.appiumagentic.testsupport.assertions.MobileAssertions.assertThat
import io.appium.java_client.ios.IOSDriver
import org.junit.jupiter.api.Test

// No missing-password or locked-out cases on iOS: entering only a username needs the
// keyboard (see IosLoginScreen), and iOS has no locked-out user.
@AppiumTest(platform = Platform.IOS)
class LoginTest {

    @Test
    fun `logging in with valid credentials shows the product catalog`(driver: IOSDriver) {
        val catalog = IosCatalogScreen(driver)
        catalog.openLogin()

        val login = IosLoginScreen(driver)
        login.login(username = "bob@example.com", password = "10203040")

        assertThat(catalog.screen).isVisible()
    }

    @Test
    fun `submitting without a username shows a validation error`(driver: IOSDriver) {
        IosCatalogScreen(driver).openLogin()

        val login = IosLoginScreen(driver)
        login.tapLogin()

        assertThat(login.validationAlert).isVisible()
        assertThat(login.usernameRequiredMessage).isVisible()
    }

    @Test
    fun `logging out returns to the login screen`(driver: IOSDriver) {
        val catalog = IosCatalogScreen(driver)
        catalog.openLogin()
        val login = IosLoginScreen(driver)
        login.login(username = "bob@example.com", password = "10203040")
        assertThat(catalog.screen).isVisible()

        catalog.logout()

        assertThat(login.header).isVisible()
    }
}
