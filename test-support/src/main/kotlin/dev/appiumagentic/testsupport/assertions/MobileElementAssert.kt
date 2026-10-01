package dev.appiumagentic.testsupport.assertions

import dev.appiumagentic.pageobject.ScreenElement
import org.assertj.core.api.AbstractAssert
import java.time.Duration

/**
 * AssertJ assertions for [ScreenElement] that poll (via [ScreenElement.isDisplayed]'s own wait,
 * or a fresh read of [ScreenElement.text]) rather than failing on the first check — assertions
 * on mobile UI need the same tolerance for in-flight rendering that element interactions do.
 */
class MobileElementAssert(actual: ScreenElement) : AbstractAssert<MobileElementAssert, ScreenElement>(actual, MobileElementAssert::class.java) {

    @JvmOverloads
    fun isVisible(timeout: Duration = Duration.ofSeconds(10)): MobileElementAssert {
        isNotNull
        if (!actual.isDisplayed(timeout)) {
            failWithMessage("Expected element to be visible within %s but it was not", timeout)
        }
        return this
    }

    @JvmOverloads
    fun isNotVisible(timeout: Duration = Duration.ofSeconds(10)): MobileElementAssert {
        isNotNull
        if (!actual.isGone(timeout)) {
            failWithMessage("Expected element to disappear within %s but it was still visible", timeout)
        }
        return this
    }

    fun hasText(expected: String): MobileElementAssert {
        isNotNull
        val actualText = actual.text
        if (actualText != expected) {
            failWithMessage("Expected element text to be <%s> but was <%s>", expected, actualText)
        }
        return this
    }

    fun containsText(expected: String): MobileElementAssert {
        isNotNull
        val actualText = actual.text
        if (!actualText.contains(expected)) {
            failWithMessage("Expected element text <%s> to contain <%s>", actualText, expected)
        }
        return this
    }
}
