package net.ulich.crm.scheduler;

import io.jmix.core.DataManager;
import net.ulich.crm.entity.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class LeadEmailService {
    private final EmailService emailService;
    private final DataManager dataManager;

    public LeadEmailService(EmailService emailService, DataManager dataManager) {
        this.emailService = emailService;
        this.dataManager = dataManager;
    }

    public void processQueuedEmails() {
        emailService.processQueuedEmails();
    }

    public void sendEmailToLead(EmailTemplate emailTemplate, Lead lead) {
        String toAddress = lead.getEmail();
        String subject = emailTemplate.getSubject();
        String body = emailTemplate.getContent();

        Personalization personalization = new Personalization(
                lead.getCompanyName(),
                lead.salutation(),
                lead.getFirstName(),
                lead.getLastName(),
                lead.getStreet(),
                lead.getPostCode(),
                lead.getCity()
        );

        emailService.sendPersonalizedEmail(
                toAddress,
                subject,
                body,
                emailTemplate.getSignature() != null ? emailTemplate.getSignature().getContent() : null,
                emailTemplate.getAttachments(),
                personalization
        );
    }

    public ScheduledEmail createRecurringEmail(Lead lead, RecurringEmail recurringEmail, LocalDate fromDate) {
        var plannedSendDate = recurringEmail.calculateNextOcurrenceFrom(fromDate);

        ScheduledEmail scheduledEmail = dataManager.create(ScheduledEmail.class);
        scheduledEmail.setLead(lead);
        scheduledEmail.setEmailTemplate(recurringEmail.getEmailTemplate());
        scheduledEmail.setPlannedSendDate(plannedSendDate.toOffsetDateTime());
        scheduledEmail.setRecurringEmail(recurringEmail);
        scheduledEmail.setSourceType(ScheduledEmailSourceType.PRODUCT_ADD_ON_REMINDER);

        return scheduledEmail;
    }
}
