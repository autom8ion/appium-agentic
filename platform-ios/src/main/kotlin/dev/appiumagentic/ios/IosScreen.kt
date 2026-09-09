package dev.appiumagentic.ios

import dev.appiumagentic.pageobject.Screen
import io.appium.java_client.ios.IOSDriver

/** Base class for iOS page objects; adds iOS-specific gestures on top of [Screen]. */
abstract class IosScreen(protected val iosDriver: IOSDriver) : Screen(iosDriver) {

    protected fun hideKeyboard() {
        // The no-arg strategy (tap outside/press "Done") fails against My Demo App iOS
        // 2.2.2's login screen in CI: "Did not know how to dismiss the keyboard". WDA's
        // fb_dismissKeyboardWithKeyNames matches a key's `identifier`/`label` against the
        // given name(s) with an exact, case-sensitive comparison, and it's not documented
        // which case a given app's return key uses — so try both, plus the common "Done"
        // spellings `mobile: hideKeyboard` always appends "done" to whatever list is passed.
        iosDriver.executeScript("mobile: hideKeyboard", mapOf("keys" to listOf("return", "Return", "Done")))
    }
}
