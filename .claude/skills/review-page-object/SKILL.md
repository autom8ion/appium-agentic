---
name: review-page-object
description: Review a diff touching *Screen.kt files (or any page object) against appium-agentic's conventions — waits, locator strategy, cross-platform parity, reset usage. Use when reviewing a page-object change or when explicitly asked to review screens/locators.
---

# Review a page object change

Static, checklist-driven review of page object code. Produce inline review comments /
suggested diffs — do not apply edits yourself unless the user asks.

## Checklist

1. **No `Thread.sleep`.** Grep the changed files (and ideally the whole repo) for
   `Thread.sleep` — there should be zero hits anywhere in `core`, `page-object`,
   `platform-android`, `platform-ios`, `test-support`, or `examples`. All waiting must go
   through `dev.appiumagentic.core.wait.Waits`.
2. **No cached `WebElement` fields.** Elements must be declared via `by element(locator)` /
   `by elements(locator)` (which re-resolve on every interaction), never
   `private val x: WebElement = driver.findElement(...)`.
3. **No raw `driver.findElement`/`findElements` outside a `Screen` subclass.** Test code and
   non-page-object classes should never call these directly — that logic belongs in a
   `ScreenElement`/`ScreenElementList` via the base class.
4. **Locator strategy.** `AppiumBy.accessibilityId(...)` is the default. Any other strategy
   (`AppiumBy.id`, `androidUIAutomator`, `iOSClassChain`, `iOSNsPredicateString`, ...) must have
   a comment explaining why accessibilityId isn't available for that element (see
   `AndroidLoginScreen.kt` for the expected style). Flag any fallback locator with no comment.
5. **Cross-platform parity.** If a shared `<Flow>Screen` interface exists, both
   `Android<Flow>Screen` and `Ios<Flow>Screen` should implement the same interface methods with
   equivalent behavior — flag a method added to only one platform's implementation without a
   note on why the other platform doesn't need it.
6. **`@ResetApp` usage.** If a test's isolation strategy changed, check the choice is
   justified: `RESET_STATE`/`REINSTALL` should only be used where `NEW_SESSION` (the default)
   is a proven, measured performance problem, not a default choice.
7. **Naming.** `Android<Flow>Screen`/`Ios<Flow>Screen`, `<Flow>Test`, packages matching
   `CLAUDE.md`'s conventions.

## Output

List findings with file:line references, ranked most-important first (correctness/flake risks
before style). If nothing violates the checklist, say so plainly rather than inventing
nitpicks.
