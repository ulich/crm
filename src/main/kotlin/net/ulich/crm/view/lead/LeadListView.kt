package net.ulich.crm.view.lead

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.checkbox.Checkbox
import com.vaadin.flow.component.textfield.TextArea
import com.vaadin.flow.router.Route
import io.jmix.core.Messages
import io.jmix.flowui.Dialogs
import io.jmix.flowui.Notifications
import io.jmix.flowui.ViewNavigators
import io.jmix.flowui.app.inputdialog.DialogActions
import io.jmix.flowui.app.inputdialog.DialogOutcome
import io.jmix.flowui.app.inputdialog.InputDialog
import io.jmix.flowui.app.inputdialog.InputParameter
import io.jmix.flowui.component.grid.DataGrid
import io.jmix.flowui.kit.action.ActionPerformedEvent
import io.jmix.flowui.model.CollectionContainer
import io.jmix.flowui.model.CollectionLoader
import io.jmix.flowui.view.*
import net.ulich.crm.entity.EmailTemplate
import net.ulich.crm.entity.Lead
import net.ulich.crm.lead.LeadCsvImporter
import net.ulich.crm.scheduler.LeadEmailService
import net.ulich.crm.view.main.MainView
import org.springframework.beans.factory.annotation.Autowired

@Route(value = "leads", layout = MainView::class)
@ViewController("Lead.list")
@ViewDescriptor("lead-list-view.xml")
@LookupComponent("leadsDataGrid")
@DialogMode(width = "64em")
class LeadListView : StandardListView<Lead>() {
    @ViewComponent
    private lateinit var leadsDataGrid: DataGrid<Lead>

    @ViewComponent
    private lateinit var leadsDl: CollectionLoader<Lead>

    @ViewComponent
    private lateinit var leadsDc: CollectionContainer<Lead>

    @Autowired
    private lateinit var dialogs: Dialogs

    @Autowired
    private lateinit var messages: Messages

    @Autowired
    private lateinit var leadCsvImporter: LeadCsvImporter

    @Autowired
    private lateinit var notifications: Notifications

    @Autowired
    private lateinit var viewNavigators: ViewNavigators

    @Autowired
    private lateinit var leadEmailService: LeadEmailService

    @Subscribe("leadsDataGrid.createFromCsv")
    private fun onLeadsDataGridCreateFromCsv(event: ActionPerformedEvent) {
        dialogs.createInputDialog(this)
            .withHeader(messages.getMessage(this.javaClass, "createFromCsvHeader"))
            .withLabelsPosition(Dialogs.InputDialogBuilder.LabelsPosition.TOP)
            .withParameters(
                InputParameter.stringParameter("csv")
                    .withField(::textArea)
                    .withLabel(messages.getMessage(this.javaClass, "csvLabel"))
            )
            .withActions(DialogActions.OK_CANCEL)
            .withCloseListener(::csvDialogClosed)
            .open()
    }

    private fun csvDialogClosed(event: InputDialog.InputDialogCloseEvent?) {
        if (event == null || !event.closedWith(DialogOutcome.OK)) {
            return
        }

        val csv = event.getValue<String>("csv")
        val lead = leadCsvImporter.importFromCsv(csv!!)
        if (lead == null) {
            notifications.create(messages.getMessage(this.javaClass, "csvImportFailed"))
                .withType(Notifications.Type.ERROR)
                .show()
            return
        }

        viewNavigators.detailView(Lead::class.java)
            .newEntity()
            .withViewClass(LeadDetailView::class.java)
            .withAfterNavigationHandler { it.view.prefillWith(lead) }
            .navigate()
    }

    private fun textArea(): Component {
        val field = TextArea()
        field.width = "100%"
        return field
    }

    @Subscribe("leadsDataGrid.sendMail")
    private fun onLeadsDataGridSendMail(event: ActionPerformedEvent) {
        val parameters = mutableListOf(
            InputParameter.entityParameter("emailTemplate", EmailTemplate::class.java)
                .withLabel(messages.getMessage(this.javaClass, "emailTemplateLabel"))
                .withRequired(true),
        )
        if (leadsDataGrid.selectedItems.size == leadsDataGrid.pageSize) {
            parameters.add(
                InputParameter.booleanParameter("sendToAll")
                    .withField {
                        Checkbox().apply {
                            label = messages.getMessage(LeadDetailView::class.java, "sendToAllLabel")
                            value = true
                        }
                    }
                    .withDefaultValue(true)
            )
        }

        dialogs.createInputDialog(this)
            .withHeader(messages.getMessage(this.javaClass, "sendMailHeader"))
            .withLabelsPosition(Dialogs.InputDialogBuilder.LabelsPosition.TOP)
            .withParameters(*parameters.toTypedArray())
            .withActions(DialogActions.OK_CANCEL)
            .withCloseListener(::sendEmailDialogClosed)
            .open()
    }

    private fun sendEmailDialogClosed(event: InputDialog.InputDialogCloseEvent?) {
        if (event == null || !event.closedWith(DialogOutcome.OK)) {
            return
        }

        val emailTemplate = event.getValue<EmailTemplate>("emailTemplate")
        val sendToAll = event.getValue<Boolean>("sendToAll") ?: false

        var leads = leadsDataGrid.selectedItems.toList()

        val oldMaxResults = leadsDl.maxResults
        if (sendToAll) {
            leadsDl.maxResults = 1000000
            leadsDl.load()
            leads = leadsDc.items
        }

        try {
            leads.forEach {
                leadEmailService.sendEmailToLead(emailTemplate!!, it)
            }
            leadEmailService.processQueuedEmails()

            notifications.create(messages.formatMessage(this.javaClass, "sendMailSuccess", leads.size))
                .withType(Notifications.Type.SUCCESS)
                .show()
        } finally {
            if (sendToAll) {
                leadsDl.maxResults = oldMaxResults
                leadsDl.load()
            }
        }
    }
}