package net.ulich.crm.view.lead;

import io.jmix.core.Messages;
import io.jmix.flowui.component.validation.ValidationErrors;
import net.ulich.crm.entity.Gender;
import net.ulich.crm.entity.Lead;
import org.springframework.stereotype.Component;

@Component
public class LeadValidator {
    private final Messages messages;

    public LeadValidator(Messages messages) {
        this.messages = messages;
    }

    public ValidationErrors validate(Lead lead) {
        ValidationErrors errors = new ValidationErrors();

        if (notAllSetOrNone(lead.getStreet(), lead.getPostCode(), lead.getCity())) {
            errors.add(messages.getMessage(LeadValidator.class, "validation.address"));
        }

        if (lead.getLastName() == null || lead.getLastName().isBlank()) {
            if (lead.getCompanyName() == null || lead.getCompanyName().isBlank()) {
                errors.add(messages.getMessage(LeadValidator.class, "validation.lastNameOrCompanyName"));
            } else if (lead.getGender() != Gender.UNKNOWN) {
                errors.add(messages.getMessage(LeadValidator.class, "validation.unknownGenderForCompany"));
            }
        }

        return errors;
    }

    private boolean notAllSetOrNone(String... values) {
        boolean hasBlank = false;
        boolean hasNonBlank = false;

        for (String value : values) {
            if (value == null || value.isBlank()) {
                hasBlank = true;
            } else {
                hasNonBlank = true;
            }
        }

        return hasBlank && hasNonBlank;
    }
}
