package net.ulich.crm.view.lead

import io.jmix.core.Messages
import io.jmix.flowui.component.validation.ValidationErrors
import net.ulich.crm.entity.Gender
import net.ulich.crm.entity.Lead
import org.springframework.stereotype.Component

@Component
class LeadValidator(private val messages: Messages) {

    fun validate(lead: Lead): ValidationErrors {
        val errors = ValidationErrors()

        if (notAllSetOrNone(lead.street, lead.postCode, lead.city)) {
            errors.add(messages.getMessage(LeadValidator::class.java, "validation.address"))
        }

        if (lead.lastName.isNullOrBlank()) {
            if (lead.companyName.isNullOrBlank()) {
                errors.add(messages.getMessage(LeadValidator::class.java, "validation.lastNameOrCompanyName"))
            } else if (lead.getGender() != Gender.UNKNOWN) {
                errors.add(messages.getMessage(LeadValidator::class.java, "validation.unknownGenderForCompany"))
            }
        }

        return errors
    }

    private fun notAllSetOrNone(vararg values: String?): Boolean {
        return values.any { it.isNullOrBlank() } && values.any { !it.isNullOrBlank() }
    }
}