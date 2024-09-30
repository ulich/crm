package net.ulich.crm.view.productaddon

import com.vaadin.flow.router.Route
import io.jmix.flowui.view.*
import net.ulich.crm.entity.ProductAddOn
import net.ulich.crm.view.main.MainView

@Route(value = "product-add-ons", layout = MainView::class)
@ViewController("ProductAddOn.list")
@ViewDescriptor("product-add-on-list-view.xml")
@LookupComponent("productAddOnsDataGrid")
@DialogMode(width = "64em")
class ProductAddOnListView : StandardListView<ProductAddOn>() {
}