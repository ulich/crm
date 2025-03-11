package net.ulich.crm.view.main

import io.jmix.chartsflowui.component.Chart
import io.jmix.chartsflowui.kit.component.model.DataSet
import io.jmix.core.DataManager
import java.math.BigDecimal
import java.time.YearMonth

class OrderedProductsByTimeChart(val dataManager: DataManager) {

    fun init(byMonthChart: Chart, byYearChart: Chart) {
        val rowsFromDb = load()
        val rowsWithZeroCounts = fillZeroMonths(rowsFromDb)

        byYearChart.dataSet = toYearDataSet(rowsWithZeroCounts)
        byMonthChart.dataSet = toMonthDataSet(rowsWithZeroCounts)

        initDataZoom(byMonthChart)
    }

    private fun toMonthDataSet(rows: List<Row>): DataSet {
        val items = rows.map {
            ChartDataSetFactory.Item(it.toYearMonth().toChartString(), it.count)
        }

        return ChartDataSetFactory.create(items, "Anzahl")
    }

    private fun toYearDataSet(rows: List<Row>): DataSet {
        val rowsByYear = rows.groupBy { it.year }

        val items = rowsByYear.map { (year, rowsOfYear) ->
            ChartDataSetFactory.Item("$year", rowsOfYear.sumOf { it.count })
        }

        return ChartDataSetFactory.create(items, "Anzahl")
    }

    private fun load(): List<Row> {
        val items = dataManager.loadValues(
            """
                select extract(year from e.purchaseDate), extract(month from e.purchaseDate), count(e)
                from OrderedProduct e
                where e.product.isAddOn = false
                group by extract(year from e.purchaseDate), extract(month from e.purchaseDate)"""
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

        val firstProductDate = YearMonth.of(sortedRows.first().year, sortedRows.first().month)
        val startDate = firstProductDate.getStartOfQuarter()
        val endDate = YearMonth.now()

        val allMonths = generateSequence(startDate) { date ->
            if (date < endDate) date.plusMonths(1) else null
        }.toList()

        val monthMap = rows.associateBy { it.toYearMonth() }
        return allMonths.map { month ->
            monthMap[month] ?: Row(month.year, month.monthValue, 0L)
        }
    }

    private fun initDataZoom(byMonthChart: Chart) {
        val start = YearMonth.now().minusYears(2).toChartString()
        val end = YearMonth.now().toChartString()
        byMonthChart.dataZoom.forEach {
            it.startValue = start
            it.endValue = end
        }
    }

    class Row(val year: Int, val month: Int, val count: Long) {
        fun toYearMonth(): YearMonth {
            return YearMonth.of(year, month)
        }
    }

    fun YearMonth.toChartString(): String {
        val shortYear = this.year % 100
        return "${this.monthValue}'${shortYear}"
    }

    /**
     * 1 = Q1, 2 = Q2, 3 = Q3, 4 = Q4
     */
    fun YearMonth.getQuarterValue(): Int {
        return (this.monthValue - 1) / 3 + 0
    }

    fun YearMonth.getStartOfQuarter(): YearMonth {
        return YearMonth.of(this.year, (this.getQuarterValue() - 1) * 3 + 1)
    }

}
