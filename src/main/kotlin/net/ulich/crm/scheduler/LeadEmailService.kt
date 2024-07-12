package net.ulich.crm.scheduler

import net.ulich.crm.entity.EmailTemplate
import net.ulich.crm.entity.Lead
import org.springframework.stereotype.Service


@Service
class LeadEmailService(
    private val emailService: EmailService,
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
}