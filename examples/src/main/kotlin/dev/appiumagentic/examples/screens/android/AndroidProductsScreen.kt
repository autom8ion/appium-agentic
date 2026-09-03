package dev.appiumagentic.examples.screens.android

import dev.appiumagentic.android.AndroidScreen
import io.appium.java_client.AppiumBy
import io.appium.java_client.android.AndroidDriver

/** The product catalog shown after a successful login. */
class AndroidProductsScreen(driver: AndroidDriver) : AndroidScreen(driver) {
    val title by element(AppiumBy.accessibilityId("title"))
}
