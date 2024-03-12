package net.ulich.crm.scheduler

import io.jmix.core.FileStorage
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class AttachmentPersonalizerTest {

    @Autowired
    private lateinit var attachmentPersonalizer: AttachmentPersonalizer

    @Autowired
    private lateinit var fileStorage: FileStorage

    @Test
    fun personalize() {
//        val template = fileStorage.saveStream("template.pdf", FileInputStream("/tmp/template.pdf"))
//        attachmentPersonalizer.personalize(template, Personalization(
//            salutation = "Sehr geehrter Herr Mustermann",
//            companyName = "Musterfirma GmbH",
//            firstName = "Max",
//            lastName = "Mustermän",
//            street = "Musterstraße 1",
//            postCode = "12345",
//            city = "Musterstadt"
//        ))
    }
}