package net.ulich.crm.view.tag

import com.vaadin.flow.router.Route
import io.jmix.flowui.view.*
import net.ulich.crm.entity.Tag
import net.ulich.crm.view.main.MainView


@Route(value = "tags", layout = MainView::class)
@ViewController("Tag.list")
@ViewDescriptor("tag-list-view.xml")
@LookupComponent("tagsDataGrid")
@DialogMode(width = "64em")
class TagListView : StandardListView<Tag>() {
}