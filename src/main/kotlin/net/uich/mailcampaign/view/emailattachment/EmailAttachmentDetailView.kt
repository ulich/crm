package net.uich.mailcampaign.view.emailattachment

import net.uich.mailcampaign.entity.EmailAttachment
import net.uich.mailcampaign.view.main.MainView
import com.vaadin.flow.router.Route
import io.jmix.flowui.view.*

@Route(value = "emailAttachments/:id", layout = MainView::class)
@ViewController("EmailAttachment.detail")
@ViewDescriptor("email-attachment-detail-view.xml")
@EditedEntityContainer("emailAttachmentDc")
class EmailAttachmentDetailView : StandardDetailView<EmailAttachment>() {
}