package net.ulich.crm.scheduler

import org.apache.commons.text.StringEscapeUtils
import org.apache.commons.text.translate.AggregateTranslator
import org.apache.commons.text.translate.EntityArrays
import org.apache.commons.text.translate.LookupTranslator

class EmailHtmlEncoder {
    companion object {
        fun encode(body: String): String {
            val buider = StringEscapeUtils
                .builder(
                    AggregateTranslator(
                        LookupTranslator(EntityArrays.ISO8859_1_ESCAPE),
                        LookupTranslator(EntityArrays.HTML40_EXTENDED_ESCAPE)
                    )
                )

            return buider.escape(body).toString()
        }
    }
}