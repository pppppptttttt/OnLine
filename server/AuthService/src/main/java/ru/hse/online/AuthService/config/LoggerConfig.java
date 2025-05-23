package ru.hse.online.AuthService.config;

import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class LoggerConfig {

  @Bean
  @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
  public Logger logger(InjectionPoint injectionPoint) {
    return LoggerFactory.getLogger(
        Objects.requireNonNull(injectionPoint.getMethodParameter())
            .getContainingClass());
  }
}
