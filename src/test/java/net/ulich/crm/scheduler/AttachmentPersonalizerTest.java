package net.ulich.crm.scheduler;

import io.jmix.core.FileStorage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AttachmentPersonalizerTest {

    @Autowired
    private AttachmentPersonalizer attachmentPersonalizer;

    @Autowired
    private FileStorage fileStorage;

    @Test
    void personalize() {
//        FileRef template = fileStorage.saveStream("template.pdf", new FileInputStream("/tmp/template.pdf"));
//        attachmentPersonalizer.personalize(template, new Personalization(
//            "Sehr geehrter Herr Mustermann",
//            "Musterfirma GmbH",
//            "Max",
//            "Mustermän",
//            "Musterstraße 1",
//            "12345",
//            "Musterstadt"
//        ));
    }
}
