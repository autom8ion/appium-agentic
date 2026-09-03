package dev.appiumagentic.pageobject

import dev.appiumagentic.core.wait.Waits
import io.appium.java_client.AppiumDriver
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import java.time.Duration

/** A locator resolved fresh on every call — never hold on to its result across interactions. */
class ScreenElementList(private val driver: AppiumDriver, private val locator: By) {

    /** The list's current contents, including empty — does not wait for it to populate. */
    fun resolve(): List<WebElement> = driver.findElements(locator)

    /** Waits until at least one match exists, then returns them. Use for "list has loaded" checks. */
    @JvmOverloads
    fun waitUntilNotEmpty(timeout: Duration = Waits.DEFAULT_TIMEOUT): List<WebElement> =
        Waits.until<List<WebElement>?>(driver, timeout) { it.findElements(locator).ifEmpty { null } }!!
}
