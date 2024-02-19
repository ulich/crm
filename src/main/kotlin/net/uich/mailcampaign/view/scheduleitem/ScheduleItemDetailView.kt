package net.uich.mailcampaign.view.scheduleitem

import net.uich.mailcampaign.entity.ScheduleItem
import net.uich.mailcampaign.view.main.MainView
import com.vaadin.flow.router.Route
import io.jmix.flowui.view.*

@Route(value = "scheduleItems/:id", layout = MainView::class)
@ViewController("ScheduleItem.detail")
@ViewDescriptor("schedule-item-detail-view.xml")
@EditedEntityContainer("scheduleItemDc")
class ScheduleItemDetailView : StandardDetailView<ScheduleItem>() {
}