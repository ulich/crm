package net.ulich.crm.view.scheduleitem

import net.ulich.crm.entity.ScheduleItem
import net.ulich.crm.view.main.MainView
import com.vaadin.flow.router.Route
import io.jmix.flowui.view.*

@Route(value = "scheduleItems/:id", layout = MainView::class)
@ViewController("ScheduleItem.detail")
@ViewDescriptor("schedule-item-detail-view.xml")
@EditedEntityContainer("scheduleItemDc")
class ScheduleItemDetailView : StandardDetailView<ScheduleItem>() {
}