package net.ulich.crm.view.main

import io.jmix.chartsflowui.data.item.MapDataItem
import io.jmix.chartsflowui.kit.component.model.DataSet
import io.jmix.chartsflowui.kit.data.chart.ListChartItems

class ChartDataSetFactory {

    class Item(val categoryValue: String, val value: Long);

    companion object {
        fun create(items: List<Item>, valueFieldName: String): DataSet {
            val months = items.map {
                MapDataItem(
                    mapOf(
                        "category" to it.categoryValue,
                        valueFieldName to it.value,
                    )
                )
            }

            return DataSet().withSource(
                DataSet.Source<MapDataItem>()
                    .withDataProvider(ListChartItems(months))
                    .withCategoryField("category")
                    .withValueFields(valueFieldName)
            )
        }
    }
}