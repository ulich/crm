package net.uich.mailcampaign.view.lead

import net.uich.mailcampaign.entity.Lead
import net.uich.mailcampaign.view.main.MainView
import com.vaadin.flow.router.Route
import io.jmix.core.DataManager
import io.jmix.flowui.view.*
import org.springframework.beans.factory.annotation.Autowired

@Route(value = "leads/:id", layout = MainView::class)
@ViewController("Lead.detail")
@ViewDescriptor("lead-detail-view.xml")
@EditedEntityContainer("leadDc")
class LeadDetailView : StandardDetailView<Lead>() {
}