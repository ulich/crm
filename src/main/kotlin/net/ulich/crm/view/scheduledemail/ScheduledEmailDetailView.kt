package net.ulich.crm.view.scheduledemail

import com.vaadin.flow.data.renderer.ComponentRenderer
import com.vaadin.flow.data.renderer.Renderer
import com.vaadin.flow.data.renderer.TextRenderer
import net.ulich.crm.entity.ScheduledEmail
import net.ulich.crm.view.main.MainView
import com.vaadin.flow.router.Route
import io.jmix.flowui.view.*
import net.ulich.crm.entity.ScheduledEmailCustomAttachments

@Route(value = "scheduledEmails/:id", layout = MainView::class)
@ViewController("ScheduledEmail.detail")
@ViewDescriptor("scheduled-email-detail-view.xml")
@EditedEntityContainer("scheduledEmailDc")
class ScheduledEmailDetailView : StandardDetailView<ScheduledEmail>() {

    @Supply(to = "customAttachmentsDataGrid.file", subject = "renderer")
    private fun customAttachmentsDataGridFileRenderer(): Renderer<ScheduledEmailCustomAttachments> {
        return TextRenderer { it.file?.fileName }
    }

}