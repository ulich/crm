package net.ulich.crm.email;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class RecipientCsvParser {

    public List<Recipient> parse(String csv) {
        String[] lines = csv.split("\\n");
        if (lines.length < 2) {
            return null;
        }

        String[] header = lines[0].split("\\t", -1);
        List<String> headerList = List.of(header);
        if (!headerList.containsAll(List.of("Anrede", "Titel", "Vorname", "Nachname", "E-Mailadresse"))) {
            return null;
        }

        List<Recipient> recipients = new ArrayList<>();
        for (int i = 1; i < lines.length; i++) {
            String[] dataRow = lines[i].split("\\t", -1);
            if (header.length != dataRow.length) {
                return null;
            }

            Map<String, String> record = new HashMap<>();
            for (int j = 0; j < header.length; j++) {
                record.put(header[j], dataRow[j]);
            }

            String email = record.get("E-Mailadresse");
            if (email == null || email.trim().isEmpty()) {
                continue;
            }

            recipients.add(new Recipient(
                    record.getOrDefault("Anrede", "").trim(),
                    record.getOrDefault("Titel", "").trim(),
                    record.getOrDefault("Vorname", "").trim(),
                    record.getOrDefault("Nachname", "").trim(),
                    email.trim()
            ));
        }

        return recipients;
    }

    public static class Recipient {
        private final String gender;
        private final String title;
        private final String firstName;
        private final String lastName;
        private final String email;

        public Recipient(String gender, String title, String firstName, String lastName, String email) {
            this.gender = gender;
            this.title = title;
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
        }

        public String getGender() {
            return gender;
        }

        public String getTitle() {
            return title;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public String getEmail() {
            return email;
        }

        public String salutation() {
            if ("Herr".equals(gender)) {
                return "Sehr geehrter Herr " + title + " " + lastName;
            } else if ("Frau".equals(gender)) {
                return "Sehr geehrte Frau " + title + " " + lastName;
            }
            return "Sehr geehrte Damen und Herren";
        }
    }
}
