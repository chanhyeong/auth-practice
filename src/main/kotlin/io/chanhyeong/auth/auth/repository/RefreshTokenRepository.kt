package io.chanhyeong.auth.auth.repository

import io.chanhyeong.auth.auth.entity.RefreshToken
import org.springframework.data.jdbc.repository.query.Modifying
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface RefreshTokenRepository : CrudRepository<RefreshToken, Long> {
    fun findByToken(token: String): RefreshToken?
    
    fun findByUserId(userId: Long): List<RefreshToken>
    
    @Modifying
    @Query("DELETE FROM refresh_tokens WHERE user_id = :userId")
    fun deleteByUserId(userId: Long)
    
    @Modifying
    @Query("DELETE FROM refresh_tokens WHERE expires_at < :now")
    fun deleteExpiredTokens(now: LocalDateTime)
}