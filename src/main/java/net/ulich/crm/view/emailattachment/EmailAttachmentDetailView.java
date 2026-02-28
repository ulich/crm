package net.ulich.crm.view.emailattachment;

import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.EditedEntityContainer;
import io.jmix.flowui.view.StandardDetailView;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;
import net.ulich.crm.entity.EmailAttachment;
import net.ulich.crm.view.main.MainView;

@Route(value = "emailAttachments/:id", layout = MainView.class)
@ViewController("EmailAttachment.detail")
@ViewDescriptor("email-attachment-detail-view.xml")
@EditedEntityContainer("emailAttachmentDc")
public class EmailAttachmentDetailView extends StandardDetailView<EmailAttachment> {
}
