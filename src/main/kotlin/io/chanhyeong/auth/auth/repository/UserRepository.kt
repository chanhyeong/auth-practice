package io.chanhyeong.auth.auth.repository

import io.chanhyeong.auth.auth.entity.User
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : CrudRepository<User, Long> {
    fun findByEmail(email: String): User?
    
    fun existsByEmail(email: String): Boolean
    
    @Query("SELECT COUNT(*) FROM users WHERE email = :email AND id != :id")
    fun countByEmailAndIdNot(email: String, id: Long): Int
}