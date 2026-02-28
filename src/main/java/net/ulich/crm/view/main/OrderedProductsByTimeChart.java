package net.ulich.crm.view.main;

import io.jmix.chartsflowui.component.Chart;
import io.jmix.chartsflowui.kit.component.model.DataSet;
import io.jmix.core.DataManager;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.*;

public class OrderedProductsByTimeChart {
    private final DataManager dataManager;

    public OrderedProductsByTimeChart(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    public void init(Chart byMonthChart, Chart byYearChart) {
        List<Row> rowsFromDb = load();
        List<Row> rowsWithZeroCounts = fillZeroMonths(rowsFromDb);

        byYearChart.setDataSet(toYearDataSet(rowsWithZeroCounts));
        byMonthChart.setDataSet(toMonthDataSet(rowsWithZeroCounts));

        initDataZoom(byMonthChart);
    }

    private DataSet toMonthDataSet(List<Row> rows) {
        List<ChartDataSetFactory.Item> items = rows.stream()
                .map(it -> new ChartDataSetFactory.Item(toChartString(it.toYearMonth()), it.count()))
                .toList();

        return ChartDataSetFactory.create(items, "Anzahl");
    }

    private DataSet toYearDataSet(List<Row> rows) {
        Map<Integer, Long> rowsByYear = new HashMap<>();
        for (Row row : rows) {
            rowsByYear.merge(row.year(), row.count(), Long::sum);
        }

        List<ChartDataSetFactory.Item> items = rowsByYear.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> new ChartDataSetFactory.Item(String.valueOf(e.getKey()), e.getValue()))
                .toList();

        return ChartDataSetFactory.create(items, "Anzahl");
    }

    private List<Row> load() {
        var items = dataManager.loadValues(
                        """
                                select extract(year from e.purchaseDate), extract(month from e.purchaseDate), count(e)
                                from OrderedProduct e
                                where e.product.isAddOn = false
                                group by extract(year from e.purchaseDate), extract(month from e.purchaseDate)"""
                )
                .properties("year", "month", "count")
                .list();

        return items.stream()
                .map(it -> new Row(
                        ((BigDecimal) it.getValue("year")).intValue(),
                        ((BigDecimal) it.getValue("month")).intValue(),
                        (Long) it.getValue("count")
                ))
                .toList();
    }

    private List<Row> fillZeroMonths(List<Row> rows) {
        if (rows.isEmpty()) {
            return rows;
        }

        List<Row> sortedRows = rows.stream()
                .sorted(Comparator.comparingInt(it -> it.year() * 12 + it.month()))
                .toList();

        YearMonth firstProductDate = YearMonth.of(sortedRows.get(0).year(), sortedRows.get(0).month());
        YearMonth startDate = getStartOfQuarter(firstProductDate);
        YearMonth endDate = YearMonth.now();

        List<YearMonth> allMonths = new ArrayList<>();
        YearMonth cursor = startDate;
        while (!cursor.isAfter(endDate)) {
            allMonths.add(cursor);
            cursor = cursor.plusMonths(1);
        }

        Map<YearMonth, Row> monthMap = new HashMap<>();
        for (Row row : rows) {
            monthMap.put(row.toYearMonth(), row);
        }

        return allMonths.stream()
                .map(month -> monthMap.getOrDefault(month, new Row(month.getYear(), month.getMonthValue(), 0L)))
                .toList();
    }

    private void initDataZoom(Chart byMonthChart) {
        String start = toChartString(YearMonth.now().minusYears(2));
        String end = toChartString(YearMonth.now());
        byMonthChart.getDataZoom().forEach(it -> {
            it.setStartValue(start);
            it.setEndValue(end);
        });
    }

    public static String toChartString(YearMonth yearMonth) {
        int shortYear = yearMonth.getYear() % 100;
        return yearMonth.getMonthValue() + "'" + shortYear;
    }

    /**
     * 1 = Q1, 2 = Q2, 3 = Q3, 4 = Q4
     */
    public static int getQuarterValue(YearMonth yearMonth) {
        return (yearMonth.getMonthValue() - 1) / 3 + 0;
    }

    public static YearMonth getStartOfQuarter(YearMonth yearMonth) {
        return YearMonth.of(yearMonth.getYear(), (getQuarterValue(yearMonth) - 1) * 3 + 1);
    }

    static class Row {
        private final int year;
        private final int month;
        private final long count;

        Row(int year, int month, long count) {
            this.year = year;
            this.month = month;
            this.count = count;
        }

        int year() {
            return year;
        }

        int month() {
            return month;
        }

        long count() {
            return count;
        }

        YearMonth toYearMonth() {
            return YearMonth.of(year, month);
        }
    }
}
