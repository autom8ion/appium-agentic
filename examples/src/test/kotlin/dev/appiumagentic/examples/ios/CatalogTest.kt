package dev.appiumagentic.examples.ios

import dev.appiumagentic.core.driver.Platform
import dev.appiumagentic.examples.screens.ios.IosCatalogScreen
import dev.appiumagentic.examples.screens.ios.IosProductDetailsScreen
import dev.appiumagentic.testsupport.AppiumTest
import dev.appiumagentic.testsupport.assertions.MobileAssertions.assertThat
import io.appium.java_client.ios.IOSDriver
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

@AppiumTest(platform = Platform.IOS)
class CatalogTest {

    @Test
    fun `the catalog lists products sorted by name`(driver: IOSDriver) {
        val catalog = IosCatalogScreen(driver)

        Assertions.assertThat(catalog.productTitles.waitUntilNotEmpty()).hasSizeGreaterThan(1)
        assertThat(catalog.firstProductTitle).hasText("Sauce Labs Backpack - Black")
    }

    @Test
    fun `opening a product shows its details`(driver: IOSDriver) {
        IosCatalogScreen(driver).openProduct(0)

        val details = IosProductDetailsScreen(driver)
        assertThat(details.name("Sauce Labs Backpack - Black")).isVisible()
        assertThat(details.price).hasText("$ 29.99")
        assertThat(details.quantity).hasText("1")
    }
}
