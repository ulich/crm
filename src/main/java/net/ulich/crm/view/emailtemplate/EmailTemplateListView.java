package net.ulich.crm.view.emailtemplate;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.Dialogs;
import io.jmix.flowui.Notifications;
import io.jmix.flowui.app.inputdialog.DialogActions;
import io.jmix.flowui.app.inputdialog.DialogOutcome;
import io.jmix.flowui.app.inputdialog.InputDialog;
import io.jmix.flowui.app.inputdialog.InputParameter;
import io.jmix.flowui.component.grid.DataGrid;
import io.jmix.flowui.kit.action.ActionPerformedEvent;
import io.jmix.flowui.view.*;
import net.ulich.crm.email.RecipientCsvParser;
import net.ulich.crm.entity.EmailTemplate;
import net.ulich.crm.scheduler.EmailService;
import net.ulich.crm.scheduler.Personalization;
import net.ulich.crm.view.main.MainView;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "emailTemplates", layout = MainView.class)
@ViewController("EmailTemplate.list")
@ViewDescriptor("email-template-list-view.xml")
@LookupComponent("emailTemplatesDataGrid")
@DialogMode(width = "64em")
public class EmailTemplateListView extends StandardListView<EmailTemplate> {
    @ViewComponent
    private DataGrid<EmailTemplate> emailTemplatesDataGrid;

    @Autowired
    private Dialogs dialogs;

    @ViewComponent
    private MessageBundle messageBundle;

    @Autowired
    private RecipientCsvParser recipientCsvParser;

    @Autowired
    private Notifications notifications;

    @Autowired
    private EmailService emailService;

    @Subscribe("emailTemplatesDataGrid.bulkSend")
    private void onEmailTemplatesDataGridBulkSend(ActionPerformedEvent event) {
        dialogs.createInputDialog(this)
                .withHeader(messageBundle.getMessage("bulkSendHeader"))
                .withLabelsPosition(Dialogs.InputDialogBuilder.LabelsPosition.TOP)
                .withParameters(
                        InputParameter.stringParameter("csv")
                                .withField(this::textArea)
                                .withLabel(messageBundle.getMessage("recipientsCsvLabel"))
                )
                .withActions(DialogActions.OK_CANCEL)
                .withCloseListener(this::bulkSendDialogClosed)
                .open();
    }

    private void bulkSendDialogClosed(InputDialog.InputDialogCloseEvent event) {
        if (event == null || !event.closedWith(DialogOutcome.OK)) {
            return;
        }

        String csv = event.getValue("csv");
        var records = recipientCsvParser.parse(csv);
        if (records == null) {
            notifications.create(messageBundle.getMessage("bulkSendFailed"))
                    .withType(Notifications.Type.ERROR)
                    .show();
            return;
        }

        records.forEach(this::sendMail);
        emailService.processQueuedEmails();

        notifications.create(messageBundle.formatMessage("bulkemailSent", records.size()))
                .withThemeVariant(NotificationVariant.LUMO_SUCCESS)
                .withPosition(Notification.Position.MIDDLE)
                .show();
    }

    private void sendMail(RecipientCsvParser.Recipient recipient) {
        EmailTemplate email = emailTemplatesDataGrid.getSingleSelectedItem();

        emailService.sendPersonalizedEmail(
                recipient.getEmail(),
                email.getSubject(),
                email.getContent(),
                email.getSignature() != null ? email.getSignature().getContent() : null,
                email.getAttachments(),
                new Personalization(
                        null,
                        recipient.salutation(),
                        recipient.getFirstName(),
                        recipient.getLastName(),
                        null,
                        null,
                        null
                )
        );
    }

    private Component textArea() {
        TextArea field = new TextArea();
        field.setWidth("100%");
        return field;
    }
}
