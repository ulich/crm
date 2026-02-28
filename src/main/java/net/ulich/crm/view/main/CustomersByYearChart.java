package net.ulich.crm.view.main;

import io.jmix.chartsflowui.component.Chart;
import io.jmix.core.DataManager;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CustomersByYearChart {
    private final DataManager dataManager;

    public CustomersByYearChart(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    public void init(Chart chart) {
        List<Row> rows = load();

        List<ChartDataSetFactory.Item> items = rows.stream()
                .map(it -> new ChartDataSetFactory.Item(String.valueOf(it.year()), it.count()))
                .toList();

        chart.setDataSet(ChartDataSetFactory.create(items, "Anzahl"));
    }

    private List<Row> load() {
        List<Row> rows = loadRows();
        return fillMissingYearsWithZero(rows);
    }

    private List<Row> fillMissingYearsWithZero(List<Row> rows) {
        if (rows.isEmpty()) {
            return List.of();
        }

        List<Row> sortedItems = rows.stream().sorted((a, b) -> Integer.compare(a.year(), b.year())).toList();
        int startYear = sortedItems.get(0).year();
        int endYear = YearMonth.now().getYear();

        List<Integer> allYears = new ArrayList<>();
        for (int year = startYear; year <= endYear; year++) {
            allYears.add(year);
        }

        Map<Integer, Row> yearMap = rows.stream().collect(Collectors.toMap(Row::year, r -> r, (a, b) -> a));
        return allYears.stream()
                .map(year -> yearMap.getOrDefault(year, new Row(year, 0L)))
                .toList();
    }

    private List<Row> loadRows() {
        var rows = dataManager.loadValues(
                        """
                                select extract(year from op.purchaseDate), count(distinct e)
                                from Lead e
                                join e.orderedProducts op
                                where op.product.isAddOn = false
                                group by extract(year from op.purchaseDate)"""
                )
                .properties("year", "count")
                .list();

        return rows.stream()
                .map(it -> new Row(((BigDecimal) it.getValue("year")).intValue(), (Long) it.getValue("count")))
                .toList();
    }

    record Row(int year, long count) {
    }
}
