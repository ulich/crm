package net.ulich.crm.view.lead

import net.ulich.crm.entity.Lead
import net.ulich.crm.view.main.MainView
import com.vaadin.flow.router.Route
import io.jmix.flowui.view.*

@Route(value = "leads", layout = MainView::class)
@ViewController("Lead.list")
@ViewDescriptor("lead-list-view.xml")
@LookupComponent("leadsDataGrid")
@DialogMode(width = "64em")
class LeadListView : StandardListView<Lead>() {
}