package net.ulich.crm.email

import org.springframework.stereotype.Component

@Component
class RecipientCsvParser {

    fun parse(csv: String): List<Recipient>? {
        val lines = csv.split("\n")
        if (lines.size < 2) {
            return null
        }

        val header = lines[0].split("\t")
        if (!header.containsAll(listOf("Anrede", "Titel", "Vorname", "Nachname", "E-Mailadresse"))) {
            return null
        }

        val dataLines = lines.drop(1)
        return dataLines.map { line ->
            val dataRow = line.split("\t")

            if (header.size != dataRow.size) {
                return null
            }

            val record = header.zip(dataRow).toMap()

            if (record["E-Mailadresse"]!!.trim().isEmpty()) {
                null
            } else {
                Recipient(
                    record["Anrede"]!!.trim(),
                    record["Titel"]!!.trim(),
                    record["Vorname"]!!.trim(),
                    record["Nachname"]!!.trim(),
                    record["E-Mailadresse"]!!.trim()
                )
            }
        }.filterNotNull()
    }

    class Recipient(
        val gender: String,
        val title: String,
        val firstName: String,
        val lastName: String,
        val email: String
    ) {

        fun salutation(): String {
            if (gender == "Herr") {
                return "Sehr geehrter Herr $title $lastName"
            } else if (gender == "Frau") {
                return "Sehr geehrte Frau $title $lastName"
            } else {
                return "Sehr geehrte Damen und Herren"
            }
        }
    }
}