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
- A local Appium 2.x server (`npm install -g appium@2 && appium driver install uiautomator2
  xcuitest`) running on `http://127.0.0.1:4723`.
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

## Modules

`core` → `page-object` → `platform-android` / `platform-ios` → `test-support` → `examples`.
See [`CLAUDE.md`](./CLAUDE.md) for what each module owns and how to add a new page object.

## License

MIT — see [LICENSE](./LICENSE).
