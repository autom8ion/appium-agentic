package dev.appiumagentic.core.config

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

class TestEnvironmentTest {

    @AfterEach
    fun clearProperty() {
        System.clearProperty(TestEnvironment.SYSTEM_PROPERTY)
    }

    @Test
    fun `defaults to local when unset`() {
        System.clearProperty(TestEnvironment.SYSTEM_PROPERTY)

        assertThat(TestEnvironment.resolve()).isEqualTo(TestEnvironment.LOCAL)
    }

    @Test
    fun `resolves ci from system property`() {
        System.setProperty(TestEnvironment.SYSTEM_PROPERTY, "ci")

        assertThat(TestEnvironment.resolve()).isEqualTo(TestEnvironment.CI)
    }

    @Test
    fun `rejects unknown environment`() {
        System.setProperty(TestEnvironment.SYSTEM_PROPERTY, "staging")

        assertThatThrownBy { TestEnvironment.resolve() }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("staging")
    }
}
