package net.ulich.crm.view.productaddon

import com.vaadin.flow.router.Route
import io.jmix.flowui.view.EditedEntityContainer
import io.jmix.flowui.view.StandardDetailView
import io.jmix.flowui.view.ViewController
import io.jmix.flowui.view.ViewDescriptor
import net.ulich.crm.entity.ProductAddOn
import net.ulich.crm.view.main.MainView

@Route(value = "product-add-ons/:id", layout = MainView::class)
@ViewController("ProductAddOn.detail")
@ViewDescriptor("product-add-on-detail-view.xml")
@EditedEntityContainer("productAddOnsDc")
class ProductAddOnDetailView : StandardDetailView<ProductAddOn>() {
}