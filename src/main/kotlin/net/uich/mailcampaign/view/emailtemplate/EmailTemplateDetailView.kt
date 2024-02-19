package net.uich.mailcampaign.view.emailtemplate

import net.uich.mailcampaign.entity.EmailTemplate
import net.uich.mailcampaign.view.main.MainView
import com.vaadin.flow.router.Route
import io.jmix.flowui.view.*

@Route(value = "emailTemplates/:id", layout = MainView::class)
@ViewController("EmailTemplate.detail")
@ViewDescriptor("email-template-detail-view.xml")
@EditedEntityContainer("emailTemplateDc")
class EmailTemplateDetailView : StandardDetailView<EmailTemplate>() {
}