package net.ulich.crm.productaddon

import io.jmix.core.DataManager
import io.jmix.core.Id
import io.jmix.core.SaveContext
import io.jmix.core.event.EntityChangedEvent
import net.ulich.crm.entity.Lead
import net.ulich.crm.entity.OrderedProduct
import net.ulich.crm.entity.ScheduledEmailSourceType
import net.ulich.crm.scheduler.LeadEmailService
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import java.time.OffsetDateTime
import java.util.stream.Collectors.groupingBy
import java.util.stream.Collectors.toSet


@Component
open class OrderedProductChangeListener(
    private val dataManager: DataManager,
    private val leadEmailService: LeadEmailService
) {

    @EventListener
    open fun onOrderedProductChangedBeforeCommit(event: EntityChangedEvent<OrderedProduct>) {
        when (event.type) {
            EntityChangedEvent.Type.CREATED -> {
                onOrderedProductCreated(event)
            }

            EntityChangedEvent.Type.UPDATED -> {
                onOrderedProductUpdated(event)
            }

            EntityChangedEvent.Type.DELETED -> {
                onOrderedProductDeleted(event)
            }

            else -> {
                throw IllegalStateException("Unsupported event type: ${event.type}")
            }
        }
    }

    private fun onOrderedProductCreated(event: EntityChangedEvent<OrderedProduct>) {
        val orderedProduct = dataManager.load(event.entityId).one()
        replaceReminderEmails(orderedProduct.lead!!)
    }

    private fun onOrderedProductUpdated(event: EntityChangedEvent<OrderedProduct>) {
        val orderedProduct = dataManager.load(event.entityId).one()
        replaceReminderEmails(orderedProduct.lead!!)
    }

    private fun onOrderedProductDeleted(event: EntityChangedEvent<OrderedProduct>) {
        val leadId = event.changes.getOldValue<Id<Lead>>("lead")
        val lead = dataManager.load(leadId!!).one()
        replaceReminderEmails(lead)
    }

    private fun replaceReminderEmails(lead: Lead) {
        val saveContext = SaveContext()

        lead.scheduledEmails.stream()
            .filter { e -> e.getSourceType() == ScheduledEmailSourceType.PRODUCT_ADD_ON_REMINDER }
            .filter { e -> e.plannedSendDate!!.isAfter(OffsetDateTime.now()) }
            .forEach { e -> saveContext.removing(e) }

        val orderedProductsByDeliveryDate = lead.orderedProducts.stream()
            .filter { p -> p.productAddOn?.recurringEmail != null }
            .collect(groupingBy { p -> p.deliveryDate })

        orderedProductsByDeliveryDate.forEach { deliveryDate, products ->
            val recurringEmails = products.stream()
                .map { p -> p.productAddOn!!.recurringEmail!! }
                .collect(toSet())

            recurringEmails.forEach { recurringEmail ->
                val scheduled = leadEmailService.createRecurringEmail(lead, recurringEmail, deliveryDate!!)
                saveContext.saving(scheduled)
            }
        }

        dataManager.save(saveContext)
    }
}