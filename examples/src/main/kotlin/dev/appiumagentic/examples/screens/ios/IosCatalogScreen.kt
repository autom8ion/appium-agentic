package dev.appiumagentic.examples.screens.ios

import dev.appiumagentic.ios.IosScreen
import io.appium.java_client.AppiumBy
import io.appium.java_client.ios.IOSDriver

/** The product catalog shown after a successful login. Identifier confirmed from the app's own XCUITest suite (`PageObject.swift`). */
class IosCatalogScreen(driver: IOSDriver) : IosScreen(driver) {
    val screen by element(AppiumBy.accessibilityId("Catalog-screen"))
}
