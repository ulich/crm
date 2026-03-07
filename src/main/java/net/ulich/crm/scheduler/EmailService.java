package net.ulich.crm.scheduler;

import io.jmix.core.FileRef;
import io.jmix.core.FileStorage;
import io.jmix.email.EmailInfoBuilder;
import io.jmix.email.Emailer;
import net.ulich.crm.entity.EmailAttachment;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class EmailService {
    private final Emailer emailer;
    private final FileStorage fileStorage;
    private final AttachmentPersonalizer attachmentPersonalizer;

    public EmailService(Emailer emailer, FileStorage fileStorage, AttachmentPersonalizer attachmentPersonalizer) {
        this.emailer = emailer;
        this.fileStorage = fileStorage;
        this.attachmentPersonalizer = attachmentPersonalizer;
    }

    public void processQueuedEmails() {
        emailer.processQueuedEmails();
    }

    public void sendPersonalizedEmail(
            String toAddress,
            String subject,
            String body,
            String signature,
            List<EmailAttachment> attachments,
            Personalization personalization
    ) {
        String finalBody = body.replace("{{salutation}}", personalization.getSalutation()) + (signature == null ? "" : signature);
        List<FileRef> attachmentFiles = prepareAttachments(attachments, personalization);
        sendEmail(toAddress, subject, finalBody, attachmentFiles);
    }

    public void sendEmail(String toAddress, String subject, String body, List<FileRef> attachments) {
        var jmixAttachments = attachments.stream()
                .map(this::toJmixAttachment)
                .toList();

        emailer.sendEmailAsync(
                EmailInfoBuilder.create(toAddress, subject, EmailHtmlEncoder.encode(body))
                        .setBodyContentType("text/html")
                        .setAttachments(jmixAttachments)
                        .build()
        );
    }

    private List<FileRef> prepareAttachments(List<EmailAttachment> attachments, Personalization personalization) {
        List<FileRef> result = new ArrayList<>();
        for (EmailAttachment attachment : attachments) {
            if (Boolean.TRUE.equals(attachment.getPersonalized())) {
                result.add(attachmentPersonalizer.personalize(attachment.getFile(), personalization));
            } else {
                result.add(attachment.getFile());
            }
        }
        return result;
    }

    private io.jmix.email.EmailAttachment toJmixAttachment(FileRef file) {
        try (var stream = fileStorage.openStream(file)) {
            byte[] fileBytes = IOUtils.toByteArray(stream);
            return new io.jmix.email.EmailAttachment(fileBytes, file.getFileName());
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read attachment file: " + file, e);
        }
    }
}
