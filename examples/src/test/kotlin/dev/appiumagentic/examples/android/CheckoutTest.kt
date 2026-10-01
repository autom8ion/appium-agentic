package dev.appiumagentic.examples.android

import dev.appiumagentic.core.driver.Platform
import dev.appiumagentic.examples.screens.android.AndroidCartScreen
import dev.appiumagentic.examples.screens.android.AndroidCatalogScreen
import dev.appiumagentic.examples.screens.android.AndroidCheckoutScreen
import dev.appiumagentic.examples.screens.android.AndroidLoginScreen
import dev.appiumagentic.examples.screens.android.AndroidProductDetailsScreen
import dev.appiumagentic.examples.screens.android.PaymentDetails
import dev.appiumagentic.examples.screens.android.ShippingAddress
import dev.appiumagentic.testsupport.AppiumTest
import dev.appiumagentic.testsupport.assertions.MobileAssertions.assertThat
import io.appium.java_client.android.AndroidDriver
import org.junit.jupiter.api.Test

@AppiumTest(platform = Platform.ANDROID)
class CheckoutTest {

    private fun addFirstProductAndCheckout(driver: AndroidDriver) {
        val catalog = AndroidCatalogScreen(driver)
        catalog.openProduct(0)
        AndroidProductDetailsScreen(driver).addToCart()
        catalog.openCart()
        AndroidCartScreen(driver).proceedToCheckout()
    }

    @Test
    fun `checking out while logged out asks the user to log in`(driver: AndroidDriver) {
        addFirstProductAndCheckout(driver)

        assertThat(AndroidLoginScreen(driver).loginButton).isVisible()
    }

    @Test
    fun `a logged in user can place an order`(driver: AndroidDriver) {
        addFirstProductAndCheckout(driver)
        // Logging in from checkout continues straight to the shipping form.
        AndroidLoginScreen(driver).login(username = "bob@example.com", password = "10203040")

        val checkout = AndroidCheckoutScreen(driver)
        checkout.enterShippingAddress(
            ShippingAddress(
                fullName = "Rebecca Winter",
                address1 = "Mandorley 112",
                city = "Truro",
                zip = "89750",
                country = "United Kingdom",
            ),
        )
        // Digits only: the card number/expiry fields insert their own spaces and slash.
        checkout.enterPayment(
            PaymentDetails(
                cardHolder = "Rebecca Winter",
                cardNumber = "3258125675687891",
                expiration = "0330",
                securityCode = "123",
            ),
        )
        checkout.placeOrder()

        assertThat(checkout.completeTitle).hasText("Checkout Complete")
    }
}
