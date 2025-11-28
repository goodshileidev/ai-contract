package com.aibidcomposer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * AI标书智能创作平台 - 后端应用启动类
 *
 * 需求编号: REQ-JAVA-001
 * 创建时间: 2025-11-26
 * 创建者: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
 *
 * @author AIBidComposer Team
 * @since 1.0.0
 */
@Slf4j
@SpringBootApplication
public class ACApplication {

    public static void main(String[] args) {
        try {
            ConfigurableApplicationContext context = SpringApplication.run(ACApplication.class, args);
            Environment env = context.getEnvironment();

            String protocol = "http";
            if (env.getProperty("server.ssl.key-store") != null) {
                protocol = "https";
            }

            String serverPort = env.getProperty("server.port", "8080");
            String contextPath = env.getProperty("server.servlet.context-path", "");
            String hostAddress = InetAddress.getLocalHost().getHostAddress();

            log.info("""

                    ----------------------------------------------------------
                    \tAI标书智能创作平台启动成功！
                    ----------------------------------------------------------
                    \t应用名称: \t{}
                    \t应用版本: \t{}
                    \t本地访问: \t{}://localhost:{}{}
                    \t外部访问: \t{}://{}:{}{}
                    \tAPI文档:  \t{}://localhost:{}{}/swagger-ui.html
                    \t配置文件: \t{}
                    ----------------------------------------------------------
                    """,
                    env.getProperty("spring.application.name", "ac-app-backend"),
                    env.getProperty("app.version", "1.0.0-SNAPSHOT"),
                    protocol, serverPort, contextPath,
                    protocol, hostAddress, serverPort, contextPath,
                    protocol, serverPort, contextPath,
                    env.getActiveProfiles().length > 0 ?
                            String.join(", ", env.getActiveProfiles()) : "default"
            );

        } catch (UnknownHostException e) {
            log.error("启动失败: 无法获取主机地址", e);
        }
    }
}
