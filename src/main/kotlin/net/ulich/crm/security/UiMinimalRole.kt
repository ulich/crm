package net.ulich.crm.security

import io.jmix.core.entity.KeyValueEntity
import io.jmix.security.model.EntityAttributePolicyAction
import io.jmix.security.model.EntityPolicyAction
import io.jmix.security.model.SecurityScope
import io.jmix.security.role.annotation.EntityAttributePolicy
import io.jmix.security.role.annotation.EntityPolicy
import io.jmix.security.role.annotation.ResourceRole
import io.jmix.security.role.annotation.SpecificPolicy
import io.jmix.securityflowui.role.annotation.ViewPolicy

@ResourceRole(name = "UI: minimal access", code = UiMinimalRole.CODE, scope = [SecurityScope.UI])
interface UiMinimalRole {

    companion object {
        const val CODE = "ui-minimal"
    }

    @ViewPolicy(viewIds = ["MainView"])
    fun main()

    @ViewPolicy(viewIds = ["LoginView"])
    @SpecificPolicy(resources = ["ui.loginToUi"])
    fun login()

    @EntityPolicy(entityClass = KeyValueEntity::class, actions = [EntityPolicyAction.READ])
    @EntityAttributePolicy(
            entityClass = KeyValueEntity::class,
            attributes = ["*"],
            action = EntityAttributePolicyAction.VIEW
    )
    fun keyValueEntity()
}
