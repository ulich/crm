package net.uich.mailcampaign.entity

import io.jmix.core.metamodel.datatype.EnumClass

enum class Gender(private val id: String) : EnumClass<String> {
    MR("MR"),
    MRS("MRS"),
    UNKNOWN("UNKNOWN");

    override fun getId() = id

    companion object {

        @JvmStatic
        fun fromId(id: String): Gender? = Gender.values().find { it.id == id }
    }
}