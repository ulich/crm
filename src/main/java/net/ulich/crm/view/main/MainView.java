package net.ulich.crm.view.main;

import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.router.Route;
import io.jmix.chartsflowui.component.Chart;
import io.jmix.core.DataManager;
import io.jmix.core.Messages;
import io.jmix.flowui.app.main.StandardMainView;
import io.jmix.flowui.view.Subscribe;
import io.jmix.flowui.view.ViewComponent;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;
import org.springframework.beans.factory.annotation.Autowired;

@Route("")
@ViewController("MainView")
@ViewDescriptor("main-view.xml")
@JsModule("@vaadin/vaadin-lumo-styles/presets/compact.js")
public class MainView extends StandardMainView {
    @ViewComponent
    private Chart leadsByStatusChart;

    @ViewComponent
    private Chart customersByYearChart;

    @ViewComponent
    private Chart orderedProductsByMonthChart;

    @ViewComponent
    private Chart orderedProductsByYearChart;

    @ViewComponent
    private Chart productAddOnsChart;

    @Autowired
    private DataManager dataManager;

    @Autowired
    private Messages messages;

    @Subscribe
    private void onInit(InitEvent event) {
        new LeadStatusChart(dataManager, messages).init(leadsByStatusChart);
        new CustomersByYearChart(dataManager).init(customersByYearChart);
        new ProductAddOnsChart(dataManager).init(productAddOnsChart);
        new OrderedProductsByTimeChart(dataManager).init(orderedProductsByMonthChart, orderedProductsByYearChart);
    }
}
