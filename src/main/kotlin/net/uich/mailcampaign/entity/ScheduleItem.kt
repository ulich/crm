package net.uich.mailcampaign.entity

import io.jmix.core.DeletePolicy
import io.jmix.core.annotation.DeletedBy
import io.jmix.core.annotation.DeletedDate
import io.jmix.core.entity.annotation.JmixGeneratedValue
import io.jmix.core.entity.annotation.OnDelete
import io.jmix.core.entity.annotation.OnDeleteInverse
import io.jmix.core.metamodel.annotation.JmixEntity
import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import java.time.OffsetDateTime
import java.util.*

@JmixEntity
@Table(name = "SCHEDULE_ITEM", indexes = [
    Index(name = "IDX_EMAIL_SCHEDULE_EMAIL_TEMPLATE", columnList = "EMAIL_TEMPLATE_ID"),
    Index(name = "IDX_SCHEDULE_ITEM_CAMPAIGN", columnList = "CAMPAIGN_ID")
])
@Entity
open class ScheduleItem {
    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    var id: UUID? = null

    @OnDeleteInverse(DeletePolicy.DENY)
    @OnDelete(DeletePolicy.UNLINK)
    @JoinColumn(name = "EMAIL_TEMPLATE_ID", nullable = false)
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    var emailTemplate: EmailTemplate? = null

    @Column(name = "DAY_", nullable = false)
    @NotNull
    var day: Int? = null

    @Column(name = "TIME_")
    @Temporal(TemporalType.TIME)
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

    @OnDeleteInverse(DeletePolicy.CASCADE)
    @JoinColumn(name = "CAMPAIGN_ID", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    var campaign: Campaign? = null
}