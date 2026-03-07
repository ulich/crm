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
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@Entity
@JmixEntity
@Table(name = "SCHEDULE_ITEM", indexes = {
        @Index(name = "IDX_EMAIL_SCHEDULE_EMAIL_TEMPLATE", columnList = "EMAIL_TEMPLATE_ID"),
        @Index(name = "IDX_SCHEDULE_ITEM_CAMPAIGN", columnList = "CAMPAIGN_ID")
})
public class ScheduleItem {

    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    private UUID id;

    @OnDeleteInverse(DeletePolicy.DENY)
    @OnDelete(DeletePolicy.UNLINK)
    @JoinColumn(name = "EMAIL_TEMPLATE_ID", nullable = false)
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private EmailTemplate emailTemplate;

    @Column(name = "DAY_", nullable = false)
    @NotNull
    private Integer day;

    @Column(name = "TIME_")
    @Temporal(TemporalType.TIME)
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

    @OnDeleteInverse(DeletePolicy.CASCADE)
    @JoinColumn(name = "CAMPAIGN_ID", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Campaign campaign;

}
