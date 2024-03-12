package net.ulich.crm.scheduler

import io.jmix.core.FileRef
import io.jmix.core.FileStorage
import io.jmix.email.EmailInfoBuilder
import io.jmix.email.Emailer
import net.ulich.crm.entity.EmailAttachment
import org.apache.commons.io.IOUtils
import org.springframework.stereotype.Service
import io.jmix.email.EmailAttachment as JmixEmailAttachment

@Service
class EmailService(
    private val emailer: Emailer,
    private val fileStorage: FileStorage,
    private val attachmentPersonalizer: AttachmentPersonalizer
) {

    fun processQueuedEmails() {
        emailer.processQueuedEmails()
    }

    fun sendPersonalizedEmail(
        toAddress: String,
        subject: String,
        body: String,
        attachments: List<EmailAttachment>,
        personalization: Personalization
    ) {
        val finalBody = body.replace("{{salutation}}", personalization.salutation)
        val attachmentFiles = prepareAttachments(attachments, personalization)

        sendEmail(toAddress, subject, finalBody, attachmentFiles)
    }

    fun sendEmail(
        toAddress: String,
        subject: String,
        body: String,
        attachments: List<FileRef>
    ) {
        emailer.sendEmailAsync(
            EmailInfoBuilder.create(toAddress, subject, body)
                .setBodyContentType("text/html")
                .setAttachments(attachments.map(::toJmixAttachment))
                .build()
        )
    }

    private fun prepareAttachments(
        attachments: List<EmailAttachment>,
        personalization: Personalization
    ): List<FileRef> {
        return attachments.map(fun(it: EmailAttachment): FileRef {
            if (it.personalized!!) {
                return attachmentPersonalizer.personalize(it.file!!, personalization)
            } else {
                return it.file!!
            }
        })
    }

    private fun toJmixAttachment(file: FileRef): JmixEmailAttachment {
        val stream = fileStorage.openStream(file)
        val fileBytes = IOUtils.toByteArray(stream)
        return JmixEmailAttachment(fileBytes, file.fileName)
    }

}