package net.ulich.crm.view.orderedproduct

import com.vaadin.flow.router.Route
import io.jmix.flowui.view.EditedEntityContainer
import io.jmix.flowui.view.StandardDetailView
import io.jmix.flowui.view.ViewController
import io.jmix.flowui.view.ViewDescriptor
import net.ulich.crm.entity.OrderedProduct
import net.ulich.crm.view.main.MainView

@Route(value = "orderedProducts/:id", layout = MainView::class)
@ViewController("OrderedProduct.detail")
@ViewDescriptor("ordered-product-detail-view.xml")
@EditedEntityContainer("orderedProductDc")
class OrderedProductDetailView : StandardDetailView<OrderedProduct>() {
}