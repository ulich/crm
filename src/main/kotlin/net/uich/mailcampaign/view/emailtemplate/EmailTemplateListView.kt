package net.uich.mailcampaign.view.emailtemplate

import net.uich.mailcampaign.entity.EmailTemplate
import net.uich.mailcampaign.view.main.MainView
import com.vaadin.flow.router.Route
import io.jmix.flowui.view.*

@Route(value = "emailTemplates", layout = MainView::class)
@ViewController("EmailTemplate.list")
@ViewDescriptor("email-template-list-view.xml")
@LookupComponent("emailTemplatesDataGrid")
@DialogMode(width = "64em")
class EmailTemplateListView : StandardListView<EmailTemplate>() {
}