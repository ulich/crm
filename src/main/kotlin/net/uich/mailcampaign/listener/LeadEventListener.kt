package net.uich.mailcampaign.listener

import io.jmix.core.DataManager
import io.jmix.core.event.EntityChangedEvent
import net.uich.mailcampaign.entity.Lead
import net.uich.mailcampaign.entity.ScheduleItem
import net.uich.mailcampaign.entity.ScheduledEmail
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId

@Component
open class LeadEventListener(private val dataManager: DataManager) {

    @EventListener
    open fun onLeadChangedBeforeCommit(event: EntityChangedEvent<Lead>) {
        if (event.type != EntityChangedEvent.Type.CREATED) {
            return
        }

        val lead = dataManager.load(event.entityId).one()
        lead.campaign?.scheduleItems?.map{ scheduleItem ->
            val scheduled = dataManager.create(ScheduledEmail::class.java).apply {
                this.lead = lead
                this.emailTemplate = scheduleItem.emailTemplate
                this.plannedSendDate = calculatePlannedSendDate(scheduleItem)
            }
            dataManager.save(scheduled)
        }
    }

    private fun calculatePlannedSendDate(scheduleItem: ScheduleItem): OffsetDateTime {
        val berlin = ZoneId.of("Europe/Berlin")

        val now = LocalDateTime.now(berlin)

        val day = scheduleItem.day!!
        if (day < 2) {
            return OffsetDateTime.now().plusMinutes(5)
        }

        val localTime = LocalDateTime.ofInstant(scheduleItem.time?.toInstant(), ZoneId.of("UTC")).toLocalTime();

        return now.plusDays((day - 1).toLong()).toLocalDate()
                .atTime(localTime)
                .atZone(berlin)
                .toOffsetDateTime()
    }
}