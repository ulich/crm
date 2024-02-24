package net.ulich.crm.view.emailtemplate

import com.vaadin.flow.data.renderer.Renderer
import com.vaadin.flow.data.renderer.TextRenderer
import com.vaadin.flow.router.Route
import io.jmix.flowui.view.*
import net.ulich.crm.entity.EmailAttachment
import net.ulich.crm.entity.EmailTemplate
import net.ulich.crm.view.main.MainView

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