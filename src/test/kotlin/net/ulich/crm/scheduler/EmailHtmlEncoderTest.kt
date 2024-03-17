package net.ulich.crm.scheduler

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class EmailHtmlEncoderTest {
    @Test
    fun encode() {
        val encoded = EmailHtmlEncoder.encode("<p>Test with Ümlaut and € sign</p>")
        assertThat(encoded).isEqualTo("<p>Test with &Uuml;mlaut and &euro; sign</p>")
    }
}
