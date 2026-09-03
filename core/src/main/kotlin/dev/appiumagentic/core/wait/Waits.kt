package dev.appiumagentic.core.wait

import org.openqa.selenium.NoSuchElementException
import org.openqa.selenium.StaleElementReferenceException
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.FluentWait
import java.time.Duration
import java.util.function.Function

/**
 * The single wait/poll mechanism used everywhere this framework interacts with elements.
 * Nothing else in appium-agentic should call `Thread.sleep` or poll manually — routing every
 * wait through here keeps flake sources auditable in one place (and is what the
 * `review-page-object` skill greps for).
 */
object Waits {
    val DEFAULT_TIMEOUT: Duration = Duration.ofSeconds(10)
    val DEFAULT_POLL_INTERVAL: Duration = Duration.ofMillis(200)

    @JvmStatic
    @JvmOverloads
    fun <T> until(
        driver: WebDriver,
        timeout: Duration = DEFAULT_TIMEOUT,
        pollInterval: Duration = DEFAULT_POLL_INTERVAL,
        condition: Function<WebDriver, T>,
    ): T =
        FluentWait(driver)
            .withTimeout(timeout)
            .pollingEvery(pollInterval)
            .ignoring(NoSuchElementException::class.java)
            .ignoring(StaleElementReferenceException::class.java)
            .until(condition)
}
