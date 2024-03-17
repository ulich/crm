package net.ulich.crm.view.emailsignature

import com.vaadin.flow.router.Route
import io.jmix.flowui.view.EditedEntityContainer
import io.jmix.flowui.view.StandardDetailView
import io.jmix.flowui.view.ViewController
import io.jmix.flowui.view.ViewDescriptor
import net.ulich.crm.entity.EmailSignature
import net.ulich.crm.view.main.MainView

@Route(value = "emailSignatures/:id", layout = MainView::class)
@ViewController("EmailSignature.detail")
@ViewDescriptor("email-signature-detail-view.xml")
@EditedEntityContainer("emailSignatureDc")
class EmailSignatureDetailView : StandardDetailView<EmailSignature>() {
}