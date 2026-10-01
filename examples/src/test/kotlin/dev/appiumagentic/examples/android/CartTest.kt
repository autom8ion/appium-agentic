package dev.appiumagentic.examples.android

import dev.appiumagentic.core.driver.Platform
import dev.appiumagentic.examples.screens.android.AndroidCartScreen
import dev.appiumagentic.examples.screens.android.AndroidCatalogScreen
import dev.appiumagentic.examples.screens.android.AndroidProductDetailsScreen
import dev.appiumagentic.testsupport.AppiumTest
import dev.appiumagentic.testsupport.assertions.MobileAssertions.assertThat
import io.appium.java_client.android.AndroidDriver
import org.junit.jupiter.api.Test

@AppiumTest(platform = Platform.ANDROID)
class CartTest {

    @Test
    fun `adding a product updates the cart badge`(driver: AndroidDriver) {
        val catalog = AndroidCatalogScreen(driver)
        catalog.openProduct(0)

        AndroidProductDetailsScreen(driver).addToCart()

        assertThat(catalog.cartBadge).hasText("1")
    }

    @Test
    fun `adding a quantity of two counts both in the badge`(driver: AndroidDriver) {
        val catalog = AndroidCatalogScreen(driver)
        catalog.openProduct(0)

        val details = AndroidProductDetailsScreen(driver)
        details.increaseQuantity()
        assertThat(details.quantity).hasText("2")
        details.addToCart()

        assertThat(catalog.cartBadge).hasText("2")
    }

    @Test
    fun `the cart lists an added product`(driver: AndroidDriver) {
        val catalog = AndroidCatalogScreen(driver)
        catalog.openProduct(0)
        AndroidProductDetailsScreen(driver).addToCart()

        catalog.openCart()

        assertThat(AndroidCartScreen(driver).item("Sauce Labs Backpack")).isVisible()
    }

    @Test
    fun `removing the only item empties the cart`(driver: AndroidDriver) {
        val catalog = AndroidCatalogScreen(driver)
        catalog.openProduct(0)
        AndroidProductDetailsScreen(driver).addToCart()
        catalog.openCart()

        val cart = AndroidCartScreen(driver)
        cart.removeFirstItem()

        assertThat(cart.emptyCartMessage).hasText("No Items")
    }
}
