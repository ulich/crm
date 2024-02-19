package net.uich.mailcampaign.view.user

import net.uich.mailcampaign.entity.User
import net.uich.mailcampaign.view.main.MainView
import com.vaadin.flow.router.Route
import io.jmix.flowui.view.*

@Route(value = "users", layout = MainView::class)
@ViewController("User.list")
@ViewDescriptor("user-list-view.xml")
@LookupComponent("usersDataGrid")
@DialogMode(width = "64em")
open class UserListView : StandardListView<User>()
