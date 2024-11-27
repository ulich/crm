package net.ulich.crm.scheduler

import io.jmix.core.DataManager
import net.ulich.crm.entity.*
import org.springframework.stereotype.Service
import java.time.LocalDate


@Service
class LeadEmailService(
    private val emailService: EmailService,
    private val dataManager: DataManager
) {
    fun processQueuedEmails() {
        emailService.processQueuedEmails()
    }

    fun sendEmailToLead(emailTemplate: EmailTemplate, lead: Lead) {
        val toAddress = lead.email!!
        val subject = emailTemplate.subject!!
        val body = emailTemplate.content!!
        val signature = emailTemplate.signature

        val personalization = Personalization(
            salutation = lead.salutation(),
            firstName = lead.firstName,
            lastName = lead.lastName,
            companyName = lead.companyName,
            street = lead.street,
            postCode = lead.postCode,
            city = lead.city,
        )
        emailService.sendPersonalizedEmail(
            toAddress,
            subject,
            body,
            signature?.content,
            emailTemplate.attachments,
            personalization
        )
    }

    fun createRecurringEmail(lead: Lead, recurringEmail: RecurringEmail, fromDate: LocalDate): ScheduledEmail {
        val plannedSendDate = recurringEmail.calculateNextOcurrenceFrom(fromDate)

        return dataManager.create(ScheduledEmail::class.java).apply {
            this.lead = lead
            this.emailTemplate = recurringEmail.emailTemplate
            this.plannedSendDate = plannedSendDate.toOffsetDateTime()
            this.recurringEmail = recurringEmail
            this.setSourceType(ScheduledEmailSourceType.PRODUCT_ADD_ON_REMINDER)
        }
    }
}