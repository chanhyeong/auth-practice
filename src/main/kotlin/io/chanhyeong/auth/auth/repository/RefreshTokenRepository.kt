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
    @Query("DELETE FROM REFRESH_TOKENS WHERE USER_ID = :userId")
    fun deleteByUserId(userId: Long)
    
    @Modifying
    @Query("DELETE FROM REFRESH_TOKENS WHERE EXPIRES_AT < :now")
    fun deleteExpiredTokens(now: LocalDateTime)
}