package dev.appiumagentic.examples.screens.android

import dev.appiumagentic.android.AndroidScreen
import dev.appiumagentic.examples.screens.CartScreen
import dev.appiumagentic.pageobject.ScreenElement
import io.appium.java_client.AppiumBy
import io.appium.java_client.android.AndroidDriver

private const val PACKAGE = "com.saucelabs.mydemoapp.android"

/** Locators confirmed against My Demo App Android 2.2.0's `fragment_cart.xml`/`item_my_cart.xml`. */
class AndroidCartScreen(driver: AndroidDriver) : AndroidScreen(driver), CartScreen {

    // CartItemAdapter sets no contentDescription on item title/empty-state TextViews, so the
    // item title is matched by resource-id plus text.
    override val emptyCartMessage by element(AppiumBy.id("$PACKAGE:id/noItemTitleTV"))

    private val removeButton by element(AppiumBy.accessibilityId("Removes product from cart"))
    private val proceedToCheckoutButton by element(AppiumBy.accessibilityId("Confirms products for checkout"))

    override fun item(name: String) = ScreenElement(
        androidDriver,
        AppiumBy.androidUIAutomator("new UiSelector().resourceId(\"$PACKAGE:id/titleTV\").text(\"$name\")"),
    )

    override fun removeFirstItem() = removeButton.click()

    override fun proceedToCheckout() = proceedToCheckoutButton.click()
}
