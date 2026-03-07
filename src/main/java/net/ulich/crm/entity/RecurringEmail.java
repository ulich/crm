package net.ulich.crm.entity;

import io.jmix.core.DeletePolicy;
import io.jmix.core.annotation.DeletedBy;
import io.jmix.core.annotation.DeletedDate;
import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.entity.annotation.OnDelete;
import io.jmix.core.entity.annotation.OnDeleteInverse;
import io.jmix.core.metamodel.annotation.InstanceName;
import io.jmix.core.metamodel.annotation.JmixEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@Entity
@JmixEntity
@Table(name = "RECURRING_EMAIL")
public class RecurringEmail {

    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    private UUID id;

    @OnDeleteInverse(DeletePolicy.DENY)
    @OnDelete(DeletePolicy.UNLINK)
    @JoinColumn(name = "EMAIL_TEMPLATE_ID", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @NotNull
    private EmailTemplate emailTemplate;

    @Column(name = "INTERVAL_MONTHS", nullable = false)
    @NotNull
    private Integer intervalMonths;

    @Column(name = "TIME_", nullable = false)
    @Temporal(TemporalType.TIME)
    @NotNull
    private Date time;

    @Column(name = "VERSION", nullable = false)
    @Version
    private Integer version;

    @CreatedBy
    @Column(name = "CREATED_BY")
    private String createdBy;

    @CreatedDate
    @Column(name = "CREATED_DATE")
    private OffsetDateTime createdDate;

    @LastModifiedBy
    @Column(name = "LAST_MODIFIED_BY")
    private String lastModifiedBy;

    @LastModifiedDate
    @Column(name = "LAST_MODIFIED_DATE")
    private OffsetDateTime lastModifiedDate;

    @DeletedBy
    @Column(name = "DELETED_BY")
    private String deletedBy;

    @DeletedDate
    @Column(name = "DELETED_DATE")
    private OffsetDateTime deletedDate;

    @InstanceName
    public String instanceName() {
        return emailTemplate.getName() + " (" + intervalMonths + "M)";
    }

    public LocalTime getLocalTime() {
        return LocalDateTime.ofInstant(time.toInstant(), ZoneOffset.UTC).toLocalTime();
    }

    public ZonedDateTime calculateNextOcurrenceFrom(LocalDate fromDate) {
        LocalDate scheduledSendDate = fromDate;
        do {
            scheduledSendDate = scheduledSendDate.plus(intervalMonths.longValue(), ChronoUnit.MONTHS);
        } while (scheduledSendDate.isBefore(LocalDate.now()));

        return ZonedDateTime.of(scheduledSendDate, getLocalTime(), net.ulich.crm.time.AppTimeZone.BERLIN);

    }

}
