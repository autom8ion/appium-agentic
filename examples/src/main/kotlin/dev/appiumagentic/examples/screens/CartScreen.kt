package dev.appiumagentic.examples.screens

import dev.appiumagentic.pageobject.ScreenElement

/** The cart, implemented per-platform by `AndroidCartScreen`/`IosCartScreen`. */
interface CartScreen {
    val emptyCartMessage: ScreenElement

    fun item(name: String): ScreenElement

    fun removeFirstItem()
    fun proceedToCheckout()
}
