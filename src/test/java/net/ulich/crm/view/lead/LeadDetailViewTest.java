package net.ulich.crm.view.lead;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.assertj.core.api.Assertions.assertThat;

class LeadDetailViewTest {

    @Nested
    class CalculatePlannedSendDate {
        private final Date time = new GregorianCalendar(0, 0, 0, 8, 0, 0).getTime();

        @ParameterizedTest
        @CsvSource({
                "1, 2024-02-26",
                "2, 2024-02-27",
                "3, 2024-02-28",
                "4, 2024-02-29",
                "5, 2024-03-01",
                "6, 2024-03-04",
                "7, 2024-03-05"
        })
        void whenTodayIsMonday(int day, String expectedDate) {
            ZonedDateTime todayIsMonday = ZonedDateTime.parse("2024-02-26T00:00:00+01:00[Europe/Berlin]");

            var plannedDate = LeadDetailView.calculatePlannedSendDate(todayIsMonday, day, time);

            assertThat(plannedDate.toLocalDate()).isEqualTo(LocalDate.parse(expectedDate));
        }

        @ParameterizedTest
        @CsvSource({
                "1, 2024-02-22",
                "2, 2024-02-23",
                "3, 2024-02-26",
                "4, 2024-02-27",
                "5, 2024-02-28",
                "6, 2024-02-29",
                "7, 2024-03-01",
                "8, 2024-03-04"
        })
        void whenTodayIsThursday(int day, String expectedDate) {
            ZonedDateTime todayIsThursday = ZonedDateTime.parse("2024-02-22T00:00:00+01:00[Europe/Berlin]");

            var plannedDate = LeadDetailView.calculatePlannedSendDate(todayIsThursday, day, time);

            assertThat(plannedDate.toLocalDate()).isEqualTo(LocalDate.parse(expectedDate));
        }

        @ParameterizedTest
        @CsvSource({
                "1, 2024-02-24",
                "2, 2024-02-26",
                "3, 2024-02-27",
                "4, 2024-02-28",
                "5, 2024-02-29",
                "6, 2024-03-01",
                "7, 2024-03-04"
        })
        void whenTodayIsSaturday(int day, String expectedDate) {
            ZonedDateTime todayIsSaturday = ZonedDateTime.parse("2024-02-24T00:00:00+01:00[Europe/Berlin]");

            var plannedDate = LeadDetailView.calculatePlannedSendDate(todayIsSaturday, day, time);

            assertThat(plannedDate.toLocalDate()).isEqualTo(LocalDate.parse(expectedDate));
        }
    }
}
