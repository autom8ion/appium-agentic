package dev.appiumagentic.pageobject

import dev.appiumagentic.core.wait.Waits
import io.appium.java_client.AppiumDriver
import org.openqa.selenium.By
import org.openqa.selenium.TimeoutException
import org.openqa.selenium.WebElement
import java.time.Duration

/**
 * A locator that re-resolves the underlying [WebElement] on every interaction rather than
 * caching one at construction time. This is what keeps page objects immune to
 * `StaleElementReferenceException` across screen transitions, app resets, and re-renders.
 */
class ScreenElement(private val driver: AppiumDriver, private val locator: By) {

    private fun resolve(timeout: Duration = Waits.DEFAULT_TIMEOUT): WebElement =
        Waits.until(driver, timeout) { it.findElement(locator) }

    /** True once no matching element is displayed (absent or hidden), false if one still is at [timeout]. */
    @JvmOverloads
    fun isGone(timeout: Duration = Waits.DEFAULT_TIMEOUT): Boolean =
        try {
            Waits.until(driver, timeout) { d -> d.findElements(locator).none { it.isDisplayed } }
        } catch (e: TimeoutException) {
            false
        }

    fun click() = resolve().click()

    fun sendKeys(text: String) = resolve().sendKeys(text)

    fun clear() = resolve().clear()

    val text: String
        get() = resolve().text

    @JvmOverloads
    fun isDisplayed(timeout: Duration = Waits.DEFAULT_TIMEOUT): Boolean =
        try {
            resolve(timeout).isDisplayed
        } catch (e: TimeoutException) {
            false
        }
}
