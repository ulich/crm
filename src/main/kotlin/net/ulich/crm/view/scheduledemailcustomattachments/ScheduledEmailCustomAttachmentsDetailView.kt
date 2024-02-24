package net.ulich.crm.view.scheduledemailcustomattachments

import net.ulich.crm.entity.ScheduledEmailCustomAttachments
import net.ulich.crm.view.main.MainView
import com.vaadin.flow.router.Route
import io.jmix.flowui.view.*

@Route(value = "scheduledEmailCustomAttachmentses/:id", layout = MainView::class)
@ViewController("ScheduledEmailCustomAttachments.detail")
@ViewDescriptor("scheduled-email-custom-attachments-detail-view.xml")
@EditedEntityContainer("scheduledEmailCustomAttachmentsDc")
class ScheduledEmailCustomAttachmentsDetailView : StandardDetailView<ScheduledEmailCustomAttachments>() {
}