package net.ulich.crm.scheduler

import io.jmix.core.FileRef
import io.jmix.core.FileStorage
import io.jmix.email.EmailAttachment
import io.jmix.email.EmailInfoBuilder
import io.jmix.email.Emailer
import org.apache.commons.io.IOUtils
import org.springframework.stereotype.Service

@Service
class EmailService(
    private val emailer: Emailer,
    private val fileStorage: FileStorage
) {

    fun processQueuedEmails() {
        emailer.processQueuedEmails()
    }

    fun sendEmail(
        toAddress: String,
        subject: String,
        body: String,
        templateVariables: Map<String, String>,
        attachments: List<FileRef>
    ) {
        val finalBody = body.replace("{{salutation}}", templateVariables.get("salutation")!!)

        emailer.sendEmailAsync(
            EmailInfoBuilder.create(toAddress, subject, finalBody)
                .setBodyContentType("text/html")
                .setAttachments(attachments.map(::toAttachment))
                .build()
        )
    }

    private fun toAttachment(file: FileRef): EmailAttachment {
        val stream = fileStorage.openStream(file)
        val fileBytes = IOUtils.toByteArray(stream)
        return EmailAttachment(fileBytes, file.fileName)
    }

}