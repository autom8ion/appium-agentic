package dev.appiumagentic.examples.screens.ios

import dev.appiumagentic.examples.screens.CatalogScreen
import dev.appiumagentic.ios.IosScreen
import io.appium.java_client.AppiumBy
import io.appium.java_client.ios.IOSDriver

/**
 * The product catalog, shown both on app launch (before login) and after a successful login —
 * confirmed against My Demo App iOS 2.2.2: `Info.plist`'s `UIMainStoryboardFile` is `TabBar`,
 * there's no separate pre-login landing screen. `screen`'s identifier is confirmed from the
 * app's own XCUITest suite (`PageObject.swift`); `moreTab`/`loginButton` are confirmed against
 * `TabBar.storyboard`/`Menu.storyboard`'s `accessibilityConfiguration` identifiers.
 *
 * Unlike Android, there's no catalog price or cart badge here: each cell's price label has a
 * fixed accessibility label ("Product Price") the code never updates, and the cart count is an
 * unidentified `UILabel` beside the tab button, with no unique locator.
 */
class IosCatalogScreen(driver: IOSDriver) : IosScreen(driver), CatalogScreen {
    val screen by element(AppiumBy.accessibilityId("Catalog-screen"))

    // Every cell's name label has the identifier "Product Name" (its label is the product
    // name, set in CatalogViewController); findElement returns the first, i.e. the first
    // product in the default name-ascending sort.
    override val firstProductTitle by element(AppiumBy.accessibilityId("Product Name"))
    override val productTitles by elements(AppiumBy.accessibilityId("Product Name"))
    private val productCells by elements(AppiumBy.accessibilityId("ProductItem"))

    // The app has no UITabBar: every screen embeds its own copy of these tab buttons, so they
    // work from any screen, not just the catalog.
    private val cartTab by element(AppiumBy.accessibilityId("Cart-tab-item"))
    private val moreTab by element(AppiumBy.accessibilityId("More-tab-item"))

    // The same menu entry is "Login" when logged out and "Log Out" when logged in
    // (MenuViewController.swift:24-28); its inner "LogOut-menu-item" button is hidden from
    // XCUITest by the container's isElement=YES.
    private val loginButton by element(AppiumBy.accessibilityId("Login Button"))

    /** Taps the "More" tab, then "Login", to reach [IosLoginScreen]. */
    override fun openLogin() {
        moreTab.click()
        loginButton.click()
    }

    override fun openProduct(index: Int) = productCells.waitUntilNotEmpty()[index].click()

    override fun openCart() = cartTab.click()

    /** "More" → "Log Out"; no confirm alert on iOS. The app then shows [IosLoginScreen]. */
    override fun logout() {
        moreTab.click()
        loginButton.click()
    }
}
