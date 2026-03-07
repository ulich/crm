package net.ulich.crm.view.scheduleitem;

import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.EditedEntityContainer;
import io.jmix.flowui.view.StandardDetailView;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;
import net.ulich.crm.entity.ScheduleItem;
import net.ulich.crm.view.main.MainView;

@Route(value = "scheduleItems/:id", layout = MainView.class)
@ViewController("ScheduleItem.detail")
@ViewDescriptor("schedule-item-detail-view.xml")
@EditedEntityContainer("scheduleItemDc")
public class ScheduleItemDetailView extends StandardDetailView<ScheduleItem> {
}
