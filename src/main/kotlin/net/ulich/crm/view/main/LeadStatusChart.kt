package net.ulich.crm.view.main

import io.jmix.chartsflowui.component.Chart
import io.jmix.core.DataManager
import io.jmix.core.Messages
import net.ulich.crm.entity.LeadStatus
import org.springframework.stereotype.Component

@Component
class LeadStatusChart(val dataManager: DataManager, val messages: Messages) {

    fun init(chart: Chart) {
        val statistics = dataManager.loadValues(
            """
                select e.status, count(e)
                from Lead e
                group by e.status
                order by count(e)"""
        )
            .properties("status", "count")
            .list()

        val items = statistics.map {
            val statusText = statusToMessage(it.getValue("status"))
            val count = it.getValue("count") as Long
            ChartDataSetFactory.Item(statusText, count)
        }

        chart.dataSet = ChartDataSetFactory.create(items, "Anzahl")
    }

    private fun statusToMessage(statusId: String): String {
        val status = LeadStatus.fromId(statusId)
        return if (status != null) messages.getMessage(status) else statusId
    }
}