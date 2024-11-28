package net.ulich.crm.entity

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
@Table(name = "SCHEDULED_EMAIL", indexes = [
    Index(name = "IDX_SCHEDULED_EMAIL_LEAD", columnList = "LEAD_ID"),
    Index(name = "IDX_SCHEDULED_EMAIL_EMAIL_TEMPLATE", columnList = "EMAIL_TEMPLATE_ID")
])
@Entity
open class ScheduledEmail {
    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    var id: UUID? = null

    @OnDeleteInverse(DeletePolicy.CASCADE)
    @JoinColumn(name = "LEAD_ID", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    var lead: Lead? = null

    @OnDeleteInverse(DeletePolicy.DENY)
    @JoinColumn(name = "EMAIL_TEMPLATE_ID", nullable = false)
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    var emailTemplate: EmailTemplate? = null

    @Column(name = "PLANNED_SEND_DATE", nullable = false)
    @NotNull
    var plannedSendDate: OffsetDateTime? = null

    @Column(name = "SENT_DATE")
    var sentDate: OffsetDateTime? = null

    @Column(name = "SOURCE_TYPE", nullable = false)
    @NotNull
    private var sourceType: String? = null

    @OnDeleteInverse(DeletePolicy.DENY)
    @OnDelete(DeletePolicy.UNLINK)
    @JoinColumn(name = "RECURRING_EMAIL_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    var recurringEmail: RecurringEmail? = null

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

    fun getSourceType(): ScheduledEmailSourceType? = sourceType.let { ScheduledEmailSourceType.fromId(it) }

    fun setSourceType(sourceType: ScheduledEmailSourceType) {
        this.sourceType = sourceType.id
    }
}