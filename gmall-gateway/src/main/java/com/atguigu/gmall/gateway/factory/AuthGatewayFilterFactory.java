package com.atguigu.gmall.gateway.factory;

import com.atguigu.gmall.gateway.properties.JWTProperties;
import com.atguigu.gmall.gateway.util.JWTParserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.GatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.PrefixPathGatewayFilterFactory;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author wyl
 * @create 2020-07-03 17:01
 */
@Component
public class AuthGatewayFilterFactory extends AbstractGatewayFilterFactory<AuthGatewayFilterFactory.Config> {
    @Autowired
    private JWTParserUtil jwtParserUtil;
    @Autowired
    private JWTProperties jwtProperties;

    private final String IS_FORCE = "force";

    public AuthGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return Collections.singletonList(IS_FORCE);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return new AuthGatewayFilter(config.isForce());
    }

    public static class Config {
        private boolean force;

        public boolean isForce() {
            return force;
        }

        public void setForce(boolean force) {
            this.force = force;
        }
    }

    class AuthGatewayFilter implements GatewayFilter {
        private boolean force;

        public AuthGatewayFilter(boolean force) {
            this.force = force;
        }

        @Override
        public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
            // 是否需要强校验
            if (force) {
                // 校验失败则添加失败状态码
                if (!verificationCookieByForce(exchange, chain)) {
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                }
            }

            return chain.filter(exchange);
        }

        /**
         * 强制校验，成功则返回true
         * @param exchange
         * @param chain
         * @return
         */
        public boolean verificationCookieByForce(ServerWebExchange exchange, GatewayFilterChain chain) {
            MultiValueMap<String, HttpCookie> cookies;
            HttpCookie httpCookie;
            String token;
            if (CollectionUtils.isEmpty(cookies = exchange.getRequest().getCookies())
                    || (httpCookie = cookies.getFirst(jwtProperties.getCookieName())) == null
                    || StringUtils.isEmpty(token = httpCookie.getValue())
            ){
                return false;
            }

            try {
                jwtParserUtil.parseToken(token);
            } catch (Exception e) {
                return false;
            }
            return true;
        }
    }
}
