package kr.hhplus.be.server.config

import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.config.Config
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RedissonConfig(
    @Value("\${spring.data.redis.host:localhost}")
    private val host: String,

    @Value("\${spring.data.redis.port:6379}")
    private val port: Int,

    @Value("\${spring.data.redis.password:}")
    private val password: String
) {

    @Bean
    fun redissonClient(): RedissonClient {
        val config = Config()
        val address = "redis://$host:$port"

        config.useSingleServer()
            .setAddress(address)
            .setConnectionPoolSize(10)
            .setConnectionMinimumIdleSize(2)
            .setConnectTimeout(3000)
            .setTimeout(3000)

        if (password.isNotEmpty()) {
            config.useSingleServer().setPassword(password)
        }

        return Redisson.create(config)
    }
}