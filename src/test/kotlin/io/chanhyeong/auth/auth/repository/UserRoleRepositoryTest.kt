package io.chanhyeong.auth.auth.repository

import io.chanhyeong.auth.auth.entity.User
import io.chanhyeong.auth.auth.entity.UserRole
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest
import org.springframework.transaction.annotation.Transactional

@DataJdbcTest
@Transactional
class UserRoleRepositoryTest {

    @Autowired
    private lateinit var userRoleRepository: UserRoleRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @Test
    fun `save should create user role successfully`() {
        val user = User(
            email = "role-test@example.com",
            password = "hashedPassword",
            name = "Test User"
        )
        val savedUser = userRepository.save(user)

        val userRole = UserRole(
            userId = savedUser.id!!,
            role = "ADMIN"
        )

        val savedUserRole = userRoleRepository.save(userRole)

        assertThat(savedUserRole.id).isNotNull()
        assertThat(savedUserRole.userId).isEqualTo(savedUser.id)
        assertThat(savedUserRole.role).isEqualTo("ADMIN")
    }

    @Test
    fun `findById should return user role when exists`() {
        val user = User(
            email = "findbyid-role@example.com",
            password = "hashedPassword",
            name = "Test User"
        )
        val savedUser = userRepository.save(user)

        val userRole = UserRole(
            userId = savedUser.id!!,
            role = "USER"
        )
        val savedUserRole = userRoleRepository.save(userRole)

        val foundUserRole = userRoleRepository.findById(savedUserRole.id!!)

        assertThat(foundUserRole.isPresent).isTrue()
        assertThat(foundUserRole.get().userId).isEqualTo(savedUser.id)
        assertThat(foundUserRole.get().role).isEqualTo("USER")
    }

    @Test
    fun `findById should return empty when user role does not exist`() {
        val foundUserRole = userRoleRepository.findById(999L)

        assertThat(foundUserRole.isPresent).isFalse()
    }

    @Test
    fun `findByUserId should return user roles when exist`() {
        val user = User(
            email = "findbyuserid-role@example.com",
            password = "hashedPassword",
            name = "Test User"
        )
        val savedUser = userRepository.save(user)

        val userRole1 = UserRole(
            userId = savedUser.id!!,
            role = "USER"
        )
        val userRole2 = UserRole(
            userId = savedUser.id!!,
            role = "ADMIN"
        )

        userRoleRepository.save(userRole1)
        userRoleRepository.save(userRole2)

        val userRoles = userRoleRepository.findByUserId(savedUser.id!!)

        assertThat(userRoles).hasSize(2)
        assertThat(userRoles.map { it.role }).containsExactlyInAnyOrder("USER", "ADMIN")
        assertThat(userRoles.all { it.userId == savedUser.id }).isTrue()
    }

    @Test
    fun `findByUserId should return empty list when user has no roles`() {
        val userRoles = userRoleRepository.findByUserId(999L)

        assertThat(userRoles).isEmpty()
    }

    @Test
    fun `deleteByUserId should remove all user roles`() {
        val user = User(
            email = "delete-role@example.com",
            password = "hashedPassword",
            name = "Test User"
        )
        val savedUser = userRepository.save(user)

        val userRole1 = UserRole(
            userId = savedUser.id!!,
            role = "USER"
        )
        val userRole2 = UserRole(
            userId = savedUser.id!!,
            role = "ADMIN"
        )

        userRoleRepository.save(userRole1)
        userRoleRepository.save(userRole2)

        userRoleRepository.deleteByUserId(savedUser.id!!)

        val userRoles = userRoleRepository.findByUserId(savedUser.id!!)
        assertThat(userRoles).isEmpty()
    }

    @Test
    fun `findAll should return all user roles`() {
        userRoleRepository.deleteAll()

        val user1 = User(
            email = "findall-role1@example.com",
            password = "hashedPassword",
            name = "User 1"
        )
        val user2 = User(
            email = "findall-role2@example.com",
            password = "hashedPassword",
            name = "User 2"
        )

        val savedUser1 = userRepository.save(user1)
        val savedUser2 = userRepository.save(user2)

        val userRole1 = UserRole(
            userId = savedUser1.id!!,
            role = "USER"
        )
        val userRole2 = UserRole(
            userId = savedUser2.id!!,
            role = "ADMIN"
        )

        userRoleRepository.save(userRole1)
        userRoleRepository.save(userRole2)

        val allUserRoles = userRoleRepository.findAll()

        assertThat(allUserRoles).hasSize(2)
    }

    @Test
    fun `delete should remove specific user role`() {
        val user = User(
            email = "delete-specific-role@example.com",
            password = "hashedPassword",
            name = "Test User"
        )
        val savedUser = userRepository.save(user)

        val userRole = UserRole(
            userId = savedUser.id!!,
            role = "ADMIN"
        )
        val savedUserRole = userRoleRepository.save(userRole)

        userRoleRepository.deleteById(savedUserRole.id!!)

        val foundUserRole = userRoleRepository.findById(savedUserRole.id!!)
        assertThat(foundUserRole.isPresent).isFalse()
    }

    @Test
    fun `save should use default role when not specified`() {
        val user = User(
            email = "default-role@example.com",
            password = "hashedPassword",
            name = "Test User"
        )
        val savedUser = userRepository.save(user)

        val userRole = UserRole(
            userId = savedUser.id!!
        )

        val savedUserRole = userRoleRepository.save(userRole)

        assertThat(savedUserRole.role).isEqualTo("USER")
    }
}