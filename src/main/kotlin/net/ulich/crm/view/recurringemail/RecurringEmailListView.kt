package net.ulich.crm.view.recurringemail

import com.vaadin.flow.router.Route
import io.jmix.flowui.view.*
import net.ulich.crm.entity.RecurringEmail
import net.ulich.crm.view.main.MainView


@Route(value = "recurringEmails", layout = MainView::class)
@ViewController("RecurringEmail.list")
@ViewDescriptor("recurring-email-list-view.xml")
@LookupComponent("recurringEmailsDataGrid")
@DialogMode(width = "64em")
class RecurringEmailListView : StandardListView<RecurringEmail>() {
}