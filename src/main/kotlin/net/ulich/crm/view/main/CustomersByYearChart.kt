package net.ulich.crm.view.main

import io.jmix.chartsflowui.component.Chart
import io.jmix.core.DataManager
import java.math.BigDecimal
import java.time.YearMonth

class CustomersByYearChart(val dataManager: DataManager) {

    fun init(chart: Chart) {
        val rows = load()

        val items = rows.map {
            ChartDataSetFactory.Item("${it.year}", it.count)
        }

        chart.dataSet = ChartDataSetFactory.create(items, "Anzahl")
    }

    private fun load(): List<Row> {
        val rows = loadRows()
        return fillMissingYearsWithZero(rows)
    }

    private fun fillMissingYearsWithZero(rows: List<Row>): List<Row> {
        if (rows.isEmpty()) {
            return emptyList()
        }

        val sortedItems = rows.sortedBy { it.year }
        val startYear = sortedItems.first().year
        val endYear = YearMonth.now().year

        val allYears = generateSequence(startYear) { year ->
            if (year < endYear) year + 1 else null
        }.toList()

        val yearMap = rows.associateBy { it.year }
        return allYears.map { year ->
            yearMap[year] ?: Row(year, 0L)
        }
    }

    private fun loadRows(): List<Row> {
        val rows = dataManager.loadValues(
            """
                    select extract(year from e.createdDate), count(distinct e)
                    from Lead e
                    join e.orderedProducts op
                    group by extract(year from e.createdDate)"""
        )
            .properties("year", "count")
            .list()

        return rows.map {
            val year = it.getValue("year") as BigDecimal
            val count = it.getValue("count") as Long
            Row(year.toInt(), count)
        }
    }

    class Row(val year: Int, val count: Long)
}
