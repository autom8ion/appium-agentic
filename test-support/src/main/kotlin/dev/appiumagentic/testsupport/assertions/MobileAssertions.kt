package dev.appiumagentic.testsupport.assertions

import dev.appiumagentic.pageobject.ScreenElement

/** Static-import-friendly entry point: `assertThat(element).isVisible()`. */
object MobileAssertions {
    @JvmStatic
    fun assertThat(element: ScreenElement): MobileElementAssert = MobileElementAssert(element)
}
