package net.ulich.crm.view.lead

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import java.time.ZonedDateTime
import java.util.*

class LeadDetailViewTest {

    @Nested
    inner class CalculatePlannedSendDate {
        val time = GregorianCalendar(0, 0, 0, 8, 0, 0).time

        @ParameterizedTest
        @CsvSource(
                "1, 2024-02-26", // mon
                "2, 2024-02-27", // tue
                "3, 2024-02-28", // wed
                "4, 2024-02-29", // thu
                "5, 2024-03-01", // fri
                "6, 2024-03-04", // mon
                "7, 2024-03-05", // tue
        )
        fun `when today is monday`(day: Int, expectedDate: String) {
            val todayIsThursday = ZonedDateTime.parse("2024-02-26T00:00:00+01:00[Europe/Berlin]")

            val plannedDate = LeadDetailView.calculatePlannedSendDate(todayIsThursday, day, time)

            assertThat(plannedDate.toLocalDate()).isEqualTo(expectedDate)
        }

        @ParameterizedTest
        @CsvSource(
                "1, 2024-02-22", // thu
                "2, 2024-02-23", // fri
                "3, 2024-02-26", // mon
                "4, 2024-02-27", // tue
                "5, 2024-02-28", // wed
                "6, 2024-02-29", // thu
                "7, 2024-03-01", // fri
                "8, 2024-03-04", // mo
                )
        fun `when today is thursday`(day: Int, expectedDate: String) {
            val todayIsThursday = ZonedDateTime.parse("2024-02-22T00:00:00+01:00[Europe/Berlin]")

            val plannedDate = LeadDetailView.calculatePlannedSendDate(todayIsThursday, day, time)

            assertThat(plannedDate.toLocalDate()).isEqualTo(expectedDate)
        }

        @ParameterizedTest
        @CsvSource(
                "1, 2024-02-24", // sat
                "2, 2024-02-26", // mon
                "3, 2024-02-27", // tue
                "4, 2024-02-28", // wed
                "5, 2024-02-29", // thu
                "6, 2024-03-01", // fri
                "7, 2024-03-04", // mon
        )
        fun `when today is saturday`(day: Int, expectedDate: String) {
            val todayIsThursday = ZonedDateTime.parse("2024-02-24T00:00:00+01:00[Europe/Berlin]")

            val plannedDate = LeadDetailView.calculatePlannedSendDate(todayIsThursday, day, time)

            assertThat(plannedDate.toLocalDate()).isEqualTo(expectedDate)
        }
    }
}