package net.uich.mailcampaign.security

import net.uich.mailcampaign.entity.User
import io.jmix.securitydata.user.AbstractDatabaseUserRepository
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component

@Primary
@Component("UserRepository")
class DatabaseUserRepository : AbstractDatabaseUserRepository<User>() {

    override fun getUserClass(): Class<User> = User::class.java

    override fun initSystemUser(systemUser: User) {
        val authorities = grantedAuthoritiesBuilder
                .addResourceRole(FullAccessRole.CODE)
                .build()
        systemUser.authorities = authorities
    }

    override fun initAnonymousUser(anonymousUser: User) {

    }
}