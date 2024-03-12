package net.ulich.crm.scheduler

import io.jmix.core.FileRef
import io.jmix.core.FileStorage
import org.apache.commons.io.output.ByteArrayOutputStream
import org.apache.pdfbox.Loader
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode.APPEND
import org.apache.pdfbox.pdmodel.font.PDType0Font
import org.springframework.stereotype.Component
import java.awt.Color
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@Component
class AttachmentPersonalizer(private val fileStorage: FileStorage) {

    fun personalize(template: FileRef, personalization: Personalization): FileRef {
        fileStorage.openStream(template).use { stream ->
            val pdf: PDDocument = addTextToPdf(stream, personalization)

            return storeFile(pdf, template.fileName)
        }
    }

    private fun addTextToPdf(stream: InputStream, personalization: Personalization): PDDocument {
        val inByteStream = ByteArrayOutputStream()
        stream.copyTo(inByteStream)

        val pdf: PDDocument = Loader.loadPDF(inByteStream.toByteArray())
        val page = pdf.getPage(0)
        PDPageContentStream(pdf, page, APPEND, true).use {
            val font = PDType0Font.load(pdf, javaClass.getResourceAsStream("/Arial.ttf"))

            val writer = TextWriter(it, font)

            writer.x = 70f
            writer.y = 685f
            if (personalization.companyName != null) {
                writer.writeLn(personalization.companyName)
            }

            if (personalization.lastName != null) {
                if (personalization.firstName != null) {
                    writer.writeLn(personalization.firstName + " " + personalization.lastName)
                } else {
                    writer.writeLn(personalization.lastName)
                }
            }

            if (personalization.street != null && personalization.postCode != null && personalization.city != null) {
                writer.writeLn("")
                writer.writeLn(personalization.street)
                writer.writeLn(personalization.postCode + " " + personalization.city)
            }

            writer.x = 495f
            writer.y = 600f
            writer.writeLn(LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")))
        }
        return pdf
    }

    class TextWriter(val contentStream: PDPageContentStream, val font: PDType0Font) {
        var x = 0f
        var y = 0f

        fun writeLn(text: String) {
            contentStream.beginText()
            contentStream.setFont(font, 10f)
            contentStream.setNonStrokingColor(Color.BLACK)

            contentStream.newLineAtOffset(x, y)
            contentStream.showText(text)

            contentStream.endText()

            y -= 12f
        }
    }

    private fun storeFile(pdf: PDDocument, filename: String): FileRef {
        val outByteStream = ByteArrayOutputStream()
        pdf.save(outByteStream)
        return fileStorage.saveStream(
            filename,
            ByteArrayInputStream(outByteStream.toByteArray())
        )
    }
}