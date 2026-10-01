package dev.appiumagentic.examples.ios

import dev.appiumagentic.core.driver.Platform
import dev.appiumagentic.examples.screens.ios.IosCartScreen
import dev.appiumagentic.examples.screens.ios.IosCatalogScreen
import dev.appiumagentic.examples.screens.ios.IosLoginScreen
import dev.appiumagentic.examples.screens.ios.IosProductDetailsScreen
import dev.appiumagentic.examples.screens.ios.IosShippingScreen
import dev.appiumagentic.testsupport.AppiumTest
import dev.appiumagentic.testsupport.assertions.MobileAssertions.assertThat
import io.appium.java_client.ios.IOSDriver
import org.junit.jupiter.api.Test

// Stops at the shipping step — see IosShippingScreen for why iOS can't complete an order.
@AppiumTest(platform = Platform.IOS)
class CheckoutTest {

    private fun addFirstProductAndCheckout(driver: IOSDriver) {
        val catalog = IosCatalogScreen(driver)
        catalog.openProduct(0)
        IosProductDetailsScreen(driver).addToCart()
        catalog.openCart()
        IosCartScreen(driver).proceedToCheckout()
    }

    @Test
    fun `checking out while logged out asks the user to log in`(driver: IOSDriver) {
        addFirstProductAndCheckout(driver)

        assertThat(IosLoginScreen(driver).header).isVisible()
    }

    @Test
    fun `logging in from checkout continues to shipping`(driver: IOSDriver) {
        addFirstProductAndCheckout(driver)

        IosLoginScreen(driver).login(username = "bob@example.com", password = "10203040")

        assertThat(IosShippingScreen(driver).screen).isVisible()
    }

    @Test
    fun `an empty shipping form shows a validation error`(driver: IOSDriver) {
        addFirstProductAndCheckout(driver)
        IosLoginScreen(driver).login(username = "bob@example.com", password = "10203040")

        val shipping = IosShippingScreen(driver)
        shipping.tapToPayment()

        assertThat(shipping.fullNameRequiredMessage).isVisible()
    }
}
