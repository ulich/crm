package net.ulich.crm.view.campaign;

import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.*;
import net.ulich.crm.entity.Campaign;
import net.ulich.crm.view.main.MainView;

@Route(value = "campaigns", layout = MainView.class)
@ViewController("Campaign.list")
@ViewDescriptor("campaign-list-view.xml")
@LookupComponent("campaignsDataGrid")
@DialogMode(width = "64em")
public class CampaignListView extends StandardListView<Campaign> {
}
