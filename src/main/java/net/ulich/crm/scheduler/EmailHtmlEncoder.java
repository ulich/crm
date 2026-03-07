package net.ulich.crm.scheduler;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.commons.text.translate.AggregateTranslator;
import org.apache.commons.text.translate.EntityArrays;
import org.apache.commons.text.translate.LookupTranslator;

public final class EmailHtmlEncoder {
    private EmailHtmlEncoder() {
    }

    public static String encode(String body) {
        return StringEscapeUtils.builder(
                new AggregateTranslator(
                        new LookupTranslator(EntityArrays.ISO8859_1_ESCAPE),
                        new LookupTranslator(EntityArrays.HTML40_EXTENDED_ESCAPE)
                )
        ).escape(body).toString();
    }
}
