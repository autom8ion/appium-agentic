package dev.appiumagentic.examples.screens.android

import dev.appiumagentic.android.AndroidScreen
import dev.appiumagentic.examples.screens.CatalogScreen
import io.appium.java_client.AppiumBy
import io.appium.java_client.android.AndroidDriver

private const val PACKAGE = "com.saucelabs.mydemoapp.android"

/**
 * The product catalog, shown both on app launch (before login) and after a successful login —
 * confirmed against My Demo App Android 2.2.0: `MainActivity` launches straight into
 * `ProductCatalogFragment`, there's no separate pre-login landing screen. Also owns the
 * header (cart icon/badge) and drawer menu, which are shared by every screen in the app.
 * Product locators confirmed against `ProductsAdapter.java`, header against
 * `menu_header_layout.xml`.
 */
class AndroidCatalogScreen(driver: AndroidDriver) : AndroidScreen(driver), CatalogScreen {
    val title by element(AppiumBy.accessibilityId("title"))

    // findElement returns the first match, i.e. the first product in the default
    // name-ascending sort.
    override val firstProductTitle by element(AppiumBy.accessibilityId("Product Title"))
    val firstProductPrice by element(AppiumBy.accessibilityId("Product Price"))
    override val productTitles by elements(AppiumBy.accessibilityId("Product Title"))

    // The click listener is on the product image, not the title (ProductsAdapter.java:51).
    private val productImages by elements(AppiumBy.accessibilityId("Product Image"))

    private val cartButton by element(AppiumBy.accessibilityId("View cart"))

    // The badge's accessibility id ("Displays number of items in your cart") is shared by the
    // cart icon and the badge container, so the count TextView's resource-id is the only
    // unique locator. Its container is GONE at count 0.
    val cartBadge by element(AppiumBy.id("$PACKAGE:id/cartTV"))

    // Confirmed against MainActivity's header/drawer setup: the login flow isn't reachable
    // directly from the catalog screen, only via the hamburger menu drawer.
    private val menuButton by element(AppiumBy.accessibilityId("View menu"))
    private val loginMenuItem by element(AppiumBy.accessibilityId("Login Menu Item"))
    private val logoutMenuItem by element(AppiumBy.accessibilityId("Logout Menu Item"))

    // A plain AlertDialog: its buttons only carry the framework's own android:id/button1.
    private val confirmLogoutButton by element(AppiumBy.id("android:id/button1"))

    override fun openLogin() {
        menuButton.click()
        loginMenuItem.click()
    }

    override fun openProduct(index: Int) = productImages.waitUntilNotEmpty()[index].click()

    override fun openCart() = cartButton.click()

    /** Logs out via drawer → "Log Out" → confirm; the app then shows [AndroidLoginScreen]. */
    override fun logout() {
        menuButton.click()
        logoutMenuItem.click()
        confirmLogoutButton.click()
    }
}
