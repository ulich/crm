package net.ulich.crm.entity;

import io.jmix.core.DeletePolicy;
import io.jmix.core.annotation.DeletedBy;
import io.jmix.core.annotation.DeletedDate;
import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.entity.annotation.OnDelete;
import io.jmix.core.entity.annotation.OnDeleteInverse;
import io.jmix.core.metamodel.annotation.JmixEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@JmixEntity
@Table(name = "SCHEDULED_EMAIL", indexes = {
        @Index(name = "IDX_SCHEDULED_EMAIL_LEAD", columnList = "LEAD_ID"),
        @Index(name = "IDX_SCHEDULED_EMAIL_EMAIL_TEMPLATE", columnList = "EMAIL_TEMPLATE_ID")
})
@Entity
public class ScheduledEmail {
    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    private UUID id;

    @OnDeleteInverse(DeletePolicy.CASCADE)
    @JoinColumn(name = "LEAD_ID", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Lead lead;

    @OnDeleteInverse(DeletePolicy.DENY)
    @JoinColumn(name = "EMAIL_TEMPLATE_ID", nullable = false)
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private EmailTemplate emailTemplate;

    @Column(name = "PLANNED_SEND_DATE", nullable = false)
    @NotNull
    private OffsetDateTime plannedSendDate;

    @Column(name = "SENT_DATE")
    private OffsetDateTime sentDate;

    @Column(name = "SOURCE_TYPE", nullable = false)
    @NotNull
    private String sourceType;

    @OnDeleteInverse(DeletePolicy.DENY)
    @OnDelete(DeletePolicy.UNLINK)
    @JoinColumn(name = "RECURRING_EMAIL_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private RecurringEmail recurringEmail;

    @Column(name = "IS_COMPLETE", nullable = false)
    @NotNull
    private Boolean isComplete = false;

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

    public ScheduledEmailSourceType getSourceType() {
        return ScheduledEmailSourceType.fromId(sourceType);
    }

    public void setSourceType(ScheduledEmailSourceType sourceType) {
        this.sourceType = sourceType.getId();
    }

    public Boolean getComplete() {
        return isComplete;
    }

    public Boolean isComplete() {
        return isComplete;
    }

    public void setComplete(Boolean complete) {
        isComplete = complete;
    }
}
