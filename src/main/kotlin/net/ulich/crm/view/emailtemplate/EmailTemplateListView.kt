package net.ulich.crm.view.emailtemplate

import net.ulich.crm.entity.EmailTemplate
import net.ulich.crm.view.main.MainView
import com.vaadin.flow.router.Route
import io.jmix.flowui.view.*

@Route(value = "emailTemplates", layout = MainView::class)
@ViewController("EmailTemplate.list")
@ViewDescriptor("email-template-list-view.xml")
@LookupComponent("emailTemplatesDataGrid")
@DialogMode(width = "64em")
class EmailTemplateListView : StandardListView<EmailTemplate>() {
}