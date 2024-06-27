package nonabili.gateway.util.jwt

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.security.Key
import kotlin.io.encoding.Base64

@Component
class JwtProvider(
    @Value("\${jwt.secret}") val secret: String,
    @Value("\${jwt.refresh-token-expires-time}") val refreshTokenExpiresTime: Long,
    @Value("\${jwt.access-token-expires-time}") val accessTokenExpiresTime: Long
): InitializingBean {
    lateinit var key: Key
    final val ROLE_KEY = "role" // todo 수정
    @OptIn(kotlin.io.encoding.ExperimentalEncodingApi::class)
    override fun afterPropertiesSet() {
        val keyByte = Base64.decode(secret)
        this.key = Keys.hmacShaKeyFor(keyByte)
    }
    val log = LoggerFactory.getLogger(javaClass)

    fun getUserIdxByToken(token: String): String {
        val idx = Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .body
            .subject
        return idx
    }

    fun getRoleByToken(token: String): String {
        val claims = Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .body
        return claims["role"].toString()
    }
    fun validateToken(token: String): Jws<Claims> {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token)
    }
}