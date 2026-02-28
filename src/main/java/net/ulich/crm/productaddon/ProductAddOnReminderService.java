package net.ulich.crm.productaddon;

import io.jmix.flowui.model.DataContext;
import net.ulich.crm.entity.*;
import net.ulich.crm.scheduler.LeadEmailService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProductAddOnReminderService {
    private final LeadEmailService leadEmailService;

    public ProductAddOnReminderService(LeadEmailService leadEmailService) {
        this.leadEmailService = leadEmailService;
    }

    public void replaceReminderEmails(Lead lead, DataContext dataContext) {
        List<ScheduledEmail> mailsToRemove = lead.getScheduledEmails().stream()
                .filter(e -> e.getSourceType() == ScheduledEmailSourceType.PRODUCT_ADD_ON_REMINDER)
                .filter(e -> e.getPlannedSendDate() != null && e.getPlannedSendDate().isAfter(OffsetDateTime.now()))
                .toList();

        lead.getScheduledEmails().removeAll(mailsToRemove);
        mailsToRemove.forEach(dataContext::remove);

        Map<LocalDate, List<OrderedProduct>> orderedProductsByDeliveryDate = lead.getOrderedProducts().stream()
                .filter(p -> p.getProductAddOns().stream().anyMatch(addOn -> addOn.getRecurringEmail() != null))
                .collect(Collectors.groupingBy(OrderedProduct::getDeliveryDate));

        orderedProductsByDeliveryDate.forEach((deliveryDate, products) -> {
            Set<RecurringEmail> recurringEmails = products.stream()
                    .flatMap(p -> p.getProductAddOns().stream().map(addOn -> addOn.getRecurringEmail()))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            recurringEmails.forEach(recurringEmail -> {
                ScheduledEmail scheduled = leadEmailService.createRecurringEmail(lead, recurringEmail, deliveryDate);
                dataContext.merge(scheduled);
                lead.getScheduledEmails().add(scheduled);
            });
        });
    }
}
