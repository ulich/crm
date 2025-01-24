package net.ulich.crm.productaddon

import io.jmix.flowui.model.DataContext
import net.ulich.crm.entity.Lead
import net.ulich.crm.entity.ScheduledEmailSourceType
import net.ulich.crm.scheduler.LeadEmailService
import org.springframework.stereotype.Service
import java.time.OffsetDateTime
import java.util.stream.Collectors.groupingBy
import java.util.stream.Collectors.toSet


@Service
open class ProductAddOnReminderService(
    private val leadEmailService: LeadEmailService
) {

    fun replaceReminderEmails(lead: Lead, dataContext: DataContext) {
        val mailsToRemove = lead.scheduledEmails.stream()
            .filter { e -> e.getSourceType() == ScheduledEmailSourceType.PRODUCT_ADD_ON_REMINDER }
            .filter { e -> e.plannedSendDate!!.isAfter(OffsetDateTime.now()) }
            .toList()

        lead.scheduledEmails.removeAll(mailsToRemove)
        mailsToRemove.forEach(dataContext::remove)

        val orderedProductsByDeliveryDate = lead.orderedProducts.stream()
            .filter { p -> p.productAddOns.any { it.recurringEmail != null } }
            .collect(groupingBy { p -> p.deliveryDate })

        orderedProductsByDeliveryDate.forEach { deliveryDate, products ->
            val recurringEmails = products.stream()
                .flatMap { p -> p.productAddOns.map { it.recurringEmail }.stream() }
                .filter { r -> r != null }
                .collect(toSet())

            recurringEmails.forEach { recurringEmail ->
                val scheduled = leadEmailService.createRecurringEmail(lead, recurringEmail!!, deliveryDate!!)
                dataContext.merge(scheduled)
                lead.scheduledEmails.add(scheduled)
            }
        }
    }
}