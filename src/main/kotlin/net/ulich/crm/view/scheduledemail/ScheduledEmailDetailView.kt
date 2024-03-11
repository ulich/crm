package net.ulich.crm.view.scheduledemail

import com.vaadin.flow.router.Route
import io.jmix.flowui.view.EditedEntityContainer
import io.jmix.flowui.view.StandardDetailView
import io.jmix.flowui.view.ViewController
import io.jmix.flowui.view.ViewDescriptor
import net.ulich.crm.entity.ScheduledEmail
import net.ulich.crm.view.main.MainView

@Route(value = "scheduledEmails/:id", layout = MainView::class)
@ViewController("ScheduledEmail.detail")
@ViewDescriptor("scheduled-email-detail-view.xml")
@EditedEntityContainer("scheduledEmailDc")
class ScheduledEmailDetailView : StandardDetailView<ScheduledEmail>() {
}