package dev.appiumagentic.examples.screens

import dev.appiumagentic.pageobject.ScreenElement
import dev.appiumagentic.pageobject.ScreenElementList

/**
 * The product catalog plus the app-wide navigation reachable from it (cart, login/logout),
 * implemented per-platform by `AndroidCatalogScreen`/`IosCatalogScreen`. Catalog prices and
 * the cart badge are Android-only: on iOS neither is exposed to XCUITest (see `IosCatalogScreen`).
 */
interface CatalogScreen {
    val firstProductTitle: ScreenElement
    val productTitles: ScreenElementList

    fun openLogin()
    fun openProduct(index: Int)
    fun openCart()
    fun logout()
}
