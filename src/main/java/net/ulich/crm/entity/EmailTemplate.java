package net.ulich.crm.entity;

import io.jmix.core.DeletePolicy;
import io.jmix.core.annotation.DeletedBy;
import io.jmix.core.annotation.DeletedDate;
import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.entity.annotation.OnDelete;
import io.jmix.core.entity.annotation.OnDeleteInverse;
import io.jmix.core.metamodel.annotation.Composition;
import io.jmix.core.metamodel.annotation.InstanceName;
import io.jmix.core.metamodel.annotation.JmixEntity;
import io.jmix.data.impl.lazyloading.NotInstantiatedList;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;


@Getter
@Setter
@Entity
@JmixEntity
@Table(name = "EMAIL_TEMPLATE", indexes = {
        @Index(name = "IDX_EMAIL_TEMPLATE_SIGNATURE", columnList = "SIGNATURE_ID")
})
public class EmailTemplate {

    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    private UUID id;

    @InstanceName
    @Column(name = "NAME", nullable = false, length = 1024)
    @NotNull
    private String name;

    @Column(name = "SUBJECT", nullable = false, length = 1024)
    @NotNull
    private String subject;

    @Column(name = "CONTENT", nullable = false)
    @Lob
    @NotNull
    private String content;

    @OnDeleteInverse(DeletePolicy.DENY)
    @JoinColumn(name = "SIGNATURE_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private EmailSignature signature;

    @Column(name = "VERSION", nullable = false)
    @Version
    private Integer version;

    @OnDelete(DeletePolicy.CASCADE)
    @Composition
    @OneToMany(mappedBy = "emailTemplate")
    private List<EmailAttachment> attachments = new NotInstantiatedList<>();

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

}
