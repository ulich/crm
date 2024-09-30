package net.ulich.crm.view.product

import com.vaadin.flow.router.Route
import io.jmix.flowui.view.EditedEntityContainer
import io.jmix.flowui.view.StandardDetailView
import io.jmix.flowui.view.ViewController
import io.jmix.flowui.view.ViewDescriptor
import net.ulich.crm.entity.Product
import net.ulich.crm.view.main.MainView

@Route(value = "products/:id", layout = MainView::class)
@ViewController("Product.detail")
@ViewDescriptor("product-detail-view.xml")
@EditedEntityContainer("productsDc")
class ProductDetailView : StandardDetailView<Product>() {
}