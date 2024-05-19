package net.ulich.crm.lead

import io.jmix.core.DataManager
import net.ulich.crm.entity.Lead
import org.springframework.stereotype.Component

@Component
class LeadCsvImporter(private val dataManager: DataManager) {

    fun importFromCsv(csv: String): Lead? {
        val lines = csv.split("\n")
        if (lines.size != 2) {
            return null
        }

        val header = lines[0].split("\t")
        val dataRow = lines[1].split("\t")

        if (header.size != dataRow.size) {
            return null
        }

        val data = header.zip(dataRow).toMap().toMutableMap()

        return dataManager.create(Lead::class.java).apply {
            companyName = data.remove("Firma")
            firstName = data.remove("Vorname")
            lastName = data.remove("Nachname")
            street = data.remove("Straße")
            postCode = data.remove("PLZ")
            city = data.remove("Stadt")
            email = data.remove("E-Mail")
            phoneNumber =
                data.remove("Telefon") ?: data.remove("Rufnummer") ?: data.remove("Mobil") ?: data.remove("Handy")
                        ?: data.remove("Mobiltelefon")
            alternativePhoneNumber =
                data.remove("Alternative Rufnummer") ?: data.remove("Mobil") ?: data.remove("Handy")
                        ?: data.remove("Mobiltelefon")
            notes = data.map { "${it.key}: ${it.value}" }.joinToString("\n")
        }
    }
}