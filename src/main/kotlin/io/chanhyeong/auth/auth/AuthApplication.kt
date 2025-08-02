package io.chanhyeong.auth.auth

import io.chanhyeong.auth.auth.config.JwtProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(JwtProperties::class)
class AuthApplication

fun main(args: Array<String>) {
	runApplication<AuthApplication>(*args)
}
