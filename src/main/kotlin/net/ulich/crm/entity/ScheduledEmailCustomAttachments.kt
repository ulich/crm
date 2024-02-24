package net.ulich.crm.entity

import io.jmix.core.DeletePolicy
import io.jmix.core.FileRef
import io.jmix.core.annotation.DeletedBy
import io.jmix.core.annotation.DeletedDate
import io.jmix.core.entity.annotation.JmixGeneratedValue
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
@Table(name = "SCHEDULED_EMAIL_CUSTOM_ATTACHMENTS", indexes = [
    Index(name = "IDX_SCHEDULED_EMAIL_CUSTOM_ATTACHMENTS_SCHEDULED_EMAIL", columnList = "SCHEDULED_EMAIL_ID")
])
@Entity
open class ScheduledEmailCustomAttachments {
    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    var id: UUID? = null

    @Column(name = "FILE_", nullable = false, length = 1024)
    @NotNull
    var file: FileRef? = null

    @Column(name = "VERSION", nullable = false)
    @Version
    var version: Int? = null

    @LastModifiedBy
    @Column(name = "LAST_MODIFIED_BY")
    var lastModifiedBy: String? = null

    @LastModifiedDate
    @Column(name = "LAST_MODIFIED_DATE")
    var lastModifiedDate: OffsetDateTime? = null

    @CreatedBy
    @Column(name = "CREATED_BY")
    var createdBy: String? = null

    @CreatedDate
    @Column(name = "CREATED_DATE")
    var createdDate: OffsetDateTime? = null

    @DeletedBy
    @Column(name = "DELETED_BY")
    var deletedBy: String? = null

    @DeletedDate
    @Column(name = "DELETED_DATE")
    var deletedDate: OffsetDateTime? = null

    @OnDeleteInverse(DeletePolicy.CASCADE)
    @JoinColumn(name = "SCHEDULED_EMAIL_ID", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    var scheduledEmail: ScheduledEmail? = null

}