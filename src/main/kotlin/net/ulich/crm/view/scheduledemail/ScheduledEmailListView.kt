package net.ulich.crm.view.scheduledemail

import com.vaadin.flow.component.grid.ItemDoubleClickEvent
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.data.renderer.ComponentRenderer
import com.vaadin.flow.data.renderer.Renderer
import com.vaadin.flow.router.Route
import io.jmix.core.DataManager
import io.jmix.flowui.DialogWindows
import io.jmix.flowui.UiComponents
import io.jmix.flowui.kit.component.button.JmixButton
import io.jmix.flowui.model.CollectionContainer
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

    @Autowired
    private lateinit var uiComponents: UiComponents

    @Autowired
    private lateinit var dataManager: DataManager

    @ViewComponent
    private lateinit var scheduledEmailsDc: CollectionContainer<ScheduledEmail>

    @Subscribe("scheduledEmailsDataGrid")
    private fun onScheduledEmailsDataGridItemDoubleClick(event: ItemDoubleClickEvent<ScheduledEmail>) {
        dialogWindows.detail<Lead, LeadDetailView>(this, Lead::class.java)
            .withViewClass(LeadDetailView::class.java)
            .editEntity(event.item.lead!!)
            .open();
    }

    @Supply(to = "scheduledEmailsDataGrid.complete", subject = "renderer")
    private fun scheduledEmailsDataGridCompleteRenderer(): Renderer<ScheduledEmail> {
        return ComponentRenderer { scheduledEmail ->
            val button = uiComponents.create(JmixButton::class.java)
            button.icon = Icon(VaadinIcon.CHECK)
            button.addClickListener {
                scheduledEmail.isComplete = true
                dataManager.save(scheduledEmail)
                scheduledEmailsDc.mutableItems.remove(scheduledEmail)
            }
            button
        }
    }
}