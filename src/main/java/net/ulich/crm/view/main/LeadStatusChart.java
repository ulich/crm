package net.ulich.crm.view.main;

import io.jmix.chartsflowui.component.Chart;
import io.jmix.core.DataManager;
import io.jmix.core.Messages;
import net.ulich.crm.entity.LeadStatus;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LeadStatusChart {
    private final DataManager dataManager;
    private final Messages messages;

    public LeadStatusChart(DataManager dataManager, Messages messages) {
        this.dataManager = dataManager;
        this.messages = messages;
    }

    public void init(Chart chart) {
        var statistics = dataManager.loadValues(
                        """
                                select e.status, count(e)
                                from Lead e
                                group by e.status
                                order by count(e)"""
                )
                .properties("status", "count")
                .list();

        List<ChartDataSetFactory.Item> items = statistics.stream()
                .map(it -> new ChartDataSetFactory.Item(
                        statusToMessage((String) it.getValue("status")),
                        (Long) it.getValue("count")
                ))
                .toList();

        chart.setDataSet(ChartDataSetFactory.create(items, "Anzahl"));
    }

    private String statusToMessage(String statusId) {
        LeadStatus status = LeadStatus.fromId(statusId);
        return status != null ? messages.getMessage(status) : statusId;
    }
}
