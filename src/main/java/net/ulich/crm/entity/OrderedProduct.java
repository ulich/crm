package net.ulich.crm.entity;

import io.jmix.core.DeletePolicy;
import io.jmix.core.annotation.DeletedBy;
import io.jmix.core.annotation.DeletedDate;
import io.jmix.core.entity.annotation.JmixGeneratedValue;
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

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@JmixEntity
@Table(name = "ORDERED_PRODUCT", indexes = {
        @Index(name = "IDX_ORDERED_PRODUCT_LEAD", columnList = "LEAD_ID"),
        @Index(name = "IDX_ORDERED_PRODUCT_PRODUCT", columnList = "PRODUCT_ID")
})
public class OrderedProduct {

    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    private UUID id;

    @OnDeleteInverse(DeletePolicy.CASCADE)
    @JoinColumn(name = "LEAD_ID", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Lead lead;

    @OnDeleteInverse(DeletePolicy.DENY)
    @JoinColumn(name = "PRODUCT_ID", nullable = false)
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Product product;

    @Column(name = "PURCHASE_DATE", nullable = false)
    @NotNull
    private LocalDate purchaseDate = LocalDate.now();

    @Column(name = "DELIVERY_DATE", nullable = false)
    @NotNull
    private LocalDate deliveryDate = LocalDate.now();

    @Column(name = "TERM_END_DATE")
    private LocalDate termEndDate;

    @Column(name = "SERIAL_NUMBER", length = 4096)
    private String serialNumber;

    @ManyToMany
    @OnDeleteInverse(DeletePolicy.DENY)
    @JoinTable(
            name = "ORDERED_PRODUCT_ADD_ONS",
            joinColumns = @JoinColumn(name = "ORDERED_PRODUCT_ID"),
            inverseJoinColumns = @JoinColumn(name = "ADD_ON_ID")
    )
    private List<ProductAddOn> productAddOns = new ArrayList<>();

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

    public void copyFrom(OrderedProduct origin) {
        this.lead = origin.lead;
        this.product = origin.product;
        this.purchaseDate = origin.purchaseDate;
        this.deliveryDate = origin.deliveryDate;
        this.termEndDate = origin.termEndDate;
        this.productAddOns = origin.productAddOns;
    }

}
