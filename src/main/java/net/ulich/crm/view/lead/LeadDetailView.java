package net.ulich.crm.view.lead;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.Route;
import io.jmix.core.DataManager;
import io.jmix.core.EntityStates;
import io.jmix.flowui.DialogWindows;
import io.jmix.flowui.component.combobox.EntityComboBox;
import io.jmix.flowui.component.grid.DataGrid;
import io.jmix.flowui.kit.action.ActionPerformedEvent;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.model.CollectionChangeType;
import io.jmix.flowui.model.CollectionContainer;
import io.jmix.flowui.model.CollectionPropertyContainer;
import io.jmix.flowui.model.DataContext;
import io.jmix.flowui.view.*;
import net.ulich.crm.entity.*;
import net.ulich.crm.productaddon.ProductAddOnReminderService;
import net.ulich.crm.time.AppTimeZone;
import net.ulich.crm.view.main.MainView;
import net.ulich.crm.view.orderedproduct.OrderedProductDetailView;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.*;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.function.IntFunction;

@Route(value = "leads/:id", layout = MainView.class)
@ViewController("Lead.detail")
@ViewDescriptor("lead-detail-view.xml")
@EditedEntityContainer("leadDc")
public class LeadDetailView extends StandardDetailView<Lead> {

    @ViewComponent
    private CollectionPropertyContainer<ScheduledEmail> scheduledEmailsDc;

    @ViewComponent
    private CollectionPropertyContainer<OrderedProduct> orderedProductsDc;

    @Autowired
    private DataManager dataManager;

    @Autowired
    private EntityStates entityStates;

    @ViewComponent
    private EntityComboBox<Campaign> campaignField;

    @ViewComponent
    private DataGrid<OrderedProduct> orderedProductsDataGrid;

    @ViewComponent
    private DataContext dataContext;

    @Autowired
    private LeadValidator leadValidator;

    @Autowired
    private DialogWindows dialogWindows;

    @Autowired
    private ProductAddOnReminderService productAddOnReminderService;

    private static IntFunction<TemporalAdjuster> addBusinessDays = days -> TemporalAdjusters.ofDateAdjuster(date -> {
        LocalDate previousOrSameMonday = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        double daysFromMonday = previousOrSameMonday.until(date).getDays();

        int businessDays = (int) (days + Math.min(daysFromMonday, 4.0));
        return previousOrSameMonday
                .plusWeeks(businessDays / 5L)
                .plusDays(businessDays % 5L);
    });

    @Subscribe
    private void onInit(InitEvent event) {
        orderedProductsDc.addCollectionChangeListener(this::onOrderedProductsChanged);
    }

    @Subscribe
    private void onBeforeShow(BeforeShowEvent event) {
        campaignField.setEnabled(entityStates.isNew(getEditedEntity()));
    }

    @Subscribe
    private void onValidation(ValidationEvent event) {
        event.addErrors(leadValidator.validate(getEditedEntity()));
    }

    @Subscribe("campaignField")
    private void onCampaignFieldComponentValueChange(AbstractField.ComponentValueChangeEvent<EntityComboBox<Campaign>, Campaign> event) {
        if (event.getOldValue() == null && !entityStates.isNew(getEditedEntity())) {
            return;
        }
        generateScheduledEmails();
    }

    private void generateScheduledEmails() {
        Lead lead = getEditedEntity();
        lead.getScheduledEmails().clear();

        ZonedDateTime now = ZonedDateTime.now(AppTimeZone.BERLIN);

        Campaign campaign = lead.getCampaign();
        if (campaign != null && campaign.getScheduleItems() != null) {
            for (ScheduleItem scheduleItem : campaign.getScheduleItems()) {
                ScheduledEmail scheduled = dataManager.create(ScheduledEmail.class);
                scheduled.setLead(lead);
                scheduled.setEmailTemplate(scheduleItem.getEmailTemplate());
                scheduled.setSourceType(ScheduledEmailSourceType.CAMPAIGN);
                scheduled.setPlannedSendDate(calculatePlannedSendDate(now, scheduleItem.getDay(), scheduleItem.getTime()));

                dataContext.merge(scheduled);
                lead.getScheduledEmails().add(scheduled);
            }
        }

        scheduledEmailsDc.setItems(sortScheduledEmailsDesc(lead.getScheduledEmails()));
    }

    public void prefillWith(Lead lead) {
        Lead edited = getEditedEntity();
        edited.setGender(lead.getGender());
        edited.setCompanyName(lead.getCompanyName());
        edited.setFirstName(lead.getFirstName());
        edited.setLastName(lead.getLastName());
        edited.setStreet(lead.getStreet());
        edited.setPostCode(lead.getPostCode());
        edited.setCity(lead.getCity());
        edited.setEmail(lead.getEmail());
        edited.setPhoneNumber(lead.getPhoneNumber());
        edited.setAlternativePhoneNumber(lead.getAlternativePhoneNumber());
        edited.setNotes(lead.getNotes());
    }

    public static OffsetDateTime calculatePlannedSendDate(ZonedDateTime now, int day, Date time) {
        if (time == null) {
            return now.plusMinutes(5).toOffsetDateTime();
        }

        var localTime = LocalDateTime.ofInstant(time.toInstant(), ZoneId.systemDefault()).toLocalTime();

        ZonedDateTime date = day == 1 ? now : now.with(addBusinessDays.apply(day - 1));
        return date.toLocalDate()
                .atTime(localTime)
                .atZone(AppTimeZone.BERLIN)
                .toOffsetDateTime();
    }

    @Subscribe(id = "callButton", subject = "clickListener")
    private void onCallButtonClick(ClickEvent<JmixButton> event) {
        callNumber(getEditedEntity().getPhoneNumber());
    }

    @Subscribe(id = "callAltNumberButton", subject = "clickListener")
    private void onCallAltNumberButtonClick(ClickEvent<JmixButton> event) {
        callNumber(getEditedEntity().getAlternativePhoneNumber());
    }

    private void callNumber(String number) {
        if (number == null || number.isBlank()) {
            return;
        }

        UI.getCurrent().getPage().executeJs("document.location.href = $0;", "tel:" + number);
    }

    @Subscribe("orderedProductsDataGrid.copy")
    private void onOrderedProductsDataGridCopy(ActionPerformedEvent event) {
        OrderedProduct selectedProduct = orderedProductsDataGrid.getSingleSelectedItem();
        if (selectedProduct == null) {
            return;
        }

        dialogWindows.detail(this, OrderedProduct.class)
                .withViewClass(OrderedProductDetailView.class)
                .newEntity()
                .withParentDataContext(dataContext)
                .withInitializer(copy -> copy.copyFrom(selectedProduct))
                .withAfterCloseListener(e -> {
                    if (e.closedWith(StandardOutcome.SAVE)) {
                        OrderedProduct copy = e.getView().getEditedEntity();
                        Lead lead = getEditedEntity();
                        lead.getOrderedProducts().add(copy);
                        copy.setLead(lead);

                        orderedProductsDc.setItems(lead.getOrderedProducts());
                    }
                })
                .open();
    }

    private void onOrderedProductsChanged(CollectionContainer.CollectionChangeEvent<OrderedProduct> event) {
        if (event.getChangeType() == CollectionChangeType.REFRESH) {
            return;
        }

        productAddOnReminderService.replaceReminderEmails(getEditedEntity(), dataContext);
        scheduledEmailsDc.setItems(sortScheduledEmailsDesc(getEditedEntity().getScheduledEmails()));
    }

    private List<ScheduledEmail> sortScheduledEmailsDesc(List<ScheduledEmail> scheduledEmails) {
        return scheduledEmails.stream()
                .sorted(Comparator.comparing(ScheduledEmail::getPlannedSendDate, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .toList();
    }
}
