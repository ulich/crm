package net.ulich.crm.view.main

import io.jmix.chartsflowui.component.Chart
import io.jmix.chartsflowui.data.item.MapDataItem
import io.jmix.chartsflowui.kit.component.model.DataSet
import io.jmix.chartsflowui.kit.data.chart.ListChartItems
import io.jmix.core.DataManager
import io.jmix.core.Messages
import net.ulich.crm.entity.LeadStatus
import org.springframework.stereotype.Component

@Component
class LeadStatusChart(val dataManager: DataManager, val messages: Messages) {

    fun init(leadsByStatusChart: Chart) {
        val statistics = dataManager.loadValues("select e.status, count(e) from Lead e group by e.status")
            .properties("status", "count")
            .list()

        val leadStatuses = statistics.map {
            val statusText = statusToMessage(it.getValue("status"))
            val count = it.getValue("count") as Long
            MapDataItem(mapOf("status" to statusText, "count" to count))
        }

        leadsByStatusChart.dataSet = DataSet().withSource(
            DataSet.Source<MapDataItem>()
                .withDataProvider(ListChartItems(leadStatuses))
                .withCategoryField("status")
                .withValueField("count")
        )
    }

    private fun statusToMessage(statusId: String): String {
        val status = LeadStatus.fromId(statusId)
        return if (status != null) messages.getMessage(status) else statusId
    }
}