package net.ulich.crm.lead

import com.opencsv.CSVParser
import com.opencsv.CSVParserBuilder
import com.opencsv.CSVReaderBuilder
import io.jmix.core.DataManager
import net.ulich.crm.entity.Gender
import net.ulich.crm.entity.Lead
import org.springframework.stereotype.Component
import java.io.StringReader

@Component
class LeadCsvImporter(private val dataManager: DataManager) {

    fun importFromCsv(csv: String): Lead? {
        val csvParsers = listOf(
            csvParser(';'),
            csvParser('\t'),
            csvParser(','),
        )

        for (parser in csvParsers) {
            val lead = parse(csv, parser)
            if (lead != null) {
                return lead
            }
        }

        return null
    }

    private fun parse(csv: String, parser: CSVParser): Lead? {
        val lines = CSVReaderBuilder(StringReader(csv))
            .withCSVParser(parser)
            .build()
            .readAll()

        if (lines.size != 2) {
            return null
        }

        val header = lines[0]
        val dataRow = lines[1].map { sanitize(it) }

        if (header.size < 2) {
            return null
        }

        val data = header.zip(dataRow).toMap().toMutableMap()

        return dataManager.create(Lead::class.java).apply {
            setGender(toGender(data))
            companyName = data.remove("Firma")
            firstName = data.remove("Vorname")
            lastName = data.remove("Nachname")
            street = data.remove("Straße")
            postCode = data.remove("PLZ")
            city = data.remove("Stadt")
            email = data.remove("E-Mail") ?: ""
            phoneNumber =
                data.remove("Telefon") ?: data.remove("Rufnummer") ?: data.remove("Mobil") ?: data.remove("Handy")
                        ?: data.remove("Mobiltelefon")
            alternativePhoneNumber =
                data.remove("Alternative Rufnummer") ?: data.remove("Mobil") ?: data.remove("Handy")
                        ?: data.remove("Mobiltelefon")
            notes = data.map { "${it.key}: ${it.value}" }.joinToString("\n")
        }
    }

    private fun sanitize(s: String): String {
        return s.trim()
            .replace(Regex("\\s{1,}"), " ")
    }

    private fun csvParser(separator: Char): CSVParser {
        return CSVParserBuilder()
            .withSeparator(separator)
            .build()
    }

    private fun toGender(data: MutableMap<String, String>): Gender {
        val str = data.get("Anrede")

        val gender = when (str?.uppercase()) {
            "HERR" -> Gender.MR
            "FRAU" -> Gender.MRS
            else -> null
        }

        if (gender != null) {
            data.remove("Anrede")
        }

        return gender ?: Gender.UNKNOWN
    }
}