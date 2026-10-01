package dev.appiumagentic.android

import dev.appiumagentic.pageobject.Screen
import io.appium.java_client.android.AndroidDriver
import io.appium.java_client.android.nativekey.AndroidKey
import io.appium.java_client.android.nativekey.KeyEvent

/** Base class for Android page objects; adds Android-specific gestures on top of [Screen]. */
abstract class AndroidScreen(protected val androidDriver: AndroidDriver) : Screen(androidDriver) {

    protected fun pressBack() {
        androidDriver.pressKey(KeyEvent(AndroidKey.BACK))
    }

    /** Hides the soft keyboard if shown; UiAutomator2's hideKeyboard errors when it isn't. */
    protected fun hideKeyboard() {
        if (androidDriver.isKeyboardShown) androidDriver.hideKeyboard()
    }
}
