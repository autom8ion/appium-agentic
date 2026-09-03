---
name: scaffold-page-object
description: Generate a new page object (and matching test) for a screen or user flow in the appium-agentic framework, following its Screen/element delegate conventions. Use when the user asks to add a page object, screen, or test for a named app flow.
---

# Scaffold a page object

Generates page objects and a test skeleton for a new screen or flow, following the
conventions in `CLAUDE.md` and the existing `examples/.../screens/` code (`LoginScreen.kt`,
`AndroidLoginScreen.kt`, `IosLoginScreen.kt` are the reference implementation — read them
before generating anything new).

## Inputs to gather

Ask the user for anything not already given:
1. **Flow/screen name** (e.g. "Checkout", "ProductDetails") — becomes `<Flow>Screen`.
2. **Target platform(s)**: Android only, iOS only, or both.
3. **Key elements and actions** on the screen (e.g. "an email field, a submit button, an error
   label"). If the user doesn't know exact locators yet, generate the code with clearly marked
   `TODO` locator placeholders rather than guessing — do not invent accessibility ids or
   resource-ids that haven't been confirmed against the real app (Appium Inspector, or, absent
   that, static inspection of the app bundle — see how `AndroidLoginScreen`/`IosLoginScreen`
   document their locator provenance).

## Workflow

1. Read `CLAUDE.md`'s "Adding a new page object + test" section and the `LoginScreen`
   reference files to match current conventions exactly (delegate syntax, package layout,
   naming).
2. If the flow is cross-platform, create `examples/src/main/kotlin/dev/appiumagentic/examples/screens/<Flow>Screen.kt`
   as a Kotlin interface with the flow's actions (mirror `LoginScreen.kt`'s shape: individual
   action methods plus an optional default-method convenience combinator).
3. For each requested platform, create:
   - Android: `examples/src/main/kotlin/dev/appiumagentic/examples/screens/android/Android<Flow>Screen.kt`,
     extending `AndroidScreen(driver)` and implementing the shared interface (or standalone if
     platform-only). Use `by element(AppiumBy.accessibilityId(...))` for each element; fall back
     to `AppiumBy.id(...)`/`androidUIAutomator(...)` only with a comment explaining why
     accessibilityId isn't available, exactly as `AndroidLoginScreen` does.
   - iOS: `examples/src/main/kotlin/dev/appiumagentic/examples/screens/ios/Ios<Flow>Screen.kt`,
     mirroring the Android file's structure with `IosScreen`/`IOSDriver` and
     `accessibilityId`/`iOSClassChain`/`iOSNsPredicateString` locators.
4. Create the test(s): `examples/src/test/kotlin/dev/appiumagentic/examples/android/<Flow>Test.kt`
   and/or `.../ios/<Flow>Test.kt`, annotated `@AppiumTest(platform = Platform.ANDROID|IOS)`,
   with a driver parameter on each `@Test` method, asserting via
   `dev.appiumagentic.testsupport.assertions.MobileAssertions.assertThat(...)`.
5. If any locators are placeholders (see "Inputs to gather"), tell the user explicitly what
   still needs to be filled in and how (Appium Inspector against a running session) before the
   generated test will pass.
6. Do not run the generated test yourself unless the user has a live Appium server + booted
   emulator/simulator available and asks you to — report what you generated and how to run it
   (`./gradlew :examples:test<Android|Ios> -DtestEnv=local`) instead.
