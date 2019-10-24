package com.alibaba.csp.sentinel.dashboard.runner;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;


@Component
public class StartedUpRunner implements ApplicationRunner {

    private Logger logger = LoggerFactory.getLogger(StartedUpRunner.class);

    @Autowired
    private ConfigurableApplicationContext context;

    @Value("${spring.application.name:dashboard}")
    private String applicationName;

    @Override
    public void run(ApplicationArguments args) {
        if (context.isActive()) {
            logger.info("  _   _   _   _   _   _   _   _");
            logger.info(" / \\ / \\ / \\ / \\ / \\ / \\ / \\ / \\");
            logger.info("( c | o | m | p | l | e | t | e )");
            logger.info(" \\_/ \\_/ \\_/ \\_/ \\_/ \\_/ \\_/ \\_/");
            logger.info("{} 启动完毕，时间：{}", applicationName, LocalDateTime.now());
        }
    }
}
