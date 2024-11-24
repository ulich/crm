package net.ulich.crm.entity

import io.jmix.core.DeletePolicy
import io.jmix.core.annotation.DeletedBy
import io.jmix.core.annotation.DeletedDate
import io.jmix.core.entity.annotation.JmixGeneratedValue
import io.jmix.core.entity.annotation.OnDelete
import io.jmix.core.entity.annotation.OnDeleteInverse
import io.jmix.core.metamodel.annotation.Composition
import io.jmix.core.metamodel.annotation.InstanceName
import io.jmix.core.metamodel.annotation.JmixEntity
import io.jmix.data.impl.lazyloading.NotInstantiatedList
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import java.time.OffsetDateTime
import java.util.*

@JmixEntity
@Table(
    name = "EMAIL_TEMPLATE", indexes = [
        Index(name = "IDX_EMAIL_TEMPLATE_SIGNATURE", columnList = "SIGNATURE_ID")
    ]
)
@Entity
@Suppress("JmixEntityFieldWithoutAnnotation")
open class EmailTemplate(
    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    var id: UUID,

    @InstanceName
    @Column(name = "NAME", nullable = false, length = 1024)
    var name: String,

    @Column(name = "SUBJECT", nullable = false, length = 1024)
    var subject: String,

    @Column(name = "CONTENT", nullable = false)
    @Lob
    var content: String,

    @OnDeleteInverse(DeletePolicy.DENY)
    @JoinColumn(name = "SIGNATURE_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    var signature: EmailSignature? = null,

    @Column(name = "VERSION", nullable = false)
    @Version
    var version: Int? = null,

    @OnDelete(DeletePolicy.CASCADE)
    @Composition
    @OneToMany(mappedBy = "emailTemplate")
    var attachments: MutableList<EmailAttachment> = NotInstantiatedList(),

    @CreatedBy
    @Column(name = "CREATED_BY")
    var createdBy: String? = null,

    @CreatedDate
    @Column(name = "CREATED_DATE")
    var createdDate: OffsetDateTime? = null,

    @LastModifiedBy
    @Column(name = "LAST_MODIFIED_BY")
    var lastModifiedBy: String? = null,

    @LastModifiedDate
    @Column(name = "LAST_MODIFIED_DATE")
    var lastModifiedDate: OffsetDateTime? = null,

    @DeletedBy
    @Column(name = "DELETED_BY")
    var deletedBy: String? = null,

    @DeletedDate
    @Column(name = "DELETED_DATE")
    var deletedDate: OffsetDateTime? = null,
)