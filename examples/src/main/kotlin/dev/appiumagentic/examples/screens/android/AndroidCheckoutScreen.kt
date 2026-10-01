package dev.appiumagentic.examples.screens.android

import dev.appiumagentic.android.AndroidScreen
import io.appium.java_client.AppiumBy
import io.appium.java_client.android.AndroidDriver

private const val PACKAGE = "com.saucelabs.mydemoapp.android"

data class ShippingAddress(
    val fullName: String,
    val address1: String,
    val city: String,
    val zip: String,
    val country: String,
)

data class PaymentDetails(
    val cardHolder: String,
    val cardNumber: String,
    val expiration: String,
    val securityCode: String,
)

/**
 * Shipping → payment → review → complete, confirmed against My Demo App Android 2.2.0's
 * `fragment_checkout_info.xml`, `fragment_checkout.xml`, `fragment_place_order.xml` and
 * `fragment_checkout_complete.xml`. One class for the whole wizard: each step is a single
 * form plus one button, too thin to warrant a class per step. Android-only, so no shared
 * interface: on iOS the software keyboard covers every checkout submit button and can't be
 * dismissed (see `IosShippingScreen`).
 *
 * None of the form EditTexts have a contentDescription, so resource-ids are the only option
 * for them. All three step buttons share the resource-id `paymentBtn` but have distinct
 * accessibility ids, so those are used.
 */
class AndroidCheckoutScreen(driver: AndroidDriver) : AndroidScreen(driver) {

    private val fullNameField by element(AppiumBy.id("$PACKAGE:id/fullNameET"))
    private val address1Field by element(AppiumBy.id("$PACKAGE:id/address1ET"))
    private val cityField by element(AppiumBy.id("$PACKAGE:id/cityET"))
    private val zipField by element(AppiumBy.id("$PACKAGE:id/zipET"))
    private val countryField by element(AppiumBy.id("$PACKAGE:id/countryET"))

    // "To Payment" sits at the bottom of a NestedScrollView, below the fold on phone-sized
    // emulators; UiScrollable scrolls it into view as part of resolving the locator.
    private val toPaymentButton by element(
        AppiumBy.androidUIAutomator(
            "new UiScrollable(new UiSelector().scrollable(true))" +
                ".scrollIntoView(new UiSelector().description(\"Saves user info for checkout\"))",
        ),
    )

    // Same resource-id as the login username field; unique while the payment step is shown.
    private val cardHolderField by element(AppiumBy.id("$PACKAGE:id/nameET"))
    private val cardNumberField by element(AppiumBy.id("$PACKAGE:id/cardNumberET"))
    private val expirationField by element(AppiumBy.id("$PACKAGE:id/expirationDateET"))
    private val securityCodeField by element(AppiumBy.id("$PACKAGE:id/securityCodeET"))
    private val reviewOrderButton by element(
        AppiumBy.accessibilityId("Saves payment info and launches screen to review checkout data"),
    )

    private val placeOrderButton by element(AppiumBy.accessibilityId("Completes the process of checkout"))

    // No contentDescription on the completion title.
    val completeTitle by element(AppiumBy.id("$PACKAGE:id/completeTV"))

    /** Fills the shipping form and continues to payment. */
    fun enterShippingAddress(address: ShippingAddress) {
        fullNameField.sendKeys(address.fullName)
        address1Field.sendKeys(address.address1)
        cityField.sendKeys(address.city)
        zipField.sendKeys(address.zip)
        countryField.sendKeys(address.country)
        hideKeyboard()
        toPaymentButton.click()
    }

    /** Fills the payment form and continues to the order review. */
    fun enterPayment(payment: PaymentDetails) {
        cardHolderField.sendKeys(payment.cardHolder)
        cardNumberField.sendKeys(payment.cardNumber)
        expirationField.sendKeys(payment.expiration)
        securityCodeField.sendKeys(payment.securityCode)
        hideKeyboard()
        reviewOrderButton.click()
    }

    fun placeOrder() = placeOrderButton.click()
}
