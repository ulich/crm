package net.ulich.crm.view.lead

import com.vaadin.flow.component.Component
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
import io.jmix.flowui.kit.action.ActionPerformedEvent
import io.jmix.flowui.view.*
import net.ulich.crm.entity.Lead
import net.ulich.crm.lead.LeadCsvImporter
import net.ulich.crm.view.main.MainView
import org.springframework.beans.factory.annotation.Autowired

@Route(value = "leads", layout = MainView::class)
@ViewController("Lead.list")
@ViewDescriptor("lead-list-view.xml")
@LookupComponent("leadsDataGrid")
@DialogMode(width = "64em")
class LeadListView : StandardListView<Lead>() {
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
}