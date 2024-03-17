package net.ulich.crm.scheduler

import io.jmix.core.DataManager
import io.jmix.core.FetchPlan
import io.jmix.core.security.SystemAuthenticator
import net.ulich.crm.entity.ScheduledEmail
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.OffsetDateTime
import java.util.concurrent.TimeUnit

@Component
class Scheduler(
    private val dataManager: DataManager,
    private val systemAuthenticator: SystemAuthenticator,
    private val emailService: EmailService,
) {

    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.MINUTES)
    fun schedule() {
        systemAuthenticator.withSystem(::start)
        emailService.processQueuedEmails()
    }

    private fun start() {
        val emailsToBeSent = dataManager.load(ScheduledEmail::class.java)
            .query("select s from ScheduledEmail s where s.sentDate is null and s.plannedSendDate < :now")
            .fetchPlan {
                it.addFetchPlan(FetchPlan.BASE)
                    .add("lead", FetchPlan.BASE)
                    .add("emailTemplate", FetchPlan.BASE)
                    .add("emailTemplate.attachments", FetchPlan.BASE)
            }
            .parameter("now", OffsetDateTime.now())
            .maxResults(2)
            .list()

        emailsToBeSent.forEach(::handleEmail)
    }

    private fun handleEmail(email: ScheduledEmail) {
        val toAddress = email.lead?.email!!
        val subject = email.emailTemplate?.subject!!
        val body = email.emailTemplate?.content!!
        val signature = email.emailTemplate?.signature

        val personalization = Personalization(
            salutation = email.lead?.salutation()!!,
            firstName = email.lead?.firstName,
            lastName = email.lead?.lastName,
            companyName = email.lead?.companyName,
            street = email.lead?.street,
            postCode = email.lead?.postCode,
            city = email.lead?.city,
        )
        emailService.sendPersonalizedEmail(
            toAddress,
            subject,
            body,
            signature?.content,
            email.emailTemplate!!.attachments,
            personalization
        )

        dataManager.save(email.apply {
            this.sentDate = OffsetDateTime.now()
        })
    }


}