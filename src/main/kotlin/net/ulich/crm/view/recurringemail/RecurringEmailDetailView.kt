package net.ulich.crm.view.recurringemail

import com.vaadin.flow.router.Route
import io.jmix.flowui.view.EditedEntityContainer
import io.jmix.flowui.view.StandardDetailView
import io.jmix.flowui.view.ViewController
import io.jmix.flowui.view.ViewDescriptor
import net.ulich.crm.entity.RecurringEmail
import net.ulich.crm.view.main.MainView

@Route(value = "recurringEmails/:id", layout = MainView::class)
@ViewController("RecurringEmail.detail")
@ViewDescriptor("recurring-email-detail-view.xml")
@EditedEntityContainer("recurringEmailDc")
class RecurringEmailDetailView : StandardDetailView<RecurringEmail>() {
}