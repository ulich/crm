package net.ulich.crm.view.tag

import com.vaadin.flow.router.Route
import io.jmix.flowui.view.EditedEntityContainer
import io.jmix.flowui.view.StandardDetailView
import io.jmix.flowui.view.ViewController
import io.jmix.flowui.view.ViewDescriptor
import net.ulich.crm.entity.Tag
import net.ulich.crm.view.main.MainView

@Route(value = "tags/:id", layout = MainView::class)
@ViewController("Tag.detail")
@ViewDescriptor("tag-detail-view.xml")
@EditedEntityContainer("tagDc")
class TagDetailView : StandardDetailView<Tag>() {
}