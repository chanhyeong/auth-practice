package io.chanhyeong.auth.auth.repository

import io.chanhyeong.auth.auth.entity.UserRole
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRoleRepository : CrudRepository<UserRole, Long> {
    fun findByUserId(userId: Long): List<UserRole>
    
    fun deleteByUserId(userId: Long)
}