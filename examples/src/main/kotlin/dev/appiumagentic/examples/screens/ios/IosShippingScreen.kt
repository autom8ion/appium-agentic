package dev.appiumagentic.examples.screens.ios

import dev.appiumagentic.ios.IosScreen
import io.appium.java_client.AppiumBy
import io.appium.java_client.ios.IOSDriver

/**
 * The first checkout step. iOS checkout stops here, unlike `AndroidCheckoutScreen`: the
 * shipping form needs five typed fields, and once the software keyboard is up it covers the
 * fixed "To Payment" footer (and "Review Order"/"Place Order" on later steps) with no way to
 * dismiss it — same root cause as `IosLoginScreen.login`, and this screen has no quick-select
 * buttons to avoid typing. Covered instead: routing into this screen and its empty-form
 * validation. Locators confirmed against `TabBar.storyboard` / `ShippingAddressViewController.swift`.
 */
class IosShippingScreen(driver: IOSDriver) : IosScreen(driver) {
    val screen by element(AppiumBy.accessibilityId("ShippingAddress-screen"))

    // "To Payment" and the validation alert's message have no identifier, only text.
    private val toPaymentButton by element(
        AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeButton' AND label == 'To Payment'"),
    )
    val fullNameRequiredMessage by element(
        AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeStaticText' AND label == 'Please provide your full name.'"),
    )

    fun tapToPayment() = toPaymentButton.click()
}
