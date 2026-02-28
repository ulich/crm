package net.ulich.crm.view.campaign;

import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.EditedEntityContainer;
import io.jmix.flowui.view.StandardDetailView;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;
import net.ulich.crm.entity.Campaign;
import net.ulich.crm.view.main.MainView;

@Route(value = "campaigns/:id", layout = MainView.class)
@ViewController("Campaign.detail")
@ViewDescriptor("campaign-detail-view.xml")
@EditedEntityContainer("campaignDc")
public class CampaignDetailView extends StandardDetailView<Campaign> {
}
