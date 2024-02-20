package net.uich.mailcampaign.scheduler

import io.jmix.core.DataManager
import io.jmix.core.FetchPlan
import io.jmix.core.FileStorage
import io.jmix.core.security.SystemAuthenticator
import io.jmix.email.EmailInfoBuilder
import io.jmix.email.Emailer
import net.uich.mailcampaign.entity.EmailAttachment
import net.uich.mailcampaign.entity.ScheduledEmail
import org.apache.commons.io.IOUtils
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.OffsetDateTime
import java.util.concurrent.TimeUnit

@Component
class Scheduler(
        private val dataManager: DataManager,
        private val systemAuthenticator: SystemAuthenticator,
        private val emailer: Emailer,
        private val fileStorage: FileStorage) {

    private val log = LoggerFactory.getLogger(Scheduler::class.java)

    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.SECONDS)
    //@Scheduled(fixedRate = 1, timeUnit = TimeUnit.MINUTES)
    fun processQueuedEmails() {
        emailer.processQueuedEmails()
    }

    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.SECONDS)
    //@Scheduled(fixedRate = 5, timeUnit = TimeUnit.MINUTES)
    fun schedule() {
        systemAuthenticator.withSystem(::start)
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
        val attachments = email.emailTemplate?.attachments!!

        val finalBody = body.replace("{{salutation}}", email.lead?.salutation()!!)

        emailer.sendEmailAsync(EmailInfoBuilder.create(toAddress, subject, finalBody)
                .setBodyContentType("text/html")
                .setAttachments(attachments.map(::toAttachment))
                .build())

        dataManager.save(email.apply {
            this.sentDate = OffsetDateTime.now()
        })
    }

    private fun toAttachment(it: EmailAttachment): io.jmix.email.EmailAttachment {
        val stream = fileStorage.openStream(it.file!!)
        val fileBytes = IOUtils.toByteArray(stream)
        return io.jmix.email.EmailAttachment(fileBytes, it.file?.fileName!!)
    }
}