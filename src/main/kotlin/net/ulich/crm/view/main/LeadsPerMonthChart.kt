package net.ulich.crm.view.main

import io.jmix.chartsflowui.component.Chart
import io.jmix.chartsflowui.data.item.MapDataItem
import io.jmix.chartsflowui.kit.component.model.DataSet
import io.jmix.chartsflowui.kit.data.chart.ListChartItems
import io.jmix.core.DataManager
import java.math.BigDecimal
import java.time.YearMonth

class LeadsPerMonthChart(val dataManager: DataManager) {

    fun init(leadsByTimeChart: Chart) {
        val rowsFromDb = load()
        val rowsWithZeroCounts = fillZeroMonths(rowsFromDb)

        val months = rowsWithZeroCounts.map {
            val shortYear = it.year % 100
            MapDataItem(
                mapOf(
                    "yearmonth" to "${it.month}'${shortYear}",
                    "Anzahl" to it.count,
                )
            )
        }

        leadsByTimeChart.dataSet = DataSet().withSource(
            DataSet.Source<MapDataItem>()
                .withDataProvider(ListChartItems(months))
                .withCategoryField("yearmonth")
                .withValueField("Anzahl")
        )
    }

    private fun load(): List<Row> {
        val items = dataManager.loadValues(
            """
                select extract(year from e.createdDate), extract(month from e.createdDate), count(e)
                from Lead e
                group by extract(year from e.createdDate), extract(month from e.createdDate)"""
        )
            .properties("year", "month", "count")
            .list()

        return items.map {
            val year = it.getValue("year") as BigDecimal
            val month = it.getValue("month") as BigDecimal
            val count = it.getValue("count") as Long
            Row(year.toInt(), month.toInt(), count)
        }
    }

    private fun fillZeroMonths(rows: List<Row>): List<Row> {
        if (rows.isEmpty()) {
            return rows
        }

        val sortedRows = rows.sortedBy { it.year * 12 + it.month }

        val startDate = YearMonth.of(sortedRows.first().year, sortedRows.first().month)
        val endDate = YearMonth.now()

        val completeMonths = generateSequence(startDate) { date ->
            if (date < endDate) date.plusMonths(1) else null
        }.toList()

        val monthMap = rows.associateBy { it.toYearMonth() }
        return completeMonths.map { month ->
            monthMap[month] ?: Row(month.year, month.monthValue, 0L)
        }
    }

    class Row(val year: Int, val month: Int, val count: Long) {
        fun toYearMonth(): YearMonth {
            return YearMonth.of(year, month)
        }
    }
}
