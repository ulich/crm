package net.ulich.crm.scheduler;

import io.jmix.core.FileRef;
import io.jmix.core.FileStorage;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode.APPEND;

@Component
public class AttachmentPersonalizer {
    private final FileStorage fileStorage;

    public AttachmentPersonalizer(FileStorage fileStorage) {
        this.fileStorage = fileStorage;
    }

    public FileRef personalize(FileRef template, Personalization personalization) {
        try (InputStream stream = fileStorage.openStream(template)) {
            PDDocument pdf = addTextToPdf(stream, personalization);
            return storeFile(pdf, template.getFileName());
        } catch (IOException e) {
            throw new IllegalStateException("Could not personalize attachment", e);
        }
    }

    private PDDocument addTextToPdf(InputStream stream, Personalization personalization) throws IOException {
        ByteArrayOutputStream inByteStream = new ByteArrayOutputStream();
        stream.transferTo(inByteStream);

        PDDocument pdf = Loader.loadPDF(inByteStream.toByteArray());
        var page = pdf.getPage(0);

        try (PDPageContentStream contentStream = new PDPageContentStream(pdf, page, APPEND, true)) {
            PDType0Font font = PDType0Font.load(pdf, AttachmentPersonalizer.class.getResourceAsStream("/Arial.ttf"));
            TextWriter writer = new TextWriter(contentStream, font);

            writer.setX(70f);
            writer.setY(685f);
            if (personalization.getCompanyName() != null) {
                writer.writeLn(personalization.getCompanyName());
            }

            if (personalization.getLastName() != null) {
                if (personalization.getFirstName() != null) {
                    writer.writeLn(personalization.getFirstName() + " " + personalization.getLastName());
                } else {
                    writer.writeLn(personalization.getLastName());
                }
            }

            if (personalization.getStreet() != null && personalization.getPostCode() != null && personalization.getCity() != null) {
                writer.writeLn("");
                writer.writeLn(personalization.getStreet());
                writer.writeLn(personalization.getPostCode() + " " + personalization.getCity());
            }

            writer.setX(495f);
            writer.setY(600f);
            writer.writeLn(LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        }

        return pdf;
    }

    private FileRef storeFile(PDDocument pdf, String filename) throws IOException {
        ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
        pdf.save(outByteStream);
        return fileStorage.saveStream(filename, new ByteArrayInputStream(outByteStream.toByteArray()));
    }

    static class TextWriter {
        private final PDPageContentStream contentStream;
        private final PDType0Font font;
        private float x;
        private float y;

        TextWriter(PDPageContentStream contentStream, PDType0Font font) {
            this.contentStream = contentStream;
            this.font = font;
        }

        void setX(float x) {
            this.x = x;
        }

        void setY(float y) {
            this.y = y;
        }

        void writeLn(String text) throws IOException {
            contentStream.beginText();
            contentStream.setFont(font, 10f);
            contentStream.setNonStrokingColor(Color.BLACK);
            contentStream.newLineAtOffset(x, y);
            contentStream.showText(text);
            contentStream.endText();
            y -= 12f;
        }
    }
}
