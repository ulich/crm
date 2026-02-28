package net.ulich.crm.view.scheduledemail;

import com.vaadin.flow.component.grid.ItemDoubleClickEvent;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.router.Route;
import io.jmix.core.DataManager;
import io.jmix.flowui.DialogWindows;
import io.jmix.flowui.UiComponents;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.model.CollectionContainer;
import io.jmix.flowui.view.*;
import net.ulich.crm.entity.Lead;
import net.ulich.crm.entity.ScheduledEmail;
import net.ulich.crm.view.lead.LeadDetailView;
import net.ulich.crm.view.main.MainView;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "scheduledEmails", layout = MainView.class)
@ViewController("ScheduledEmail.list")
@ViewDescriptor("scheduled-email-list-view.xml")
@LookupComponent("scheduledEmailsDataGrid")
@DialogMode(width = "64em")
public class ScheduledEmailListView extends StandardListView<ScheduledEmail> {

    @Autowired
    private DialogWindows dialogWindows;

    @Autowired
    private UiComponents uiComponents;

    @Autowired
    private DataManager dataManager;

    @ViewComponent
    private CollectionContainer<ScheduledEmail> scheduledEmailsDc;

    @Subscribe("scheduledEmailsDataGrid")
    private void onScheduledEmailsDataGridItemDoubleClick(ItemDoubleClickEvent<ScheduledEmail> event) {
        dialogWindows.detail(this, Lead.class)
                .withViewClass(LeadDetailView.class)
                .editEntity(event.getItem().getLead())
                .open();
    }

    @Supply(to = "scheduledEmailsDataGrid.complete", subject = "renderer")
    private Renderer<ScheduledEmail> scheduledEmailsDataGridCompleteRenderer() {
        return new ComponentRenderer<>(scheduledEmail -> {
            JmixButton button = uiComponents.create(JmixButton.class);
            button.setIcon(new Icon(VaadinIcon.CHECK));
            button.addClickListener(click -> {
                scheduledEmail.setComplete(true);
                dataManager.save(scheduledEmail);
                scheduledEmailsDc.getMutableItems().remove(scheduledEmail);
            });
            return button;
        });
    }
}
