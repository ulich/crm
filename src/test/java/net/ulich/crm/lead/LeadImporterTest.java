package net.ulich.crm.lead;

import io.jmix.core.DataManager;
import net.ulich.crm.entity.Gender;
import net.ulich.crm.entity.Lead;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LeadImporterTest {

    private final DataManager dataManager = mock(DataManager.class);
    private final LeadImporter importer = new LeadImporter(dataManager);

    @BeforeEach
    void setUp() {
        when(dataManager.create(Lead.class)).thenReturn(new Lead());
    }

    @Test
    void testImportFromTabCsv() {
        Lead lead = importer.importLead("""
                Anfrage gestellt\tID\tFirma\tAnrede\tVorname\tNachname\tStraße\tPLZ\tStadt\tLand\tE-Mail\tRufnummer\tAlternative Rufnummer\tMobil\tEinsatzort\tAnzahl Personen\tKommentar
                12.03.2024 09:18\t136353\tSome Company GmbH\tHerr\tMax\tMustermann\tHauptstraße 1\t12345\tBerlin\tDE\tmax.muster@gmbh.de\t+49 123 456789\t+49 987 654321\t+49 174 12345\tLadenlokal\t100+\tAllgemeine Rufnummer: 0333 132321""".stripIndent());

        assertThat(lead).isNotNull();
        assertThat(lead.getGender()).isEqualTo(Gender.MR);
        assertThat(lead.getCompanyName()).isEqualTo("Some Company GmbH");
        assertThat(lead.getFirstName()).isEqualTo("Max");
        assertThat(lead.getLastName()).isEqualTo("Mustermann");
        assertThat(lead.getStreet()).isEqualTo("Hauptstraße 1");
        assertThat(lead.getPostCode()).isEqualTo("12345");
        assertThat(lead.getCity()).isEqualTo("Berlin");
        assertThat(lead.getEmail()).isEqualTo("max.muster@gmbh.de");
        assertThat(lead.getPhoneNumber()).isEqualTo("+49 123 456789");
        assertThat(lead.getAlternativePhoneNumber()).isEqualTo("+49 987 654321");
        assertThat(lead.getNotes()).isEqualTo(
                "Anfrage gestellt: 12.03.2024 09:18\n" +
                "ID: 136353\n" +
                "Land: DE\n" +
                "Mobil: +49 174 12345\n" +
                "Einsatzort: Ladenlokal\n" +
                "Anzahl Personen: 100+\n" +
                "Kommentar: Allgemeine Rufnummer: 0333 132321"
        );
    }

    @Test
    void testImportFromSemicolonCsv() {
        Lead lead = importer.importLead("""
                "Anfrage gestellt";ID;Firma;Vorname;Nachname;Straße;PLZ;Stadt;Land;E-Mail;Telefon;Einsatzort;"Anzahl Personen";Kommentar
                "12.03.2024 09:18";136353;"Some Company GmbH";Max;Mustermann;"Hauptstraße 1";12345;Berlin;DE;max.muster@gmbh.de;"+49 123 456789";Ladenlokal;30-50;"Allgemeine Rufnummer: 0333 132321"
                """.stripIndent());

        assertThat(lead).isNotNull();
        assertThat(lead.getGender()).isNull();
        assertThat(lead.getCompanyName()).isEqualTo("Some Company GmbH");
        assertThat(lead.getFirstName()).isEqualTo("Max");
        assertThat(lead.getLastName()).isEqualTo("Mustermann");
        assertThat(lead.getStreet()).isEqualTo("Hauptstraße 1");
        assertThat(lead.getPostCode()).isEqualTo("12345");
        assertThat(lead.getCity()).isEqualTo("Berlin");
        assertThat(lead.getEmail()).isEqualTo("max.muster@gmbh.de");
        assertThat(lead.getPhoneNumber()).isEqualTo("+49 123 456789");
        assertThat(lead.getAlternativePhoneNumber()).isNull();
        assertThat(lead.getNotes()).isEqualTo(
                "Anfrage gestellt: 12.03.2024 09:18\n" +
                "ID: 136353\n" +
                "Land: DE\n" +
                "Einsatzort: Ladenlokal\n" +
                "Anzahl Personen: 30-50\n" +
                "Kommentar: Allgemeine Rufnummer: 0333 132321"
        );
    }

    @Test
    void testImportWithSanitizing() {
        Lead lead = importer.importLead("""
                "Anfrage gestellt";ID;Firma
                "12.03.2024 09:18";136353;"Some\tCom\t\tpany \t GmbH "
                """.stripIndent());

        assertThat(lead).isNotNull();
        assertThat(lead.getCompanyName()).isEqualTo("Some Com pany GmbH");
    }

    @Test
    void testImportFromLabelValuePairs() {
        Lead lead = importer.importLead("""
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
                """.stripIndent());

        assertThat(lead).isNotNull();
        assertThat(lead.getGender()).isEqualTo(Gender.MR);
        assertThat(lead.getCompanyName()).isEqualTo("Test company");
        assertThat(lead.getFirstName()).isEqualTo("Testfirstname");
        assertThat(lead.getLastName()).isEqualTo("Testname");
        assertThat(lead.getStreet()).isEqualTo("Teststr. 123");
        assertThat(lead.getPostCode()).isEqualTo("123456");
        assertThat(lead.getCity()).isEqualTo("Testcity");
        assertThat(lead.getEmail()).isEqualTo("test@example.com");
        assertThat(lead.getPhoneNumber()).isEqualTo("0123456789");
        assertThat(lead.getAlternativePhoneNumber()).isNull();
        assertThat(lead.getNotes()).isEqualTo(
                "Einsatzort: Büro & Unternehmen\n" +
                "Anzahl Personen: 1\n" +
                "Kommentar: Some long comment with : and with\n" +
                "newlines"
        );
    }
}
