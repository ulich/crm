package net.ulich.crm.entity

import io.jmix.core.DeletePolicy
import io.jmix.core.annotation.DeletedBy
import io.jmix.core.annotation.DeletedDate
import io.jmix.core.entity.annotation.JmixGeneratedValue
import io.jmix.core.entity.annotation.OnDelete
import io.jmix.core.entity.annotation.OnDeleteInverse
import io.jmix.core.metamodel.annotation.InstanceName
import io.jmix.core.metamodel.annotation.JmixEntity
import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import net.ulich.crm.time.AppTimeZone.Companion.BERLIN
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import java.time.*
import java.time.ZoneOffset.UTC
import java.time.temporal.ChronoUnit
import java.util.*

@JmixEntity
@Table(name = "RECURRING_EMAIL")
@Entity
open class RecurringEmail {
    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    var id: UUID? = null

    @OnDeleteInverse(DeletePolicy.DENY)
    @OnDelete(DeletePolicy.UNLINK)
    @JoinColumn(name = "EMAIL_TEMPLATE_ID", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @NotNull
    var emailTemplate: EmailTemplate? = null

    @Column(name = "INTERVAL_MONTHS", nullable = false)
    @NotNull
    var intervalMonths: Int? = null

    @Column(name = "TIME_", nullable = false)
    @Temporal(TemporalType.TIME)
    @NotNull
    var time: Date? = null

    @Column(name = "VERSION", nullable = false)
    @Version
    var version: Int? = null

    @CreatedBy
    @Column(name = "CREATED_BY")
    var createdBy: String? = null

    @CreatedDate
    @Column(name = "CREATED_DATE")
    var createdDate: OffsetDateTime? = null

    @LastModifiedBy
    @Column(name = "LAST_MODIFIED_BY")
    var lastModifiedBy: String? = null

    @LastModifiedDate
    @Column(name = "LAST_MODIFIED_DATE")
    var lastModifiedDate: OffsetDateTime? = null

    @DeletedBy
    @Column(name = "DELETED_BY")
    var deletedBy: String? = null

    @DeletedDate
    @Column(name = "DELETED_DATE")
    var deletedDate: OffsetDateTime? = null

    @InstanceName
    fun instanceName(): String {
        return emailTemplate!!.name + " (" + intervalMonths + "M)"
    }

    fun getLocalTime(): LocalTime {
        return LocalDateTime.ofInstant(time!!.toInstant(), UTC).toLocalTime()
    }

    fun calculateNextOcurrenceFrom(fromDate: LocalDate): ZonedDateTime {
        val scheduledSendDate = fromDate.plus(intervalMonths!!.toLong(), ChronoUnit.MONTHS)
        return ZonedDateTime.of(scheduledSendDate, getLocalTime(), BERLIN)
    }
}