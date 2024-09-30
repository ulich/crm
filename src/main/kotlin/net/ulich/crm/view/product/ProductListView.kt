package net.ulich.crm.view.product

import com.vaadin.flow.router.Route
import io.jmix.flowui.view.*
import net.ulich.crm.entity.Product
import net.ulich.crm.view.main.MainView

@Route(value = "products", layout = MainView::class)
@ViewController("Product.list")
@ViewDescriptor("product-list-view.xml")
@LookupComponent("productsDataGrid")
@DialogMode(width = "64em")
class ProductListView : StandardListView<Product>() {
}