package net.uich.mailcampaign.view.lead

import net.uich.mailcampaign.entity.Lead
import net.uich.mailcampaign.view.main.MainView
import com.vaadin.flow.router.Route
import io.jmix.flowui.view.*

@Route(value = "leads", layout = MainView::class)
@ViewController("Lead.list")
@ViewDescriptor("lead-list-view.xml")
@LookupComponent("leadsDataGrid")
@DialogMode(width = "64em")
class LeadListView : StandardListView<Lead>() {
}