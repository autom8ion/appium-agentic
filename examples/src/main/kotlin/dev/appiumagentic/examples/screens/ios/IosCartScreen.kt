package dev.appiumagentic.examples.screens.ios

import dev.appiumagentic.examples.screens.CartScreen
import dev.appiumagentic.ios.IosScreen
import dev.appiumagentic.pageobject.ScreenElement
import io.appium.java_client.AppiumBy
import io.appium.java_client.ios.IOSDriver

/** Locators confirmed against My Demo App iOS 2.2.2's `TabBar.storyboard` and `MyCartViewController.swift`. */
class IosCartScreen(driver: IOSDriver) : IosScreen(driver), CartScreen {
    val screen by element(AppiumBy.accessibilityId("Cart-screen"))

    // Item name and empty-state labels have no identifier, only their text.
    override val emptyCartMessage by element(
        AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeStaticText' AND label == 'No Items'"),
    )

    // "Remove Item" is the button's title, which XCUITest also uses as its name (no identifier).
    private val removeButton by element(AppiumBy.accessibilityId("Remove Item"))
    private val proceedToCheckoutButton by element(AppiumBy.accessibilityId("ProceedToCheckout"))

    override fun item(name: String) = ScreenElement(
        iosDriver,
        AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeStaticText' AND label == '$name'"),
    )

    override fun removeFirstItem() = removeButton.click()

    override fun proceedToCheckout() = proceedToCheckoutButton.click()
}
