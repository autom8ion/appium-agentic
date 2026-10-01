package dev.appiumagentic.examples.screens

import dev.appiumagentic.pageobject.ScreenElement

/** A single product's page, implemented per-platform by `Android/IosProductDetailsScreen`. */
interface ProductDetailsScreen {
    val price: ScreenElement
    val quantity: ScreenElement

    fun increaseQuantity()
    fun addToCart()
}
