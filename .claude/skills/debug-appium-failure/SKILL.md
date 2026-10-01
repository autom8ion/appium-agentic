---
name: debug-appium-failure
description: Investigate a failing or flaky appium-agentic test — reads Allure results, screenshots, page source, and Appium server logs to find the root cause and propose a fix. Use when a test failed, is flaky, or the user pastes a stack trace or CI failure link.
---

# Debug an Appium test failure

Investigates why a test failed (or is flaky) and proposes a root cause and fix. Never silently
retries or auto-fixes the test — always surface findings for human review first.

## Where to look

- **Allure results**: `examples/build/allure-results/*.json` (or `*.xml`) for the failing run —
  each result includes the failure message/stacktrace, timing, and (via `AppiumSessionExtension`'s
  `TestWatcher`) an attached screenshot and page source captured at the moment of failure.
- **Appium server log**: wherever the local server was started with output redirected, or the
  CI job's "Start Appium server" step log if this is a CI failure.
- **Test config**: `examples/src/test/resources/config/<platform>/<env>.conf` for the
  device/app/version the run used.
- **CI history** (if investigating flakiness): `git log` on the touched `Screen.kt`/locator
  files, and recent workflow runs, to see whether the flake correlates with a locator or app
  version change.

- **Maestro failures** (`maestro/` suite): JUnit report at `examples/build/maestro/<platform>.xml`,
  and per-run screenshots/logs under `~/.maestro/tests/<timestamp>/` (CI uploads both as the
  `maestro-results-<platform>` artifact). A flow failing while the matching Appium test passes
  usually means the flow's selector drifted from the page object's locator.

For a deep investigation spanning many log files or multiple CI runs, delegate to the
`mobile-test-debugger` subagent rather than pulling all of it into the main conversation.

## Classification checklist

Work through these in order — most Appium failures fall into one of:

1. **Locator not found** (`NoSuchElementException` after the wait timeout) — the element
   genuinely isn't there yet, the locator is stale (UI changed), or it's on a different
   screen than expected. Check the attached screenshot/page source first.
2. **Timeout on a condition that isn't purely "element exists"** — e.g. waiting on text that
   never matches; check whether the expected value assumption in the test is still correct.
3. **Stale element reference** — should be rare given this framework's re-resolving
   `ScreenElement`s; if it happens, look for code that bypassed `element{}`/`Waits` and cached
   a raw `WebElement`.
4. **App crash or ANR** — check the Appium server log around the failure timestamp for a
   crash/ANR signal, not just the test's own exception.
5. **Session-start / environment failure** (fails before the test body even runs) — capability
   mismatch, wrong `device-name`/`platform-version` for what's actually booted, Appium server
   not running, or app binary missing (`:examples:downloadSampleApps` not run).

For flakiness specifically, additionally check: does the failure correlate with a specific
device/CI runner, a specific `ResetApp` strategy, or a recent locator change? A flake that only
reproduces under `RESET_STATE`/`REINSTALL` often points to app state leaking between tests.

## Output

Report: the classification, the concrete root cause, the evidence (which log/screenshot/line
led there), and a proposed fix (e.g. "switch to `AppiumBy.accessibilityId(...)`", "the wait
timeout is too short for this screen's load time", "app state leaks — use `@ResetApp(NEW_SESSION)`
for this test"). Do not apply the fix yourself unless the user asks you to.
