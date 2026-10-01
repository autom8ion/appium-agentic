package dev.appiumagentic.examples.android

import dev.appiumagentic.core.driver.Platform
import dev.appiumagentic.examples.screens.android.AndroidCatalogScreen
import dev.appiumagentic.examples.screens.android.AndroidProductDetailsScreen
import dev.appiumagentic.testsupport.AppiumTest
import dev.appiumagentic.testsupport.assertions.MobileAssertions.assertThat
import io.appium.java_client.android.AndroidDriver
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

@AppiumTest(platform = Platform.ANDROID)
class CatalogTest {

    @Test
    fun `the catalog lists products sorted by name`(driver: AndroidDriver) {
        val catalog = AndroidCatalogScreen(driver)

        Assertions.assertThat(catalog.productTitles.waitUntilNotEmpty()).hasSizeGreaterThan(1)
        assertThat(catalog.firstProductTitle).hasText("Sauce Labs Backpack")
        assertThat(catalog.firstProductPrice).hasText("$ 29.99")
    }

    @Test
    fun `opening a product shows its details`(driver: AndroidDriver) {
        AndroidCatalogScreen(driver).openProduct(0)

        val details = AndroidProductDetailsScreen(driver)
        assertThat(details.name).hasText("Sauce Labs Backpack")
        assertThat(details.price).hasText("$ 29.99")
        assertThat(details.quantity).hasText("1")
    }
}
