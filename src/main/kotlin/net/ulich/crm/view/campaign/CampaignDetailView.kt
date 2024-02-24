package net.ulich.crm.view.campaign

import net.ulich.crm.entity.Campaign
import net.ulich.crm.view.main.MainView
import com.vaadin.flow.router.Route
import io.jmix.flowui.view.*

@Route(value = "campaigns/:id", layout = MainView::class)
@ViewController("Campaign.detail")
@ViewDescriptor("campaign-detail-view.xml")
@EditedEntityContainer("campaignDc")
class CampaignDetailView : StandardDetailView<Campaign>() {
}