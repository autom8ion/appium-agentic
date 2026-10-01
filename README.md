# appium-agentic

A Kotlin/Java Appium 2.x framework for testing the same app on iOS (XCUITest) and Android
(UiAutomator2), plus a Claude Code dev-time layer (`.claude/`) that knows the framework's
conventions well enough to scaffold new page objects/tests and triage failures. See
[`CLAUDE.md`](./CLAUDE.md) for the full architecture, module layout, and conventions.

The test framework itself is fully deterministic — no LLM calls anywhere in driver/session
lifecycle, waits, or assertions. "Agentic" here means Claude Code as a dev-time assistant, not
an LLM in the test-execution path.

## Requirements

- JDK 17+ on `PATH` to run Gradle (the toolchain auto-provisioning plugin needs it); the
  framework code itself compiles against an auto-provisioned JDK 21 toolchain.
- A local Appium 2.x server (`npm install -g appium@2 && appium driver install
  uiautomator2@4.2.4 xcuitest@9.2.5`) running on `http://127.0.0.1:4723`. Driver versions are
  pinned because newer `uiautomator2`/`xcuitest` driver releases require Appium 3.
- A booted Android Emulator and/or iOS Simulator matching
  `examples/src/test/resources/config/{android,ios}/local.conf`.

## Running the examples

```
./gradlew :examples:testAndroid -DtestEnv=local
./gradlew :examples:testIos -DtestEnv=local
```

Both tasks depend on `:examples:downloadSampleApps`, which fetches the pinned
[Sauce Labs "My Demo App"](https://github.com/saucelabs/my-demo-app-android) Android/iOS builds
into `examples/apps/` (gitignored) on first run.

## Maestro suite

[`maestro/`](./maestro) holds YAML flows that mirror the Appium tests case-for-case against the
same apps and the same locators — handy for comparing the two approaches side by side. Install
the [Maestro CLI](https://maestro.mobile.dev), install the app on a booted device
(`adb install examples/apps/mda.apk` / `xcrun simctl install booted examples/apps/MyDemoApp.app`),
then:

```
./gradlew :examples:maestroAndroid
./gradlew :examples:maestroIos
```

JUnit reports land in `examples/build/maestro/`. No Appium server needed.

## Claude Code agents

`.claude/agents/` has dev-time subagents: `locator-inspector` (confirms locators from the app
source/bundle), `page-object-scaffolder`, `page-object-reviewer`, `maestro-flow-author`, and
`mobile-test-debugger`.

## Modules

`core` → `page-object` → `platform-android` / `platform-ios` → `test-support` → `examples`.
See [`CLAUDE.md`](./CLAUDE.md) for what each module owns and how to add a new page object.

## License

MIT — see [LICENSE](./LICENSE).
