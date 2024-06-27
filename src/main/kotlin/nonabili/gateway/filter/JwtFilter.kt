package nonabili.gateway.filter

import nonabili.gateway.util.jwt.JwtProvider
import org.slf4j.LoggerFactory
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class JwtFilter(val jwtProvider: JwtProvider) : AbstractGatewayFilterFactory<JwtFilter.Config>(Config::class.java) {
    val log = LoggerFactory.getLogger(javaClass)
    override fun apply(config: Config?): GatewayFilter {
        //Custom Pre Filter
        return GatewayFilter { exchange: ServerWebExchange, chain: GatewayFilterChain ->
            val request = exchange.request
            val response = exchange.response
            log.info("Custom Pre filter: request id -> {}", request.id)

            val token = request.headers["Authorization"]!!.get(0)
            request.mutate()
                .header("userIdx", jwtProvider.getUserIdxByToken(token))
                .header("role", jwtProvider.getRoleByToken(token))
                .build()

            chain.filter(exchange).then(Mono.fromRunnable { log.info("Custom Post filter: response code -> {}", response.statusCode) })
        }
    }

    class Config { //Put the configuration properties
    }
}