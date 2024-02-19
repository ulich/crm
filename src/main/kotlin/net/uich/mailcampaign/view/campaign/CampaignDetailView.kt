package net.uich.mailcampaign.view.campaign

import net.uich.mailcampaign.entity.Campaign
import net.uich.mailcampaign.view.main.MainView
import com.vaadin.flow.router.Route
import io.jmix.flowui.view.*

@Route(value = "campaigns/:id", layout = MainView::class)
@ViewController("Campaign.detail")
@ViewDescriptor("campaign-detail-view.xml")
@EditedEntityContainer("campaignDc")
class CampaignDetailView : StandardDetailView<Campaign>() {
}