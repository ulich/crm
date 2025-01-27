package net.ulich.crm.view.main

import io.jmix.chartsflowui.component.Chart
import io.jmix.core.DataManager

class ProductAddOnsChart(val dataManager: DataManager) {

    fun init(chart: Chart) {
        val statistics = dataManager.loadValues(
            """
                select addon.name, count(op.id)
                from OrderedProduct op
                join op.productAddOns addon
                group by addon.id
                order by count(op)"""
        )
            .properties("name", "count")
            .list()

        val items = statistics.map {
            val name = it.getValue("name") as String
            val count = it.getValue("count") as Long
            ChartDataSetFactory.Item(name, count)
        }

        chart.dataSet = ChartDataSetFactory.create(items, "Anzahl")
    }
}
