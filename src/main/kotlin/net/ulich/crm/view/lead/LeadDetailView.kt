package net.ulich.crm.view.lead

import com.vaadin.flow.component.AbstractField
import com.vaadin.flow.router.Route
import io.jmix.core.DataManager
import io.jmix.core.EntityStates
import io.jmix.flowui.component.combobox.EntityComboBox
import io.jmix.flowui.model.CollectionPropertyContainer
import io.jmix.flowui.model.DataContext
import io.jmix.flowui.view.*
import net.ulich.crm.entity.Campaign
import net.ulich.crm.entity.Lead
import net.ulich.crm.entity.ScheduledEmail
import net.ulich.crm.view.main.MainView
import org.springframework.beans.factory.annotation.Autowired
import java.time.*
import java.time.temporal.TemporalAdjuster
import java.time.temporal.TemporalAdjusters
import java.util.*
import java.util.function.IntFunction
import kotlin.math.min


@Route(value = "leads/:id", layout = MainView::class)
@ViewController("Lead.detail")
@ViewDescriptor("lead-detail-view.xml")
@EditedEntityContainer("leadDc")
class LeadDetailView : StandardDetailView<Lead>() {
    @ViewComponent
    private lateinit var scheduledEmailsDc: CollectionPropertyContainer<ScheduledEmail>

    @Autowired
    private lateinit var dataManager: DataManager

    @Autowired
    private lateinit var entityStates: EntityStates

    @ViewComponent
    private lateinit var campaignField: EntityComboBox<Campaign>

    @ViewComponent
    private lateinit var dataContext: DataContext

    @Autowired
    private lateinit var leadValidator: LeadValidator

    @Subscribe
    private fun onBeforeShow(event: BeforeShowEvent) {
        campaignField.isEnabled = entityStates.isNew(editedEntity)
    }

    @Subscribe
    private fun onValidation(event: ValidationEvent) {
        val errors = leadValidator.validate(editedEntity)
        event.addErrors(errors)
    }

    @Subscribe("campaignField")
    private fun onCampaignFieldComponentValueChange(event: AbstractField.ComponentValueChangeEvent<EntityComboBox<Campaign>, Campaign>) {
        // when editing an existing entity, an event is triggered when opening the edit view, discard it
        if (event.oldValue == null && !entityStates.isNew(editedEntity)) {
            return
        }
        generateScheduledEmails()
    }

    private fun generateScheduledEmails() {
        val lead = editedEntity
        lead.scheduledEmails.clear()

        val now = ZonedDateTime.now(berlin)

        lead.campaign?.scheduleItems?.map{ scheduleItem ->
            val scheduled = dataManager.create(ScheduledEmail::class.java).apply {
                this.lead = lead
                this.emailTemplate = scheduleItem.emailTemplate

                this.plannedSendDate = calculatePlannedSendDate(now, scheduleItem.day!!, scheduleItem.time)
            }
            dataContext.merge(scheduled)
            lead.scheduledEmails.add(scheduled)
        }

        scheduledEmailsDc.setItems(lead.scheduledEmails.sortedBy { it.plannedSendDate })
    }

    fun prefillWith(lead: Lead) {
        editedEntity.companyName = lead.companyName
        editedEntity.firstName = lead.firstName
        editedEntity.lastName = lead.lastName
        editedEntity.street = lead.street
        editedEntity.postCode = lead.postCode
        editedEntity.city = lead.city
        editedEntity.email = lead.email
        editedEntity.phoneNumber = lead.phoneNumber
        editedEntity.notes = lead.notes
    }

    companion object {
        private val berlin = ZoneId.of("Europe/Berlin")

        fun calculatePlannedSendDate(now: ZonedDateTime, day: Int, time: Date?): OffsetDateTime {
            if (time == null) {
                return now.plusMinutes(5).toOffsetDateTime()
            }

            val localTime = LocalDateTime.ofInstant(time.toInstant(), ZoneId.systemDefault()).toLocalTime()

            val date = if (day == 1) now else now.with(addBusinessDays.apply(day - 1))
            return date.toLocalDate()
                    .atTime(localTime)
                    .atZone(berlin)
                    .toOffsetDateTime()
        }

        var addBusinessDays: IntFunction<TemporalAdjuster> = IntFunction { days: Int ->
            TemporalAdjusters.ofDateAdjuster { date: LocalDate ->
                val previousOrSameMonday = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                val daysFromMonday = previousOrSameMonday.until(date).days.toDouble()

                val businessDays = (days + min(daysFromMonday, 4.0)).toInt()
                previousOrSameMonday
                        .plusWeeks((businessDays / 5).toLong())
                        .plusDays((businessDays % 5).toLong())
            }
        }
    }
}