package net.uich.mailcampaign.view.campaign

import net.uich.mailcampaign.entity.Campaign
import net.uich.mailcampaign.view.main.MainView
import com.vaadin.flow.router.Route
import io.jmix.flowui.view.*

@Route(value = "campaigns", layout = MainView::class)
@ViewController("Campaign.list")
@ViewDescriptor("campaign-list-view.xml")
@LookupComponent("campaignsDataGrid")
@DialogMode(width = "64em")
class CampaignListView : StandardListView<Campaign>() {
}