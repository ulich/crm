package net.ulich.crm.view.main

import com.vaadin.flow.component.dependency.JsModule
import com.vaadin.flow.router.Route
import io.jmix.chartsflowui.component.Chart
import io.jmix.core.DataManager
import io.jmix.core.Messages
import io.jmix.flowui.app.main.StandardMainView
import io.jmix.flowui.view.Subscribe
import io.jmix.flowui.view.ViewComponent
import io.jmix.flowui.view.ViewController
import io.jmix.flowui.view.ViewDescriptor
import org.springframework.beans.factory.annotation.Autowired

@Route("")
@ViewController("MainView")
@ViewDescriptor("main-view.xml")
@JsModule("@vaadin/vaadin-lumo-styles/presets/compact.js")
open class MainView : StandardMainView() {
    @ViewComponent
    private lateinit var leadsByStatusChart: Chart

    @ViewComponent
    private lateinit var customersByYearChart: Chart

    @ViewComponent
    private lateinit var orderedProductsByMonthChart: Chart

    @ViewComponent
    private lateinit var orderedProductsByYearChart: Chart

    @ViewComponent
    private lateinit var productAddOnsChart: Chart

    @Autowired
    private lateinit var dataManager: DataManager

    @Autowired
    private lateinit var messages: Messages

    @Subscribe
    private fun onInit(event: InitEvent) {
        LeadStatusChart(dataManager, messages).init(leadsByStatusChart)
        CustomersByYearChart(dataManager).init(customersByYearChart)
        ProductAddOnsChart(dataManager).init(productAddOnsChart)
        OrderedProductsByTimeChart(dataManager).init(orderedProductsByMonthChart, orderedProductsByYearChart)
    }
}
