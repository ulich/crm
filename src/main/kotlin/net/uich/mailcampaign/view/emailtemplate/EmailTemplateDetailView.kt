package net.uich.mailcampaign.view.emailtemplate

import com.vaadin.flow.data.renderer.Renderer
import com.vaadin.flow.data.renderer.TextRenderer
import com.vaadin.flow.router.Route
import io.jmix.flowui.view.*
import net.uich.mailcampaign.entity.EmailAttachment
import net.uich.mailcampaign.entity.EmailTemplate
import net.uich.mailcampaign.view.main.MainView

@Route(value = "emailTemplates/:id", layout = MainView::class)
@ViewController("EmailTemplate.detail")
@ViewDescriptor("email-template-detail-view.xml")
@EditedEntityContainer("emailTemplateDc")
class EmailTemplateDetailView : StandardDetailView<EmailTemplate>() {

    @Supply(to = "attachmentsDataGrid.file", subject = "renderer")
    private fun attachmentsDataGridFileRenderer(): Renderer<EmailAttachment> {
        return TextRenderer { it.file?.fileName }
    }
}