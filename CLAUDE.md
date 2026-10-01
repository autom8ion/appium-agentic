# appium-agentic

A Kotlin/Java Appium 2.x framework for testing the same app on iOS (XCUITest) and Android
(UiAutomator2), plus a Claude Code dev-time layer (this file, `.claude/skills/`,
`.claude/agents/`) that knows the framework's conventions.

**Guardrail: the test framework itself makes zero LLM calls at test-run time.** Everything
under `core/`, `page-object/`, `platform-android/`, `platform-ios/`, `test-support/` and
`examples/` runs fully deterministically — driver/session lifecycle, waits, assertions. Claude
Code's role is strictly dev-time: scaffolding page objects/tests and triaging failures, never
anything invoked from session-lifecycle or test-execution code. Don't add an LLM call anywhere
in those modules.

## Modules

| Module             | Contains                                                                 |
|---------------------|---------------------------------------------------------------------------|
| `core`               | `MobileDriverFactory`/`MobileSession`, `Platform`, `PlatformConfig`, `Waits` |
| `page-object`        | `Screen` base class, `element`/`elements` delegates, `ScreenElement(List)`, `ScreenFactory` |
| `platform-android`   | `AndroidScreen`, `AndroidCapabilityBuilder` (`UiAutomator2Options`)        |
| `platform-ios`       | `IosScreen`, `IosCapabilityBuilder` (`XCUITestOptions`)                    |
| `test-support`       | `@AppiumTest`, `AppiumSessionExtension`, `@ResetApp`, `MobileAssertions`   |
| `examples`           | Real page objects + tests against the "My Demo App" sample apps           |

Not a Gradle module: `maestro/` (repo root) is a sibling Maestro suite — YAML flows mirroring
the Appium tests in `examples/`, run via `:examples:maestroAndroid`/`:examples:maestroIos`.

Dependency direction: `core` → `page-object` → `platform-android`/`platform-ios` →
`test-support` → `examples`. `core` never depends on a platform module — `MobileDriverFactory`
is injected with one `CapabilityBuilder` per `Platform` by its caller (`AppiumSessionExtension`
wires this for you).

## Adding a new page object + test

1. Add a shared behavior interface in `examples/.../screens/` if the flow exists on both
   platforms (see `LoginScreen.kt`); skip it for a platform-only flow.
2. Implement it per platform in `examples/.../screens/android/` and `.../screens/ios/`,
   extending `AndroidScreen`/`IosScreen`. Declare elements with `by element(locator)` /
   `by elements(locator)` — never cache a raw `WebElement` field.
3. Locator priority: `AppiumBy.accessibilityId(...)` first (stable across both platforms).
   Fall back to `AppiumBy.id(...)` (Android resource-id), `androidUIAutomator`, `iOSClassChain`,
   or `iOSNsPredicateString` only when no accessibility id is set on the element, and say why
   in a comment (see `AndroidLoginScreen` for a real example of this).
4. Write the test in `examples/src/test/kotlin/.../android/` or `.../ios/`, annotated
   `@AppiumTest(platform = Platform.ANDROID)` (or `.IOS`), with an `AndroidDriver`/`IOSDriver`
   test-method parameter. Assert via `MobileAssertions.assertThat(screenElement)`.
5. The `scaffold-page-object` skill does steps 1–4 for you given a screen/flow description.

## Maestro flows

- Layout: `maestro/<platform>/<flow>/<case>.yaml`, one file per Appium test method (snake_case
  of its name); shared steps in `maestro/<platform>/common/`, pulled in via `runFlow`.
  `maestro/<platform>/config.yaml` lists which folders are runnable flows.
- Each flow starts with `launchApp: { clearState: true }` (≙ `@ResetApp(NEW_SESSION)`).
- Selectors must match the locators in the corresponding `Android*/Ios*Screen` — the page
  objects (backed by `locator-inspector`) are the source of truth. Keep both suites' cases in
  sync; the `maestro-flow-author` agent does this.
- Run: install the app on the booted device first (Maestro doesn't), then
  `./gradlew :examples:maestroAndroid` / `:examples:maestroIos`. Reports:
  `examples/build/maestro/<platform>.xml`.

## Claude Code agents

`.claude/agents/`: `locator-inspector` (confirm locators from app source — run before writing
any screen/flow), `page-object-scaffolder`, `page-object-reviewer` (read-only review of
page object/test/flow diffs), `maestro-flow-author`, `mobile-test-debugger`.

## Naming conventions

- Page objects: `Android<Flow>Screen` / `Ios<Flow>Screen`, implementing a shared `<Flow>Screen`
  interface when the flow is cross-platform.
- Tests: `<Flow>Test`, one file per flow, under `examples/.../android/` or `.../ios/`.
- One package per user flow under `examples/.../screens/`.

## Config layering

`PlatformConfig.load(platform, environment)` resolves `config/<platform>/<env>.conf` on the
consuming module's test classpath (see `examples/src/test/resources/config/`), layered over
`core`'s `reference.conf` defaults, with `-D` system property overrides (namespaced
`appium-agentic.*`) taking highest precedence. `environment` is `local` (default) or `ci`,
selected via `-DtestEnv=`.

Each `<platform>/<env>.conf` must set: `device-name`, `platform-version`, `app-path`, `app-id`
(Android package / iOS bundle id). See `examples/src/test/resources/config/android/local.conf`
for a real example.

## Running locally

Requires a locally running Appium 2.x server (`appium` on `http://127.0.0.1:4723` by default)
with the `uiautomator2`/`xcuitest` drivers installed, and a booted Android Emulator / iOS
Simulator matching the `device-name`/`platform-version` in `config/<platform>/local.conf`.

```
./gradlew :examples:testAndroid -DtestEnv=local
./gradlew :examples:testIos -DtestEnv=local
```

Both tasks depend on `:examples:downloadSampleApps`, which fetches the pinned "My Demo App"
Android/iOS builds into `examples/apps/` (gitignored) on first run.

Building this project requires JDK 17+ to run Gradle itself (the toolchain auto-provisioning
plugin needs it); the actual framework code compiles against a JDK 21 toolchain, auto-provisioned
if you don't already have one on `PATH`.

## Reset strategies

`@ResetApp(strategy = ...)` on a test class or method controls isolation between tests:
`NEW_SESSION` (default — new Appium session per test, simplest and most deterministic),
`RESET_STATE` (reuse the session, terminate+reactivate the app — faster, weaker isolation),
`REINSTALL` (reuse the session, uninstall+reinstall the app — strongest isolation short of a
new session, slowest).

## Reviewing page object changes

The `review-page-object` skill checks: no `Thread.sleep` (grep for it — there should be zero
hits outside `Waits`), all element access via `element{}`/`elements{}`, `accessibilityId`
preferred with a comment justifying any fallback, cross-platform interface implementations
have matching method surfaces, no raw `driver.findElement` outside a `Screen` subclass, and
correct `@ResetApp` usage.
