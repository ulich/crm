package net.ulich.crm.entity

import io.jmix.core.metamodel.datatype.EnumClass

enum class ScheduledEmailSourceType(private val id: String) : EnumClass<String> {
    CAMPAIGN("CAMPAIGN"),
    PRODUCT_ADD_ON_REMINDER("PRODUCT_ADD_ON_REMINDER");

    override fun getId() = id

    companion object {

        @JvmStatic
        fun fromId(id: String?): ScheduledEmailSourceType? = ScheduledEmailSourceType.values().find { it.id == id }
    }
}