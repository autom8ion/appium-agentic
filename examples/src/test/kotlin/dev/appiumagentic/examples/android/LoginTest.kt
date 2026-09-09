package dev.appiumagentic.examples.android

import dev.appiumagentic.core.driver.Platform
import dev.appiumagentic.examples.screens.android.AndroidLoginScreen
import dev.appiumagentic.examples.screens.android.AndroidProductsScreen
import dev.appiumagentic.testsupport.AppiumTest
import dev.appiumagentic.testsupport.assertions.MobileAssertions.assertThat
import io.appium.java_client.android.AndroidDriver
import org.junit.jupiter.api.Test

@AppiumTest(platform = Platform.ANDROID)
class LoginTest {

    @Test
    fun `logging in with valid credentials shows the product catalog`(driver: AndroidDriver) {
        val products = AndroidProductsScreen(driver)
        products.openLogin()

        val login = AndroidLoginScreen(driver)
        login.login(username = "bob@example.com", password = "10203040")

        assertThat(products.title).isVisible()
    }

    @Test
    fun `submitting without a username shows a validation error`(driver: AndroidDriver) {
        AndroidProductsScreen(driver).openLogin()

        val login = AndroidLoginScreen(driver)
        login.tapLogin()

        assertThat(login.usernameError).hasText("Username is required")
    }
}
