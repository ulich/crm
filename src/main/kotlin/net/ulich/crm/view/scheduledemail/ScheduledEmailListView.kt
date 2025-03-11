package net.ulich.crm.view.scheduledemail

import com.vaadin.flow.component.grid.ItemDoubleClickEvent
import com.vaadin.flow.router.Route
import io.jmix.flowui.DialogWindows
import io.jmix.flowui.view.*
import net.ulich.crm.entity.Lead
import net.ulich.crm.entity.ScheduledEmail
import net.ulich.crm.view.lead.LeadDetailView
import net.ulich.crm.view.main.MainView
import org.springframework.beans.factory.annotation.Autowired


@Route(value = "scheduledEmails", layout = MainView::class)
@ViewController("ScheduledEmail.list")
@ViewDescriptor("scheduled-email-list-view.xml")
@LookupComponent("scheduledEmailsDataGrid")
@DialogMode(width = "64em")
class ScheduledEmailListView : StandardListView<ScheduledEmail>() {

    @Autowired
    private lateinit var dialogWindows: DialogWindows

    @Subscribe("scheduledEmailsDataGrid")
    private fun onScheduledEmailsDataGridItemDoubleClick(event: ItemDoubleClickEvent<ScheduledEmail>) {
        dialogWindows.detail<Lead, LeadDetailView>(this, Lead::class.java)
            .withViewClass(LeadDetailView::class.java)
            .editEntity(event.item.lead!!)
            .open();
    }
}