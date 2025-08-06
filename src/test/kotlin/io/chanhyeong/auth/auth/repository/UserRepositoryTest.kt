package io.chanhyeong.auth.auth.repository

import io.chanhyeong.auth.auth.entity.User
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@DataJdbcTest
@Transactional
class UserRepositoryTest {

    @Autowired
    private lateinit var userRepository: UserRepository

    @Test
    fun `save should create user successfully`() {
        val user = User(
            email = "save-test@example.com",
            password = "hashedPassword",
            name = "Test User"
        )

        val savedUser = userRepository.save(user)

        assertThat(savedUser.id).isNotNull()
        assertThat(savedUser.email).isEqualTo("save-test@example.com")
        assertThat(savedUser.name).isEqualTo("Test User")
        assertThat(savedUser.isActive).isTrue()
    }

    @Test
    fun `findById should return user when exists`() {
        val user = User(
            email = "findbyid-test@example.com",
            password = "hashedPassword",
            name = "Test User"
        )
        val savedUser = userRepository.save(user)

        val foundUser = userRepository.findById(savedUser.id!!)

        assertThat(foundUser.isPresent).isTrue()
        assertThat(foundUser.get().email).isEqualTo("findbyid-test@example.com")
        assertThat(foundUser.get().name).isEqualTo("Test User")
    }

    @Test
    fun `findById should return empty when user does not exist`() {
        val foundUser = userRepository.findById(999L)

        assertThat(foundUser.isPresent).isFalse()
    }

    @Test
    fun `findByEmail should return user when exists`() {
        val user = User(
            email = "findbyemail-test@example.com",
            password = "hashedPassword",
            name = "Test User"
        )
        userRepository.save(user)

        val foundUser = userRepository.findByEmail("findbyemail-test@example.com")

        assertThat(foundUser).isNotNull()
        assertThat(foundUser!!.email).isEqualTo("findbyemail-test@example.com")
        assertThat(foundUser.name).isEqualTo("Test User")
    }

    @Test
    fun `findByEmail should return null when user does not exist`() {
        val foundUser = userRepository.findByEmail("nonexistent@example.com")

        assertThat(foundUser).isNull()
    }

    @Test
    fun `existsByEmail should return true when user exists`() {
        val user = User(
            email = "exists-test@example.com",
            password = "hashedPassword",
            name = "Test User"
        )
        userRepository.save(user)

        val exists = userRepository.existsByEmail("exists-test@example.com")

        assertThat(exists).isTrue()
    }

    @Test
    fun `existsByEmail should return false when user does not exist`() {
        val exists = userRepository.existsByEmail("nonexistent@example.com")

        assertThat(exists).isFalse()
    }

    @Test
    fun `countByEmailAndIdNot should return 0 when no other user has same email`() {
        val user = User(
            email = "count0-test@example.com",
            password = "hashedPassword",
            name = "Test User"
        )
        val savedUser = userRepository.save(user)

        val count = userRepository.countByEmailAndIdNot("count0-test@example.com", savedUser.id!!)

        assertThat(count).isEqualTo(0)
    }

    @Test
    fun `countByEmailAndIdNot should return 0 when checking different email`() {
        val user1 = User(
            email = "count1-test@example.com",
            password = "hashedPassword1",
            name = "Test User 1"
        )
        userRepository.save(user1)
        
        val user2 = User(
            email = "count1-other@example.com",
            password = "hashedPassword2",
            name = "Test User 2"
        )
        val savedUser2 = userRepository.save(user2)

        val count = userRepository.countByEmailAndIdNot("count1-test@example.com", savedUser2.id!!)

        assertThat(count).isEqualTo(1)
    }

    @Test
    fun `findAll should return all users`() {
        userRepository.deleteAll()
        
        val user1 = User(
            email = "findall1@example.com",
            password = "hashedPassword1",
            name = "User 1"
        )
        val user2 = User(
            email = "findall2@example.com",
            password = "hashedPassword2",
            name = "User 2"
        )
        
        userRepository.save(user1)
        userRepository.save(user2)

        val allUsers = userRepository.findAll()

        assertThat(allUsers).hasSize(2)
    }

    @Test
    fun `delete should remove user`() {
        val user = User(
            email = "delete-test@example.com",
            password = "hashedPassword",
            name = "Test User"
        )
        val savedUser = userRepository.save(user)

        userRepository.deleteById(savedUser.id!!)

        val foundUser = userRepository.findById(savedUser.id!!)
        assertThat(foundUser.isPresent).isFalse()
    }
}