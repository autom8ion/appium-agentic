package dev.appiumagentic.examples.android

import dev.appiumagentic.core.driver.Platform
import dev.appiumagentic.examples.screens.android.AndroidCatalogScreen
import dev.appiumagentic.examples.screens.android.AndroidLoginScreen
import dev.appiumagentic.testsupport.AppiumTest
import dev.appiumagentic.testsupport.assertions.MobileAssertions.assertThat
import io.appium.java_client.android.AndroidDriver
import org.junit.jupiter.api.Test

@AppiumTest(platform = Platform.ANDROID)
class LoginTest {

    @Test
    fun `logging in with valid credentials shows the product catalog`(driver: AndroidDriver) {
        val catalog = AndroidCatalogScreen(driver)
        catalog.openLogin()

        val login = AndroidLoginScreen(driver)
        login.login(username = "bob@example.com", password = "10203040")

        assertThat(catalog.title).isVisible()
    }

    @Test
    fun `submitting without a username shows a validation error`(driver: AndroidDriver) {
        AndroidCatalogScreen(driver).openLogin()

        val login = AndroidLoginScreen(driver)
        login.tapLogin()

        assertThat(login.usernameError).hasText("Username is required")
    }

    @Test
    fun `submitting without a password shows a validation error`(driver: AndroidDriver) {
        AndroidCatalogScreen(driver).openLogin()

        val login = AndroidLoginScreen(driver)
        login.enterUsername("bob@example.com")
        login.tapLogin()

        assertThat(login.passwordError).hasText("Enter Password")
    }

    // Android-only: on iOS, alice@example.com logs in like any other user.
    @Test
    fun `a locked out user sees an error`(driver: AndroidDriver) {
        AndroidCatalogScreen(driver).openLogin()

        val login = AndroidLoginScreen(driver)
        login.login(username = "alice@example.com", password = "10203040")

        assertThat(login.passwordError).hasText("Sorry this user has been locked out.")
    }

    @Test
    fun `logging out returns to the login screen`(driver: AndroidDriver) {
        val catalog = AndroidCatalogScreen(driver)
        catalog.openLogin()
        val login = AndroidLoginScreen(driver)
        login.login(username = "bob@example.com", password = "10203040")
        assertThat(catalog.title).isVisible()

        catalog.logout()

        assertThat(login.loginButton).isVisible()
    }
}
