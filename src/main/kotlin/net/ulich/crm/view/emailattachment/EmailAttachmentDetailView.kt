package net.ulich.crm.view.emailattachment

import net.ulich.crm.entity.EmailAttachment
import net.ulich.crm.view.main.MainView
import com.vaadin.flow.router.Route
import io.jmix.flowui.view.*

@Route(value = "emailAttachments/:id", layout = MainView::class)
@ViewController("EmailAttachment.detail")
@ViewDescriptor("email-attachment-detail-view.xml")
@EditedEntityContainer("emailAttachmentDc")
class EmailAttachmentDetailView : StandardDetailView<EmailAttachment>() {
}