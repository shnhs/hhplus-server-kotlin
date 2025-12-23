package kr.hhplus.be.server

import jakarta.annotation.PreDestroy
import org.springframework.context.annotation.Configuration
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.utility.DockerImageName

@Configuration
class TestcontainersConfiguration {
    @PreDestroy
    fun preDestroy() {
        if (mySqlContainer.isRunning) mySqlContainer.stop()
    }

    companion object {
        val mySqlContainer: MySQLContainer<*> = MySQLContainer(DockerImageName.parse("mysql:8.0"))
            .withDatabaseName("hhplus")
            .withUsername("test")
            .withPassword("test")
            .apply {
                start()
            }

        val redisContainer: GenericContainer<*> = GenericContainer(DockerImageName.parse("redis:7-alpine"))
            .withExposedPorts(6379)
            .apply {
                start()
            }

        init {
            // MySql 설정
            System.setProperty(
                "spring.datasource.url",
                mySqlContainer.getJdbcUrl() + "?characterEncoding=UTF-8&serverTimezone=UTC"
            )
            System.setProperty("spring.datasource.username", mySqlContainer.username)
            System.setProperty("spring.datasource.password", mySqlContainer.password)

            // Redis 설정 - getMappedPort()로 동적 포트 가져오기
            val redisHost = redisContainer.host
            val redisPort = redisContainer.getMappedPort(6379)  // 동적으로 할당된 호스트 포트

            System.setProperty("spring.data.redis.host", redisHost)
            System.setProperty("spring.data.redis.port", redisPort.toString())

            println("=".repeat(50))
            println("Test Redis Container Started!")
            println("Host: $redisHost")
            println("Port: $redisPort")
            println("=".repeat(50))
        }
    }
}
