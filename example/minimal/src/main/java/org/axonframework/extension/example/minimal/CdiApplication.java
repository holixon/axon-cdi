package org.axonframework.extension.example.minimal;

import lombok.extern.slf4j.Slf4j;
import org.apache.deltaspike.cdise.api.CdiContainer;
import org.apache.deltaspike.cdise.api.CdiContainerLoader;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.eventhandling.interceptors.EventLoggingInterceptor;
import org.axonframework.extension.example.minimal.core.api.CreateAccountCommand;

import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceUnit;

@Slf4j
public class CdiApplication {

  // @EventStoreEnginePersistenceUnit
//  @PersistenceUnit
//  private EntityManager em;

  @Inject
  private EventBus eventBus;

  @Inject
  private CommandGateway commandGateway;

  public void run() {
    eventBus.registerDispatchInterceptor(new EventLoggingInterceptor());
    commandGateway.send(new CreateAccountCommand("4711", 1000));
    // commandBus.dispatch(asCommandMessage(new CreateAccountCommand("4711",
    // 1000)));
  }

}
