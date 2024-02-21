package net.uich.mailcampaign.view.scheduledemailcustomattachments

import net.uich.mailcampaign.entity.ScheduledEmailCustomAttachments
import net.uich.mailcampaign.view.main.MainView
import com.vaadin.flow.router.Route
import io.jmix.flowui.view.*

@Route(value = "scheduledEmailCustomAttachmentses/:id", layout = MainView::class)
@ViewController("ScheduledEmailCustomAttachments.detail")
@ViewDescriptor("scheduled-email-custom-attachments-detail-view.xml")
@EditedEntityContainer("scheduledEmailCustomAttachmentsDc")
class ScheduledEmailCustomAttachmentsDetailView : StandardDetailView<ScheduledEmailCustomAttachments>() {
}