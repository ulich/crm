package net.ulich.crm.scheduler;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EmailHtmlEncoderTest {

    @Test
    void encode() {
        String encoded = EmailHtmlEncoder.encode("<p>Test with Ümlaut and € sign</p>");
        assertThat(encoded).isEqualTo("<p>Test with &Uuml;mlaut and &euro; sign</p>");
    }
}
