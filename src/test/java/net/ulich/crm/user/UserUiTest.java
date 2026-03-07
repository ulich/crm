package net.ulich.crm.user;

import io.jmix.core.DataManager;
import io.jmix.flowui.ViewNavigators;
import io.jmix.flowui.component.UiComponentUtils;
import io.jmix.flowui.component.grid.DataGrid;
import io.jmix.flowui.component.textfield.JmixPasswordField;
import io.jmix.flowui.component.textfield.TypedTextField;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.testassist.FlowuiTestAssistConfiguration;
import io.jmix.flowui.testassist.UiTest;
import io.jmix.flowui.testassist.UiTestUtils;
import io.jmix.flowui.view.View;
import net.ulich.crm.CrmApplication;
import net.ulich.crm.entity.User;
import net.ulich.crm.view.user.UserDetailView;
import net.ulich.crm.view.user.UserListView;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Sample UI integration test for the User entity.
 */
@UiTest
@SpringBootTest(classes = {CrmApplication.class, FlowuiTestAssistConfiguration.class})
class UserUiTest {

    @Autowired
    private DataManager dataManager;

    @Autowired
    private ViewNavigators viewNavigators;

    @Test
    void test_createUser() {
        viewNavigators.view(UserListView.class).navigate();

        UserListView userListView = (UserListView) UiTestUtils.getCurrentView();

        JmixButton createBtn = findComponent(userListView, "createBtn");
        createBtn.click();

        UserDetailView userDetailView = (UserDetailView) UiTestUtils.getCurrentView();

        TypedTextField<String> usernameField = findComponent(userDetailView, "usernameField");
        String username = "test-user-" + System.currentTimeMillis();
        usernameField.setValue(username);

        JmixPasswordField passwordField = findComponent(userDetailView, "passwordField");
        passwordField.setValue("test-passwd");

        JmixPasswordField confirmPasswordField = findComponent(userDetailView, "confirmPasswordField");
        confirmPasswordField.setValue("test-passwd");

        JmixButton commitAndCloseBtn = findComponent(userDetailView, "saveAndCloseBtn");
        commitAndCloseBtn.click();

        userListView = (UserListView) UiTestUtils.getCurrentView();

        DataGrid<User> usersDataGrid = findComponent(userListView, "usersDataGrid");

        User user = usersDataGrid.getItems().getItems().stream()
                .filter(u -> username.equals(u.getUsername()))
                .findFirst()
                .orElse(null);

        Assertions.assertNotNull(user);
    }

    @AfterEach
    void tearDown() {
        dataManager.load(User.class)
                .query("e.username like ?1", "test-user-%")
                .list()
                .forEach(dataManager::remove);
    }

    @SuppressWarnings("unchecked")
    private <T> T findComponent(View<?> view, String componentId) {
        var component = UiComponentUtils.findComponent(view, componentId);
        Assertions.assertTrue(component.isPresent());
        return (T) component.get();
    }
}
