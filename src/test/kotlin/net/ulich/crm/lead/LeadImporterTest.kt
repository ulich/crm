package net.ulich.crm.lead

import io.jmix.core.DataManager
import net.ulich.crm.entity.Gender
import net.ulich.crm.entity.Lead
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

@Suppress("JmixIncorrectCreateEntity")
class LeadImporterTest {

    val dataManager = mock(DataManager::class.java)
    val importer = LeadImporter(dataManager)

    @BeforeEach
    fun setUp() {
        `when`(dataManager.create(Lead::class.java)).thenReturn(Lead())
    }

    @Test
    fun testImportFromTabCsv() {
        val lead = importer.importLead(
            """
            Anfrage gestellt	ID	Firma	Anrede	Vorname	Nachname	Straße	PLZ	Stadt	Land	E-Mail	Rufnummer	Alternative Rufnummer	Mobil	Einsatzort	Anzahl Personen	Kommentar
            12.03.2024 09:18	136353	Some Company GmbH	Herr	Max	Mustermann	Hauptstraße 1	12345	Berlin	DE	max.muster@gmbh.de	+49 123 456789	+49 987 654321	+49 174 12345	Ladenlokal	100+	Allgemeine Rufnummer: 0333 132321""".trimIndent()
        )

        assertThat(lead).isNotNull()
        assertThat(lead?.getGender()).isEqualTo(Gender.MR)
        assertThat(lead?.companyName).isEqualTo("Some Company GmbH")
        assertThat(lead?.firstName).isEqualTo("Max")
        assertThat(lead?.lastName).isEqualTo("Mustermann")
        assertThat(lead?.street).isEqualTo("Hauptstraße 1")
        assertThat(lead?.postCode).isEqualTo("12345")
        assertThat(lead?.city).isEqualTo("Berlin")
        assertThat(lead?.email).isEqualTo("max.muster@gmbh.de")
        assertThat(lead?.phoneNumber).isEqualTo("+49 123 456789")
        assertThat(lead?.alternativePhoneNumber).isEqualTo("+49 987 654321")
        assertThat(lead?.notes).isEqualTo(
            """
                Anfrage gestellt: 12.03.2024 09:18
                ID: 136353
                Land: DE
                Mobil: +49 174 12345
                Einsatzort: Ladenlokal
                Anzahl Personen: 100+
                Kommentar: Allgemeine Rufnummer: 0333 132321
                """.trimIndent()
        )
    }

    @Test
    fun testImportFromSemicolonCsv() {
        val lead = importer.importLead(
            """
            "Anfrage gestellt";ID;Firma;Vorname;Nachname;Straße;PLZ;Stadt;Land;E-Mail;Telefon;Einsatzort;"Anzahl Personen";Kommentar
            "12.03.2024 09:18";136353;"Some Company GmbH";Max;Mustermann;"Hauptstraße 1";12345;Berlin;DE;max.muster@gmbh.de;"+49 123 456789";Ladenlokal;30-50;"Allgemeine Rufnummer: 0333 132321"
            """.trimIndent()
        )

        assertThat(lead).isNotNull()
        assertThat(lead?.getGender()).isNull()
        assertThat(lead?.companyName).isEqualTo("Some Company GmbH")
        assertThat(lead?.firstName).isEqualTo("Max")
        assertThat(lead?.lastName).isEqualTo("Mustermann")
        assertThat(lead?.street).isEqualTo("Hauptstraße 1")
        assertThat(lead?.postCode).isEqualTo("12345")
        assertThat(lead?.city).isEqualTo("Berlin")
        assertThat(lead?.email).isEqualTo("max.muster@gmbh.de")
        assertThat(lead?.phoneNumber).isEqualTo("+49 123 456789")
        assertThat(lead?.alternativePhoneNumber).isNull()
        assertThat(lead?.notes).isEqualTo(
            """
                Anfrage gestellt: 12.03.2024 09:18
                ID: 136353
                Land: DE
                Einsatzort: Ladenlokal
                Anzahl Personen: 30-50
                Kommentar: Allgemeine Rufnummer: 0333 132321
                """.trimIndent()
        )
    }

    @Test
    fun testImportWithSanitizing() {
        val lead = importer.importLead(
            """
            "Anfrage gestellt";ID;Firma
            "12.03.2024 09:18";136353;"Some	Com		pany 	 GmbH "
            """.trimIndent()
        )

        assertThat(lead).isNotNull()
        assertThat(lead?.companyName).isEqualTo("Some Com pany GmbH")
    }

    @Test
    fun testImportFromLabelValuePairs() {
        val lead = importer.importLead(
            """
            Firma: Test company
            E-Mail: test@example.com
            Straße/Nr.: Teststr. 123
            PLZ: 123456
            Ort: Testcity
            Anrede: Herr
            Vorname: Testfirstname
            Name: Testname
            Telefon: 0123456789
            Einsatzort : Büro & Unternehmen
            Anzahl Personen: 1
            Kommentar: Some long comment with : and with
            newlines
            """.trimIndent()
        )

        assertThat(lead).isNotNull()
        assertThat(lead?.getGender()).isEqualTo(Gender.MR)
        assertThat(lead?.companyName).isEqualTo("Test company")
        assertThat(lead?.firstName).isEqualTo("Testfirstname")
        assertThat(lead?.lastName).isEqualTo("Testname")
        assertThat(lead?.street).isEqualTo("Teststr. 123")
        assertThat(lead?.postCode).isEqualTo("123456")
        assertThat(lead?.city).isEqualTo("Testcity")
        assertThat(lead?.email).isEqualTo("test@example.com")
        assertThat(lead?.phoneNumber).isEqualTo("0123456789")
        assertThat(lead?.alternativePhoneNumber).isNull()
        assertThat(lead?.notes).isEqualTo(
            """
                Einsatzort: Büro & Unternehmen
                Anzahl Personen: 1
                Kommentar: Some long comment with : and with
                newlines
                """.trimIndent()
        )
    }
}