package io.chanhyeong.auth.auth.security

import io.chanhyeong.auth.auth.config.JwtProperties
import io.jsonwebtoken.*
import io.jsonwebtoken.security.Keys
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.security.Key
import java.util.*

@Component
class JwtTokenProvider(
    private val jwtProperties: JwtProperties
) {
    private val key: Key = Keys.hmacShaKeyFor(
        jwtProperties.secret.toByteArray(StandardCharsets.UTF_8)
    )

    fun generateAccessToken(authentication: Authentication): String {
        val authorities = authentication.authorities.joinToString(",") { it.authority }
        val now = System.currentTimeMillis()
        
        return Jwts.builder()
            .subject(authentication.name)
            .claim("authorities", authorities)
            .claim("type", "access")
            .issuedAt(Date(now))
            .expiration(Date(now + jwtProperties.accessTokenExpiration))
            .signWith(key)
            .compact()
    }

    fun generateRefreshToken(username: String): String {
        val now = System.currentTimeMillis()
        
        return Jwts.builder()
            .subject(username)
            .claim("type", "refresh")
            .issuedAt(Date(now))
            .expiration(Date(now + jwtProperties.refreshTokenExpiration))
            .signWith(key)
            .compact()
    }

    fun getAuthentication(token: String): Authentication? {
        val claims = parseClaims(token)
        
        if (claims.subject == null) {
            return null
        }

        val authorities = claims["authorities"]?.toString()
            ?.split(",")
            ?.map { SimpleGrantedAuthority(it) }
            ?: emptyList()

        return JwtAuthenticationToken(claims.subject, authorities)
    }

    fun validateToken(token: String): Boolean {
        return try {
            parseClaims(token)
            true
        } catch (e: JwtException) {
            false
        } catch (e: IllegalArgumentException) {
            false
        }
    }

    fun getUsernameFromToken(token: String): String {
        return parseClaims(token).subject
    }

    fun getTokenType(token: String): String? {
        return parseClaims(token)["type"]?.toString()
    }
    
    fun getExpirationFromToken(token: String): Date {
        return parseClaims(token).expiration
    }

    private fun parseClaims(token: String): Claims {
        return Jwts.parser()
            .verifyWith(key as javax.crypto.SecretKey)
            .build()
            .parseSignedClaims(token)
            .payload
    }
}

class SimpleGrantedAuthority(private val authority: String) : GrantedAuthority {
    override fun getAuthority(): String = authority
    override fun equals(other: Any?): Boolean {
        return this === other || (other is GrantedAuthority && authority == other.authority)
    }
    override fun hashCode(): Int = authority.hashCode()
}

class JwtAuthenticationToken(
    private val principal: String,
    private val authorities: Collection<GrantedAuthority>
) : Authentication {
    private var authenticated = true
    
    override fun getName(): String = principal
    override fun getAuthorities(): Collection<GrantedAuthority> = authorities
    override fun getCredentials(): Any? = null
    override fun getDetails(): Any? = null
    override fun getPrincipal(): Any = principal
    override fun isAuthenticated(): Boolean = authenticated
    override fun setAuthenticated(isAuthenticated: Boolean) {
        this.authenticated = isAuthenticated
    }
}