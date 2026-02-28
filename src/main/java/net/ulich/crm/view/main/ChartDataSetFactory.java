package net.ulich.crm.view.main;

import io.jmix.chartsflowui.data.item.MapDataItem;
import io.jmix.chartsflowui.kit.component.model.DataSet;
import io.jmix.chartsflowui.kit.data.chart.ListChartItems;

import java.util.List;
import java.util.Map;

public final class ChartDataSetFactory {

    private ChartDataSetFactory() {
    }

    public static class Item {
        private final String categoryValue;
        private final long value;

        public Item(String categoryValue, long value) {
            this.categoryValue = categoryValue;
            this.value = value;
        }

        public String getCategoryValue() {
            return categoryValue;
        }

        public long getValue() {
            return value;
        }
    }

    public static DataSet create(List<Item> items, String valueFieldName) {
        List<MapDataItem> months = items.stream()
                .map(it -> new MapDataItem(Map.of(
                        "category", it.getCategoryValue(),
                        valueFieldName, it.getValue()
                )))
                .toList();

        return new DataSet().withSource(
                new DataSet.Source<MapDataItem>()
                        .withDataProvider(new ListChartItems<>(months))
                        .withCategoryField("category")
                        .withValueFields(valueFieldName)
        );
    }
}
