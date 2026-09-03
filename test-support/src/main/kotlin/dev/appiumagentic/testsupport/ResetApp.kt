package dev.appiumagentic.testsupport

/** How the app under test is isolated between tests. Applies at class or method level; method wins. */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ResetApp(val strategy: ResetStrategy = ResetStrategy.NEW_SESSION)

enum class ResetStrategy {
    /** Quit the session after the test and start a fresh one for the next. Default: simplest, most deterministic. */
    NEW_SESSION,

    /** Reuse the session; terminate then reactivate the app under test. Faster than [NEW_SESSION], weaker isolation. */
    RESET_STATE,

    /** Reuse the session; uninstall and reinstall the app under test. Strongest isolation short of [NEW_SESSION], slowest. */
    REINSTALL,
}
