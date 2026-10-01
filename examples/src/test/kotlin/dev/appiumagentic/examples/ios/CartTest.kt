package dev.appiumagentic.examples.ios

import dev.appiumagentic.core.driver.Platform
import dev.appiumagentic.examples.screens.ios.IosCartScreen
import dev.appiumagentic.examples.screens.ios.IosCatalogScreen
import dev.appiumagentic.examples.screens.ios.IosProductDetailsScreen
import dev.appiumagentic.testsupport.AppiumTest
import dev.appiumagentic.testsupport.assertions.MobileAssertions.assertThat
import io.appium.java_client.ios.IOSDriver
import org.junit.jupiter.api.Test

// No cart-badge cases on iOS: the count label has no unique locator (see IosCatalogScreen).
@AppiumTest(platform = Platform.IOS)
class CartTest {

    @Test
    fun `adding a quantity of two updates the quantity`(driver: IOSDriver) {
        IosCatalogScreen(driver).openProduct(0)

        val details = IosProductDetailsScreen(driver)
        details.increaseQuantity()

        assertThat(details.quantity).hasText("2")
    }

    @Test
    fun `the cart lists an added product`(driver: IOSDriver) {
        val catalog = IosCatalogScreen(driver)
        catalog.openProduct(0)
        IosProductDetailsScreen(driver).addToCart()

        catalog.openCart()

        assertThat(IosCartScreen(driver).item("Sauce Labs Backpack - Black")).isVisible()
    }

    @Test
    fun `removing the only item empties the cart`(driver: IOSDriver) {
        val catalog = IosCatalogScreen(driver)
        catalog.openProduct(0)
        IosProductDetailsScreen(driver).addToCart()
        catalog.openCart()

        val cart = IosCartScreen(driver)
        val item = cart.item("Sauce Labs Backpack - Black")
        assertThat(item).isVisible()
        cart.removeFirstItem()

        // Not emptyCartMessage: on iOS the "No Items" label is never hidden, only covered by
        // the item list, so XCUITest reports it visible either way.
        assertThat(item).isNotVisible()
    }
}
