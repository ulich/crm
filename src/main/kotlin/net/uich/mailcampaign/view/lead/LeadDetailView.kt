package net.uich.mailcampaign.view.lead

import com.vaadin.flow.component.AbstractField
import com.vaadin.flow.component.BlurNotifier
import com.vaadin.flow.data.renderer.Renderer
import com.vaadin.flow.data.renderer.TextRenderer
import net.uich.mailcampaign.view.main.MainView
import com.vaadin.flow.router.Route
import io.jmix.core.DataManager
import io.jmix.core.EntityStates
import io.jmix.core.event.EntityChangedEvent
import io.jmix.flowui.DialogWindows
import io.jmix.flowui.component.combobox.EntityComboBox
import io.jmix.flowui.component.grid.DataGrid
import io.jmix.flowui.kit.action.ActionPerformedEvent
import io.jmix.flowui.model.CollectionPropertyContainer
import io.jmix.flowui.view.*
import net.uich.mailcampaign.entity.*
import net.uich.mailcampaign.view.scheduledemail.ScheduledEmailDetailView
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId

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

    @Autowired
    private lateinit var dialogWindows: DialogWindows

    @Supply(to = "scheduledEmailsDataGrid.customAttachments", subject = "renderer")
    private fun scheduledEmailsDataGridCustomAttachmentsRenderer(): Renderer<ScheduledEmail> {
        return TextRenderer { it.customAttachments.size.toString() }
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

        lead.campaign?.scheduleItems?.map{ scheduleItem ->
            val scheduled = dataManager.create(ScheduledEmail::class.java).apply {
                this.lead = lead
                this.emailTemplate = scheduleItem.emailTemplate
                this.plannedSendDate = calculatePlannedSendDate(scheduleItem)
            }
            lead.scheduledEmails.add(scheduled)
        }

        scheduledEmailsDc.setItems(lead.scheduledEmails)
    }

    private fun calculatePlannedSendDate(scheduleItem: ScheduleItem): OffsetDateTime {
        val berlin = ZoneId.of("Europe/Berlin")

        val now = LocalDateTime.now(berlin)

        val day = scheduleItem.day!!
        if (day < 2) {
            return OffsetDateTime.now().plusMinutes(5)
        }

        val localTime = LocalDateTime.ofInstant(scheduleItem.time?.toInstant(), ZoneId.systemDefault()).toLocalTime();
        return now.plusDays((day - 1).toLong()).toLocalDate()
                .atTime(localTime)
                .atZone(berlin)
                .toOffsetDateTime()
    }
}