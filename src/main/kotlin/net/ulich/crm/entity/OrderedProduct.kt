package net.ulich.crm.entity

import io.jmix.core.DeletePolicy
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
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.*

@JmixEntity
@Table(
    name = "ORDERED_PRODUCT", indexes = [
        Index(name = "IDX_ORDERED_PRODUCT_LEAD", columnList = "LEAD_ID"),
        Index(name = "IDX_ORDERED_PRODUCT_PRODUCT", columnList = "PRODUCT_ID"),
    ]
)
@Entity
open class OrderedProduct {
    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    var id: UUID? = null

    @OnDeleteInverse(DeletePolicy.CASCADE)
    @JoinColumn(name = "LEAD_ID", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    var lead: Lead? = null

    @OnDeleteInverse(DeletePolicy.DENY)
    @JoinColumn(name = "PRODUCT_ID", nullable = false)
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    var product: Product? = null

    @Column(name = "PURCHASE_DATE", nullable = false)
    @NotNull
    var purchaseDate: LocalDate? = LocalDate.now()

    @Column(name = "DELIVERY_DATE", nullable = false)
    @NotNull
    var deliveryDate: LocalDate? = LocalDate.now()

    @Column(name = "TERM_END_DATE")
    var termEndDate: LocalDate? = null

    @Column(name = "SERIAL_NUMBER", length = 4096)
    var serialNumber: String? = null

    @OnDeleteInverse(DeletePolicy.DENY)
    @JoinTable(
        name = "ORDERED_PRODUCT_ADD_ONS",
        joinColumns = [JoinColumn(name = "ORDERED_PRODUCT_ID")],
        inverseJoinColumns = [JoinColumn(name = "ADD_ON_ID")]
    )
    @ManyToMany
    var productAddOns: List<ProductAddOn> = ArrayList()

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

    fun copyFrom(origin: OrderedProduct) {
        this.lead = origin.lead
        this.product = origin.product
        this.purchaseDate = origin.purchaseDate
        this.deliveryDate = origin.deliveryDate
        this.termEndDate = origin.termEndDate
        this.productAddOns = origin.productAddOns
    }

}
