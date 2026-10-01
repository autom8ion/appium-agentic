package dev.appiumagentic.examples.screens.android

import dev.appiumagentic.android.AndroidScreen
import dev.appiumagentic.examples.screens.ProductDetailsScreen
import io.appium.java_client.AppiumBy
import io.appium.java_client.android.AndroidDriver

private const val PACKAGE = "com.saucelabs.mydemoapp.android"

/** Locators confirmed against My Demo App Android 2.2.0's `fragment_product_detail.xml`. */
class AndroidProductDetailsScreen(driver: AndroidDriver) : AndroidScreen(driver), ProductDetailsScreen {

    // Name, price and quantity TextViews have no contentDescription, only resource-ids.
    val name by element(AppiumBy.id("$PACKAGE:id/productTV"))
    override val price by element(AppiumBy.id("$PACKAGE:id/priceTV"))
    override val quantity by element(AppiumBy.id("$PACKAGE:id/noTV"))

    private val increaseQuantityButton by element(AppiumBy.accessibilityId("Increase item quantity"))
    private val addToCartButton by element(AppiumBy.accessibilityId("Tap to add product to cart"))

    override fun increaseQuantity() = increaseQuantityButton.click()

    override fun addToCart() = addToCartButton.click()
}
