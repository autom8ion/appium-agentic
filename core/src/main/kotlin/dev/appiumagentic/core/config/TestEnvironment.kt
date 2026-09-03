package dev.appiumagentic.core.config

/**
 * Which layered config file (`config/<platform>/<env>.conf`) a test run resolves against.
 * Selected via `-DtestEnv=local|ci`, defaulting to [LOCAL].
 */
enum class TestEnvironment(val configName: String) {
    LOCAL("local"),
    CI("ci"),
    ;

    companion object {
        const val SYSTEM_PROPERTY: String = "testEnv"

        @JvmStatic
        fun resolve(): TestEnvironment {
            val raw = System.getProperty(SYSTEM_PROPERTY, LOCAL.configName)
            return entries.firstOrNull { it.configName.equals(raw, ignoreCase = true) }
                ?: throw IllegalArgumentException(
                    "Unknown -D$SYSTEM_PROPERTY value '$raw'; expected one of " +
                        entries.joinToString(", ") { it.configName },
                )
        }
    }
}
