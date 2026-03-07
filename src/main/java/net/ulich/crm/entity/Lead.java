package net.ulich.crm.entity;

import io.jmix.core.DeletePolicy;
import io.jmix.core.annotation.DeletedBy;
import io.jmix.core.annotation.DeletedDate;
import io.jmix.core.entity.annotation.JmixGeneratedValue;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity(name = "Lead")
@JmixEntity
@Table(name = "LEAD_", indexes = {
        @Index(name = "IDX_LEAD__CAMPAIGN", columnList = "CAMPAIGN_ID")
})
public class Lead {

    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    private UUID id;

    @Column(name = "STATUS", nullable = false)
    @NotNull
    private String status = LeadStatus.NEW.getId();

    @OrderBy("plannedSendDate DESC")
    @Composition
    @OneToMany(mappedBy = "lead")
    private List<ScheduledEmail> scheduledEmails = new NotInstantiatedList<>();

    @OrderBy("deliveryDate ASC")
    @Composition
    @OneToMany(mappedBy = "lead")
    private List<OrderedProduct> orderedProducts = new NotInstantiatedList<>();

    @Column(name = "EMAIL", nullable = false, length = 512)
    @NotNull
    private String email;

    @Column(name = "PHONE_NUMBER")
    private String phoneNumber;

    @Column(name = "ALT_PHONE_NUMBER")
    private String alternativePhoneNumber;

    @Column(name = "GENDER", nullable = false)
    @NotNull
    private String gender;

    @Column(name = "COMPANY_NAME", length = 1024)
    private String companyName;

    @Column(name = "FIRST_NAME", length = 1024)
    private String firstName;

    @InstanceName
    @Column(name = "LAST_NAME", length = 1024)
    private String lastName;

    @Column(name = "STREET", length = 1024)
    private String street;

    @Column(name = "POST_CODE")
    private String postCode;

    @Column(name = "CITY", length = 1024)
    private String city;

    @Lob
    @Column(name = "NOTES")
    private String notes;

    @JoinColumn(name = "CAMPAIGN_ID", nullable = false)
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Campaign campaign;

    @ManyToMany
    @OnDeleteInverse(DeletePolicy.DENY)
    @JoinTable(
            name = "LEAD_TAGS",
            joinColumns = @JoinColumn(name = "LEAD_ID"),
            inverseJoinColumns = @JoinColumn(name = "TAG_ID")
    )
    private List<Tag> tags = new ArrayList<>();

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

    public LeadStatus getStatus() {
        return LeadStatus.fromId(status);
    }

    public void setStatus(LeadStatus status) {
        this.status = status.getId();
    }

    public Gender getGender() {
        return gender == null ? null : Gender.fromId(gender);
    }

    public void setGender(Gender gender) {
        this.gender = gender == null ? null : gender.getId();
    }

    public String salutation() {
        if (Gender.MR.getId().equals(gender)) {
            return "Sehr geehrter Herr " + lastName;
        } else if (Gender.MRS.getId().equals(gender)) {
            return "Sehr geehrte Frau " + lastName;
        }
        return "Sehr geehrte Damen und Herren";
    }

}
