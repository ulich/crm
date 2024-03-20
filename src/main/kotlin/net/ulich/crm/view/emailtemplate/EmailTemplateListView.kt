package net.ulich.crm.view.emailtemplate

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.notification.NotificationVariant
import com.vaadin.flow.component.textfield.TextArea
import com.vaadin.flow.router.Route
import io.jmix.flowui.Dialogs
import io.jmix.flowui.Notifications
import io.jmix.flowui.app.inputdialog.DialogActions
import io.jmix.flowui.app.inputdialog.DialogOutcome
import io.jmix.flowui.app.inputdialog.InputDialog
import io.jmix.flowui.app.inputdialog.InputParameter
import io.jmix.flowui.component.grid.DataGrid
import io.jmix.flowui.kit.action.ActionPerformedEvent
import io.jmix.flowui.view.*
import net.ulich.crm.email.RecipientCsvParser
import net.ulich.crm.entity.EmailTemplate
import net.ulich.crm.scheduler.EmailService
import net.ulich.crm.scheduler.Personalization
import net.ulich.crm.view.main.MainView
import org.springframework.beans.factory.annotation.Autowired

@Route(value = "emailTemplates", layout = MainView::class)
@ViewController("EmailTemplate.list")
@ViewDescriptor("email-template-list-view.xml")
@LookupComponent("emailTemplatesDataGrid")
@DialogMode(width = "64em")
class EmailTemplateListView : StandardListView<EmailTemplate>() {
    @ViewComponent
    private lateinit var emailTemplatesDataGrid: DataGrid<EmailTemplate>

    @Autowired
    private lateinit var dialogs: Dialogs

    @Autowired
    private lateinit var messageBundle: MessageBundle

    @Autowired
    private lateinit var recipientCsvParser: RecipientCsvParser

    @Autowired
    private lateinit var notifications: Notifications

    @Autowired
    private lateinit var emailService: EmailService

    @Subscribe("emailTemplatesDataGrid.bulkSend")
    private fun onEmailTemplatesDataGridBulkSend(event: ActionPerformedEvent) {
        dialogs.createInputDialog(this)
            .withHeader(messageBundle.getMessage("bulkSendHeader"))
            .withLabelsPosition(Dialogs.InputDialogBuilder.LabelsPosition.TOP)
            .withParameters(
                InputParameter.stringParameter("csv")
                    .withField(::textArea)
                    .withLabel(messageBundle.getMessage("csvLabel"))
            )
            .withActions(DialogActions.OK_CANCEL)
            .withCloseListener(::bulkSendDialogClosed)
            .open()
    }

    private fun bulkSendDialogClosed(event: InputDialog.InputDialogCloseEvent?) {
        if (event == null || !event.closedWith(DialogOutcome.OK)) {
            return
        }

        val csv = event.getValue<String>("csv")
        val records = recipientCsvParser.parse(csv!!)
        if (records == null) {
            notifications.create(messageBundle.getMessage("csvImportFailed"))
                .withType(Notifications.Type.ERROR)
                .show()
            return
        }

        records.forEach { record ->
            sendMail(record)
        }

        emailService.processQueuedEmails()

        notifications.create(messageBundle.formatMessage("bulkemailSent", records.size))
            .withThemeVariant(NotificationVariant.LUMO_SUCCESS)
            .withPosition(Notification.Position.MIDDLE)
            .show()
    }

    private fun sendMail(recipient: RecipientCsvParser.Recipient) {
        val email = emailTemplatesDataGrid.singleSelectedItem!!

        emailService.sendPersonalizedEmail(
            recipient.email,
            email.subject!!,
            email.content!!,
            email.signature?.content,
            email.attachments,
            Personalization(
                salutation = recipient.salutation(),
                firstName = recipient.firstName,
                lastName = recipient.lastName,
                companyName = null,
                street = null,
                postCode = null,
                city = null
            )
        )
    }

    private fun textArea(): Component {
        val field = TextArea()
        field.width = "100%"
        return field
    }
}