package net.ulich.crm.view.emailsignature

import com.vaadin.flow.router.Route
import io.jmix.flowui.view.*
import net.ulich.crm.entity.EmailSignature
import net.ulich.crm.view.main.MainView

@Route(value = "emailSignatures", layout = MainView::class)
@ViewController("EmailSignature.list")
@ViewDescriptor("email-signature-list-view.xml")
@LookupComponent("emailSignaturesDataGrid")
@DialogMode(width = "64em")
class EmailSignatureListView : StandardListView<EmailSignature>() {
}