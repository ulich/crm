package net.ulich.crm.entity

import io.jmix.core.metamodel.datatype.EnumClass

enum class LeadStatus(private val id: String) : EnumClass<String> {
    NEW("NEW"),
    DECISION_EXPECTED_SOON("DECISION_EXPECTED_SOON"),
    DECISION_EXPECTED_LATER("DECISION_EXPECTED_LATER"),
    SUCCESS("SUCCESS"),
    CANCELED("CANCELED");

    override fun getId() = id

    companion object {

        @JvmStatic
        fun fromId(id: String): LeadStatus? = LeadStatus.values().find { it.id == id }
    }
}