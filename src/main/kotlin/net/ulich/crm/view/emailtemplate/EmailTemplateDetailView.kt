package net.ulich.crm.view.emailtemplate

import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.notification.NotificationVariant
import com.vaadin.flow.data.renderer.Renderer
import com.vaadin.flow.data.renderer.TextRenderer
import com.vaadin.flow.router.Route
import io.jmix.core.security.CurrentAuthentication
import io.jmix.flowui.Dialogs
import io.jmix.flowui.Notifications
import io.jmix.flowui.UiComponents
import io.jmix.flowui.app.inputdialog.DialogActions
import io.jmix.flowui.app.inputdialog.DialogOutcome
import io.jmix.flowui.app.inputdialog.InputParameter
import io.jmix.flowui.component.codeeditor.CodeEditor
import io.jmix.flowui.component.richtexteditor.RichTextEditor
import io.jmix.flowui.data.value.ContainerValueSource
import io.jmix.flowui.kit.action.ActionPerformedEvent
import io.jmix.flowui.model.InstanceContainer
import io.jmix.flowui.view.*
import net.ulich.crm.entity.EmailAttachment
import net.ulich.crm.entity.EmailTemplate
import net.ulich.crm.entity.User
import net.ulich.crm.scheduler.EmailService
import net.ulich.crm.scheduler.Personalization
import net.ulich.crm.view.main.MainView
import org.springframework.beans.factory.annotation.Autowired

@Route(value = "emailTemplates/:id", layout = MainView::class)
@ViewController("EmailTemplate.detail")
@ViewDescriptor("email-template-detail-view.xml")
@EditedEntityContainer("emailTemplateDc")
class EmailTemplateDetailView : StandardDetailView<EmailTemplate>() {
    @ViewComponent
    private lateinit var form: FormLayout

    @Autowired
    private lateinit var emailService: EmailService

    @Autowired
    private lateinit var notifications: Notifications

    @ViewComponent
    private lateinit var messageBundle: MessageBundle

    @Autowired
    private lateinit var dialogs: Dialogs

    @Autowired
    private lateinit var currentAuthentication: CurrentAuthentication

    @Autowired
    private lateinit var uiComponents: UiComponents

    @ViewComponent
    private lateinit var emailTemplateDc: InstanceContainer<EmailTemplate>

    @Subscribe
    private fun onReady(event: ReadyEvent) {
        val hasTable = editedEntity.content?.contains("<table>") == true
                || editedEntity.content?.contains("<table ") == true

        val fieldClass = if (hasTable) CodeEditor::class.java else RichTextEditor::class.java
        val contentField = uiComponents.create(fieldClass)
        contentField.valueSource = ContainerValueSource(emailTemplateDc, "content")
        contentField.label = messageBundle.getMessage("content")
        form.add(contentField)
        form.setColspan(contentField, 2)
    }

    @Supply(to = "attachmentsDataGrid.file", subject = "renderer")
    private fun attachmentsDataGridFileRenderer(): Renderer<EmailAttachment> {
        return TextRenderer { it.file?.fileName }
    }

    @Subscribe("sendTestMail")
    private fun onSendTestMail(event: ActionPerformedEvent) {
        if (editedEntity.subject == null || editedEntity.content == null) {
            return
        }

        val currentUser = currentAuthentication.user as User

        dialogs.createInputDialog(this)
            .withHeader(messageBundle.getMessage("sendTestMailHeader"))
            .withLabelsPosition(Dialogs.InputDialogBuilder.LabelsPosition.TOP)
            .withParameters(
                InputParameter("emailAddress")
                    .withLabel(messageBundle.getMessage("emailAddress"))
                    .withDefaultValue(currentUser.email)
            )
            .withActions(DialogActions.OK_CANCEL)
            .withCloseListener { e ->
                if (e.closedWith(DialogOutcome.OK)) {
                    val emailAddress = e.values["emailAddress"] as String
                    sendMail(emailAddress)
                }
            }.open()
    }

    private fun sendMail(toAddress: String) {
        emailService.sendPersonalizedEmail(
            toAddress,
            editedEntity.subject!!,
            editedEntity.content!!,
            editedEntity.signature?.content,
            editedEntity.attachments,
            Personalization(
                salutation = "Sehr geehrter Herr Mustermann",
                firstName = "Max",
                lastName = "Mustermann",
                companyName = "Musterfirma GmbH",
                street = "Musterstraße 1",
                postCode = "12345",
                city = "Musterstadt"
            )
        )
        emailService.processQueuedEmails()

        notifications.create(messageBundle.getMessage("emailSent"))
            .withThemeVariant(NotificationVariant.LUMO_SUCCESS)
            .withPosition(Notification.Position.BOTTOM_END)
            .show()
    }
}