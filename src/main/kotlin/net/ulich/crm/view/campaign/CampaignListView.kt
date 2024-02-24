package net.ulich.crm.view.campaign

import net.ulich.crm.entity.Campaign
import net.ulich.crm.view.main.MainView
import com.vaadin.flow.router.Route
import io.jmix.flowui.view.*

@Route(value = "campaigns", layout = MainView::class)
@ViewController("Campaign.list")
@ViewDescriptor("campaign-list-view.xml")
@LookupComponent("campaignsDataGrid")
@DialogMode(width = "64em")
class CampaignListView : StandardListView<Campaign>() {
}