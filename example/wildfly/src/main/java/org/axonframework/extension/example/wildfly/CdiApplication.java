package org.axonframework.extension.example.wildfly;

import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.eventhandling.interceptors.EventLoggingInterceptor;
import org.axonframework.extension.example.common.core.api.CreateAccountCommand;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

@Slf4j
@Singleton
@Startup
public class CdiApplication {

  @Inject
  private EventBus eventBus;

  @Inject
  private CommandGateway commandGateway;

  @PostConstruct
  public void run() {
    log.info("Initializing CDI application");
    eventBus.registerDispatchInterceptor(new EventLoggingInterceptor());
    commandGateway.send(new CreateAccountCommand("4711", 1000));
  }
}
