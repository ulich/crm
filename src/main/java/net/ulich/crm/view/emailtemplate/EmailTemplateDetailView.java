package net.ulich.crm.view.emailtemplate;

import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.router.Route;
import io.jmix.core.security.CurrentAuthentication;
import io.jmix.flowui.Dialogs;
import io.jmix.flowui.Notifications;
import io.jmix.flowui.UiComponents;
import io.jmix.flowui.app.inputdialog.DialogActions;
import io.jmix.flowui.app.inputdialog.DialogOutcome;
import io.jmix.flowui.app.inputdialog.InputParameter;
import io.jmix.flowui.component.codeeditor.CodeEditor;
import io.jmix.flowui.component.richtexteditor.RichTextEditor;
import io.jmix.flowui.data.value.ContainerValueSource;
import io.jmix.flowui.kit.action.ActionPerformedEvent;
import io.jmix.flowui.model.InstanceContainer;
import io.jmix.flowui.view.*;
import net.ulich.crm.entity.EmailAttachment;
import net.ulich.crm.entity.EmailTemplate;
import net.ulich.crm.entity.User;
import net.ulich.crm.scheduler.EmailService;
import net.ulich.crm.scheduler.Personalization;
import net.ulich.crm.view.main.MainView;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "emailTemplates/:id", layout = MainView.class)
@ViewController("EmailTemplate.detail")
@ViewDescriptor("email-template-detail-view.xml")
@EditedEntityContainer("emailTemplateDc")
public class EmailTemplateDetailView extends StandardDetailView<EmailTemplate> {
    @ViewComponent
    private FormLayout form;

    @Autowired
    private EmailService emailService;

    @Autowired
    private Notifications notifications;

    @ViewComponent
    private MessageBundle messageBundle;

    @Autowired
    private Dialogs dialogs;

    @Autowired
    private CurrentAuthentication currentAuthentication;

    @Autowired
    private UiComponents uiComponents;

    @ViewComponent
    private InstanceContainer<EmailTemplate> emailTemplateDc;

    @Subscribe
    private void onReady(ReadyEvent event) {
        String content = getEditedEntity().getContent();
        boolean showCodeEditor = content != null && (content.contains("<table>") || content.contains("<table ") || content.contains("<img "));

        if (showCodeEditor) {
            CodeEditor contentField = uiComponents.create(CodeEditor.class);
            contentField.setValueSource(new ContainerValueSource<>(emailTemplateDc, "content"));
            contentField.setLabel(messageBundle.getMessage("content"));
            form.add(contentField);
            form.setColspan(contentField, 2);
        } else {
            RichTextEditor contentField = uiComponents.create(RichTextEditor.class);
            contentField.setValueSource(new ContainerValueSource<>(emailTemplateDc, "content"));
            contentField.setLabel(messageBundle.getMessage("content"));
            form.add(contentField);
            form.setColspan(contentField, 2);
        }
    }

    @Supply(to = "attachmentsDataGrid.file", subject = "renderer")
    private Renderer<EmailAttachment> attachmentsDataGridFileRenderer() {
        return new TextRenderer<>(attachment -> attachment.getFile() != null ? attachment.getFile().getFileName() : null);
    }

    @Subscribe("sendTestMail")
    private void onSendTestMail(ActionPerformedEvent event) {
        if (getEditedEntity().getSubject() == null || getEditedEntity().getContent() == null) {
            return;
        }

        User currentUser = (User) currentAuthentication.getUser();

        dialogs.createInputDialog(this)
                .withHeader(messageBundle.getMessage("sendTestMailHeader"))
                .withLabelsPosition(Dialogs.InputDialogBuilder.LabelsPosition.TOP)
                .withParameters(
                        new InputParameter("emailAddress")
                                .withLabel(messageBundle.getMessage("emailAddress"))
                                .withDefaultValue(currentUser.getEmail())
                )
                .withActions(DialogActions.OK_CANCEL)
                .withCloseListener(e -> {
                    if (e.closedWith(DialogOutcome.OK)) {
                        String emailAddress = (String) e.getValues().get("emailAddress");
                        sendMail(emailAddress);
                    }
                })
                .open();
    }

    private void sendMail(String toAddress) {
        emailService.sendPersonalizedEmail(
                toAddress,
                getEditedEntity().getSubject(),
                getEditedEntity().getContent(),
                getEditedEntity().getSignature() != null ? getEditedEntity().getSignature().getContent() : null,
                getEditedEntity().getAttachments(),
                new Personalization(
                        "Musterfirma GmbH",
                        "Sehr geehrter Herr Mustermann",
                        "Max",
                        "Mustermann",
                        "Musterstraße 1",
                        "12345",
                        "Musterstadt"
                )
        );
        emailService.processQueuedEmails();

        notifications.create(messageBundle.getMessage("emailSent"))
                .withThemeVariant(NotificationVariant.LUMO_SUCCESS)
                .withPosition(Notification.Position.BOTTOM_END)
                .show();
    }
}
