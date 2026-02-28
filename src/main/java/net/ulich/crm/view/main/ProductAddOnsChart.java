package net.ulich.crm.view.main;

import io.jmix.chartsflowui.component.Chart;
import io.jmix.core.DataManager;

import java.util.List;

public class ProductAddOnsChart {
    private final DataManager dataManager;

    public ProductAddOnsChart(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    public void init(Chart chart) {
        var statistics = dataManager.loadValues(
                        """
                                select addon.name, count(op.id)
                                from OrderedProduct op
                                join op.productAddOns addon
                                group by addon.id
                                order by count(op)"""
                )
                .properties("name", "count")
                .list();

        List<ChartDataSetFactory.Item> items = statistics.stream()
                .map(it -> new ChartDataSetFactory.Item((String) it.getValue("name"), (Long) it.getValue("count")))
                .toList();

        chart.setDataSet(ChartDataSetFactory.create(items, "Anzahl"));
    }
}
