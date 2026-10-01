package dev.appiumagentic.examples.screens.ios

import dev.appiumagentic.examples.screens.ProductDetailsScreen
import dev.appiumagentic.ios.IosScreen
import dev.appiumagentic.pageobject.ScreenElement
import io.appium.java_client.AppiumBy
import io.appium.java_client.ios.IOSDriver

/** Locators confirmed against My Demo App iOS 2.2.2's `TabBar.storyboard` and `PageObject.swift`. */
class IosProductDetailsScreen(driver: IOSDriver) : IosScreen(driver), ProductDetailsScreen {
    val screen by element(AppiumBy.accessibilityId("ProductDetails-screen"))

    override val price by element(AppiumBy.accessibilityId("Price"))
    override val quantity by element(AppiumBy.accessibilityId("Amount"))

    // The +/- buttons have no identifier; XCUITest names them after their image asset.
    private val increaseQuantityButton by element(AppiumBy.accessibilityId("AddPlus Icons"))
    private val addToCartButton by element(AppiumBy.accessibilityId("AddToCart"))

    /**
     * The product name label has no identifier (ProductPageDetailViewController sets only its
     * text), so it's matched by its label — hence a function of the expected name.
     */
    fun name(expected: String) = ScreenElement(
        iosDriver,
        AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeStaticText' AND label == '$expected'"),
    )

    override fun increaseQuantity() = increaseQuantityButton.click()

    override fun addToCart() = addToCartButton.click()
}
