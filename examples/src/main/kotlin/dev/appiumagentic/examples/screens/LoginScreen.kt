package dev.appiumagentic.examples.screens

/** The login flow, implemented per-platform by `AndroidLoginScreen`/`IosLoginScreen`. */
interface LoginScreen {
    fun enterUsername(username: String)
    fun enterPassword(password: String)
    fun tapLogin()

    fun login(username: String, password: String) {
        enterUsername(username)
        enterPassword(password)
        tapLogin()
    }
}
