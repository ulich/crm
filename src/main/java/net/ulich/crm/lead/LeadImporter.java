package net.ulich.crm.lead;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReaderBuilder;
import io.jmix.core.DataManager;
import net.ulich.crm.entity.Gender;
import net.ulich.crm.entity.Lead;
import org.springframework.stereotype.Component;

import java.io.StringReader;
import java.util.*;
import java.util.regex.Pattern;

@Component
public class LeadImporter {
    private static final Pattern MULTI_SPACE = Pattern.compile("\\s{1,}");

    private final DataManager dataManager;

    public LeadImporter(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    public Lead importLead(String text) {
        List<CSVParser> csvParsers = List.of(csvParser(';'), csvParser('\t'), csvParser(','));

        for (CSVParser parser : csvParsers) {
            Lead lead = parseCsv(text, parser);
            if (lead != null) {
                return lead;
            }
        }

        return parseLabelValuePairs(text);
    }

    private Lead parseCsv(String csv, CSVParser parser) {
        List<String[]> lines;
        try {
            lines = new CSVReaderBuilder(new StringReader(csv))
                    .withCSVParser(parser)
                    .build()
                    .readAll();
        } catch (Exception e) {
            return null;
        }

        if (lines.size() != 2) {
            return null;
        }

        String[] header = lines.get(0);
        List<String> dataRow = new ArrayList<>();
        for (String value : lines.get(1)) {
            dataRow.add(sanitize(value));
        }

        if (header.length < 2) {
            return null;
        }

        Map<String, String> data = new LinkedHashMap<>();
        for (int i = 0; i < header.length && i < dataRow.size(); i++) {
            data.put(header[i], dataRow.get(i));
        }

        return createLeadFromMap(data);
    }

    private Lead parseLabelValuePairs(String text) {
        Map<String, String> data = new LinkedHashMap<>();

        for (String line : text.lines().toList()) {
            String[] parts = line.split(":", 2);
            String key = sanitize(parts[0]);
            if (parts.length == 2) {
                data.put(key, sanitize(parts[1]));
            } else {
                data.put(key, "");
            }
        }

        if (data.isEmpty()) {
            return null;
        }

        return createLeadFromMap(data);
    }

    private Lead createLeadFromMap(Map<String, String> data) {
        Lead lead = dataManager.create(Lead.class);

        lead.setGender(toGender(data));
        lead.setCompanyName(data.remove("Firma"));
        lead.setFirstName(data.remove("Vorname"));

        String lastName = data.remove("Name");
        if (lastName == null) {
            lastName = data.remove("Nachname");
        }
        lead.setLastName(lastName);

        String street = data.remove("Straße/Nr.");
        if (street == null) {
            street = data.remove("Straße");
        }
        lead.setStreet(street);

        lead.setPostCode(data.remove("PLZ"));

        String city = data.remove("Ort");
        if (city == null) {
            city = data.remove("Stadt");
        }
        lead.setCity(city);

        lead.setEmail(data.remove("E-Mail"));

        String phone = data.remove("Telefon");
        if (phone == null) {
            phone = data.remove("Rufnummer");
        }
        if (phone == null) {
            phone = data.remove("Mobil");
        }
        if (phone == null) {
            phone = data.remove("Handy");
        }
        if (phone == null) {
            phone = data.remove("Mobiltelefon");
        }
        lead.setPhoneNumber(phone);

        String altPhone = data.remove("Alternative Rufnummer");
        if (altPhone == null) {
            altPhone = data.remove("Mobil");
        }
        if (altPhone == null) {
            altPhone = data.remove("Handy");
        }
        if (altPhone == null) {
            altPhone = data.remove("Mobiltelefon");
        }
        lead.setAlternativePhoneNumber(altPhone);

        List<String> notes = new ArrayList<>();
        for (Map.Entry<String, String> entry : data.entrySet()) {
            if (!entry.getValue().isEmpty()) {
                notes.add(entry.getKey() + ": " + entry.getValue());
            } else {
                notes.add(entry.getKey());
            }
        }
        lead.setNotes(String.join("\n", notes));

        return lead;
    }

    private String sanitize(String s) {
        return MULTI_SPACE.matcher(s.trim()).replaceAll(" ");
    }

    private CSVParser csvParser(char separator) {
        return new CSVParserBuilder()
                .withSeparator(separator)
                .build();
    }

    private Gender toGender(Map<String, String> data) {
        String str = data.get("Anrede");
        if (str == null) {
            return null;
        }

        Gender gender;
        switch (str.toUpperCase(Locale.ROOT)) {
            case "HERR" -> gender = Gender.MR;
            case "FRAU" -> gender = Gender.MRS;
            default -> gender = null;
        }

        if (gender != null) {
            data.remove("Anrede");
        }

        return gender;
    }
}
