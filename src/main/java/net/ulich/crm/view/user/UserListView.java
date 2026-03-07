package net.ulich.crm.view.user;

import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.*;
import net.ulich.crm.entity.User;
import net.ulich.crm.view.main.MainView;

@Route(value = "users", layout = MainView.class)
@ViewController("User.list")
@ViewDescriptor("user-list-view.xml")
@LookupComponent("usersDataGrid")
@DialogMode(width = "64em")
public class UserListView extends StandardListView<User> {
}
