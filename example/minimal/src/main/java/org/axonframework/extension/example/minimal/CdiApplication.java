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

// TODO add support for marked event storage engine PU.
//  @EventStoreEnginePersistenceUnit
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

  public static void main(final String[] args) throws Exception {
    final CdiContainer container = CdiContainerLoader.getCdiContainer();
    try {
      container.boot();
      final CdiApplication application = CDI.current().select(CdiApplication.class).get();
      application.run();
    } catch (Exception e) {
      log.error("Error in example", e);
    } finally {
      container.shutdown();
      log.info("Shutting down...");
    }
  }

}
