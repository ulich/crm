package net.ulich.crm.view.main

import com.vaadin.flow.component.dependency.JsModule
import com.vaadin.flow.router.Route
import io.jmix.chartsflowui.component.Chart
import io.jmix.chartsflowui.data.item.MapDataItem
import io.jmix.chartsflowui.kit.component.model.DataSet
import io.jmix.chartsflowui.kit.data.chart.ListChartItems
import io.jmix.core.DataManager
import io.jmix.core.Messages
import io.jmix.flowui.app.main.StandardMainView
import io.jmix.flowui.view.Subscribe
import io.jmix.flowui.view.ViewComponent
import io.jmix.flowui.view.ViewController
import io.jmix.flowui.view.ViewDescriptor
import net.ulich.crm.entity.LeadStatus
import org.springframework.beans.factory.annotation.Autowired

@Route("")
@ViewController("MainView")
@ViewDescriptor("main-view.xml")
@JsModule("@vaadin/vaadin-lumo-styles/presets/compact.js")
open class MainView : StandardMainView() {
    @ViewComponent
    private lateinit var leadsChart: Chart

    @Autowired
    private lateinit var dataManager: DataManager

    @Autowired
    private lateinit var messages: Messages

    @Subscribe
    private fun onInit(event: InitEvent) {
        val statistics = dataManager.loadValues("select e.status, count(e) from Lead e group by e.status")
            .properties("status", "count")
            .list()

        val leadStatuses = statistics.map {
            val statusText = statusToMessage(it.getValue("status"))
            val count = it.getValue("count") as Long
            MapDataItem(mapOf("status" to statusText, "count" to count))
        }

        leadsChart.dataSet = DataSet().withSource(
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
