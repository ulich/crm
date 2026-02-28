package net.ulich.crm.view.lead;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.Route;
import io.jmix.core.Messages;
import io.jmix.flowui.Dialogs;
import io.jmix.flowui.Notifications;
import io.jmix.flowui.ViewNavigators;
import io.jmix.flowui.app.inputdialog.DialogActions;
import io.jmix.flowui.app.inputdialog.DialogOutcome;
import io.jmix.flowui.app.inputdialog.InputDialog;
import io.jmix.flowui.app.inputdialog.InputParameter;
import io.jmix.flowui.component.grid.DataGrid;
import io.jmix.flowui.kit.action.ActionPerformedEvent;
import io.jmix.flowui.model.CollectionContainer;
import io.jmix.flowui.model.CollectionLoader;
import io.jmix.flowui.view.*;
import net.ulich.crm.entity.EmailTemplate;
import net.ulich.crm.entity.Lead;
import net.ulich.crm.lead.LeadImporter;
import net.ulich.crm.scheduler.LeadEmailService;
import net.ulich.crm.view.main.MainView;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@Route(value = "leads", layout = MainView.class)
@ViewController("Lead.list")
@ViewDescriptor("lead-list-view.xml")
@LookupComponent("leadsDataGrid")
@DialogMode(width = "64em")
public class LeadListView extends StandardListView<Lead> {
    @ViewComponent
    private DataGrid<Lead> leadsDataGrid;

    @ViewComponent
    private CollectionLoader<Lead> leadsDl;

    @ViewComponent
    private CollectionContainer<Lead> leadsDc;

    @Autowired
    private Dialogs dialogs;

    @Autowired
    private Messages messages;

    @Autowired
    private LeadImporter leadImporter;

    @Autowired
    private Notifications notifications;

    @Autowired
    private ViewNavigators viewNavigators;

    @Autowired
    private LeadEmailService leadEmailService;

    @Subscribe("leadsDataGrid.importLead")
    private void onLeadsDataGridimportLead(ActionPerformedEvent event) {
        dialogs.createInputDialog(this)
                .withHeader(messages.getMessage(getClass(), "importLeadHeader"))
                .withLabelsPosition(Dialogs.InputDialogBuilder.LabelsPosition.TOP)
                .withParameters(
                        InputParameter.stringParameter("content")
                                .withField(this::textArea)
                                .withLabel(messages.getMessage(getClass(), "importLeadLabel"))
                )
                .withActions(DialogActions.OK_CANCEL)
                .withCloseListener(this::csvDialogClosed)
                .open();
    }

    private void csvDialogClosed(InputDialog.InputDialogCloseEvent event) {
        if (event == null || !event.closedWith(DialogOutcome.OK)) {
            return;
        }

        String content = event.getValue("content");
        Lead lead = leadImporter.importLead(content);
        if (lead == null) {
            notifications.create(messages.getMessage(getClass(), "importLeadFailed"))
                    .withType(Notifications.Type.ERROR)
                    .show();
            return;
        }

        viewNavigators.detailView(Lead.class)
                .newEntity()
                .withViewClass(LeadDetailView.class)
                .withAfterNavigationHandler(e -> e.getView().prefillWith(lead))
                .navigate();
    }

    private Component textArea() {
        TextArea field = new TextArea();
        field.setWidth("100%");
        return field;
    }

    @Subscribe("leadsDataGrid.sendMail")
    private void onLeadsDataGridSendMail(ActionPerformedEvent event) {
        List<InputParameter> parameters = new ArrayList<>();
        parameters.add(
                InputParameter.entityParameter("emailTemplate", EmailTemplate.class)
                        .withLabel(messages.getMessage(getClass(), "emailTemplateLabel"))
                        .withRequired(true)
        );

        if (leadsDataGrid.getSelectedItems().size() == leadsDataGrid.getPageSize()) {
            parameters.add(
                    InputParameter.booleanParameter("sendToAll")
                            .withField(() -> {
                                Checkbox checkbox = new Checkbox();
                                checkbox.setLabel(messages.getMessage(LeadDetailView.class, "sendToAllLabel"));
                                checkbox.setValue(true);
                                return checkbox;
                            })
                            .withDefaultValue(true)
            );
        }

        dialogs.createInputDialog(this)
                .withHeader(messages.getMessage(getClass(), "sendMailHeader"))
                .withLabelsPosition(Dialogs.InputDialogBuilder.LabelsPosition.TOP)
                .withParameters(parameters.toArray(new InputParameter[0]))
                .withActions(DialogActions.OK_CANCEL)
                .withCloseListener(this::sendEmailDialogClosed)
                .open();
    }

    private void sendEmailDialogClosed(InputDialog.InputDialogCloseEvent event) {
        if (event == null || !event.closedWith(DialogOutcome.OK)) {
            return;
        }

        EmailTemplate emailTemplate = event.getValue("emailTemplate");
        Boolean sendToAll = event.getValue("sendToAll");
        if (sendToAll == null) {
            sendToAll = false;
        }

        List<Lead> leads = new ArrayList<>(leadsDataGrid.getSelectedItems());

        Integer oldMaxResults = leadsDl.getMaxResults();
        if (sendToAll) {
            leadsDl.setMaxResults(1_000_000);
            leadsDl.load();
            leads = leadsDc.getItems();
        }

        try {
            for (Lead lead : leads) {
                leadEmailService.sendEmailToLead(emailTemplate, lead);
            }
            leadEmailService.processQueuedEmails();

            notifications.create(messages.formatMessage(getClass(), "sendMailSuccess", leads.size()))
                    .withType(Notifications.Type.SUCCESS)
                    .show();
        } finally {
            if (sendToAll) {
                leadsDl.setMaxResults(oldMaxResults);
                leadsDl.load();
            }
        }
    }
}
