package org.axonframework.cdi.extension;

import lombok.extern.slf4j.Slf4j;
import org.axonframework.cdi.stereotype.Aggregate;
import org.axonframework.cdi.stereotype.EventStoreEnginePersistenceUnit;
import org.axonframework.cdi.stereotype.SubscribingEventProcessor;
import org.axonframework.cdi.util.CDIUtils;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.common.Registration;
import org.axonframework.common.jpa.EntityManagerProvider;
import org.axonframework.common.transaction.LoggingTransactionManager;
import org.axonframework.common.transaction.TransactionManager;
import org.axonframework.config.Configuration;
import org.axonframework.config.Configurer;
import org.axonframework.config.DefaultConfigurer;
import org.axonframework.config.EventHandlingConfiguration;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.EventMessage;
import org.axonframework.eventhandling.MessageProcessor;
import org.axonframework.eventhandling.tokenstore.TokenStore;
import org.axonframework.eventsourcing.eventstore.EventStorageEngine;
import org.axonframework.messaging.SubscribableEventMessageSource;
import org.axonframework.serialization.Serializer;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Destroyed;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.*;
import javax.persistence.PersistenceUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Main CDI Extension class reponsible for collecting CDI beans and setting up Axon Configuration.
 */
@Slf4j
public class AxonCdiExtension implements Extension {

  private final List<Class<?>> aggregates = new ArrayList<>();
  private final List<Bean<?>> eventHandlers = new ArrayList<>();
  private final Map<String, Producer<SubscribableEventMessageSource>> subscribableEventMessageSourceProducers = new HashMap<>();
  private final List<Registration> messageProcessorSubscriptions = new ArrayList<>();
  private Producer<EventStorageEngine> eventStorageEngineProducer;
  private Producer<Serializer> serializerProducer;
  private Producer<EventBus> eventBusProducer;
  private Producer<CommandBus> commandBusProducer;
  private Producer<MessageProcessor> messsageProcessorProducer;
  private Producer<EventHandlingConfiguration> eventHandlingConfigurationProducer;
  private Producer<Configurer> configurerProducer;
  private Producer<TransactionManager> txManagerProducer;
  private Producer<EntityManagerProvider> entityManagerProviderProducer;
  private Producer<TokenStore> tokenStoreProducer;

  /**
   * Scans all annotated types with {@link Aggregate} annotation and collects them for registration.
   *
   * @param pat annotated type processing event.
   */
  <T> void processAggregate(@Observes @WithAnnotations({Aggregate.class}) final ProcessAnnotatedType<T> pat) {
    final Class<?> clazz = pat.getAnnotatedType().getJavaClass();
    log.debug("Found aggregate {}", clazz);
    aggregates.add(clazz);
  }

  /**
   * Scans for event storage engine producer.
   *
   * @param pp process producer event.
   * @param bm bean manager.
   */
  <T> void processProducerEventStorageEngine(@Observes final ProcessProducer<T, EventStorageEngine> pp, final BeanManager bm) {
    log.debug("Producer for EventStorageEngine found: {}.", pp.getProducer());
    this.eventStorageEngineProducer = pp.getProducer();
  }

  /**
   * Scans for configurer producer.
   *
   * @param pp process producer event.
   * @param bm bean manager.
   */
  <T> void processProducerConfigurer(@Observes final ProcessProducer<T, Configurer> pp, final BeanManager bm) {
    log.debug("Producer for Configurer found: {}.", pp.getProducer());
    this.configurerProducer = pp.getProducer();
  }

  /**
   * Scans for tx manager producer.
   *
   * @param pp process producer event.
   * @param bm bean manager.
   */
  <T> void processProducerTransactionManager(@Observes final ProcessProducer<T, TransactionManager> pp, final BeanManager bm) {
    log.debug("Producer for TransactionManager found: {}.", pp.getProducer());
    this.txManagerProducer = pp.getProducer();
  }

  /**
   * Scans for serializer producer.
   *
   * @param pp process producer event.
   * @param bm bean manager.
   */
  <T> void processProducerSerializer(@Observes final ProcessProducer<T, Serializer> pp, final BeanManager bm) {
    log.debug("Producer for Serializer found: {}.", pp.getProducer());
    this.serializerProducer = pp.getProducer();
  }

  /**
   * Scans for event handling configuration producer.
   *
   * @param pp process producer event.
   * @param bm bean manager.
   */
  <T> void processProducerEventHandlingConfiguration(@Observes final ProcessProducer<T, EventHandlingConfiguration> pp, final BeanManager bm) {
    log.debug("Producer for EventHandlingConfiguration found: {}.", pp.getProducer());
    this.eventHandlingConfigurationProducer = pp.getProducer();
  }

  /**
   * Scans for event bus producer.
   *
   * @param pp process producer event.
   * @param bm bean manager.
   */
  <T> void processProducerEventBus(@Observes final ProcessProducer<T, EventBus> pp, final BeanManager bm) {
    log.debug("Producer for EventBus found: {}.", pp.getProducer());
    this.eventBusProducer = pp.getProducer();
  }

  /**
   * Scans for command bus producer.
   *
   * @param pp process producer event.
   * @param bm bean manager.
   */
  <T> void processProducerCommandBus(@Observes final ProcessProducer<T, CommandBus> pp, final BeanManager bm) {
    log.debug("Producer for CommandBus found: {}.", pp.getProducer());
    this.commandBusProducer = pp.getProducer();
  }

  /**
   * Scans for message consumer producer.
   *
   * @param pp process producer event.
   * @param bm bean manager.
   */
  <T> void processProducerMessageConsumer(@Observes final ProcessProducer<T, MessageProcessor> pp, final BeanManager bm) {
    log.debug("Producer for Message Consumer found: {}.", pp.getProducer());
    this.messsageProcessorProducer = pp.getProducer();
  }

  /**
   * Scans for entity manager provider producer.
   *
   * @param pp process producer event.
   * @param bm bean manager.
   */
  <T> void processProducerEntityManagerProvider(@Observes final ProcessProducer<T, EntityManagerProvider> pp, final BeanManager bm) {
    log.debug("Producer for EntityManager Provider found: {}.", pp.getProducer());
    this.entityManagerProviderProducer = pp.getProducer();
  }

  /**
   * Scans for token store producer.
   *
   * @param pp process producer event.
   * @param bm bean manager.
   */
  <T> void processProducerTokenStore(@Observes final ProcessProducer<T, TokenStore> pp, final BeanManager bm) {
    log.debug("Producer for Token Store: {}.", pp.getProducer());
    this.tokenStoreProducer = pp.getProducer();
  }

  /**
   * Scans for SubscribableEventMessageSource producer.
   *
   * @param pp process producer event.
   * @param bm bean manager.
   */
  <T> void processProducerSubscribableEventMessageSource(@Observes final ProcessProducer<T, SubscribableEventMessageSource> pp, final BeanManager bm) {
    log.debug("Producer for SubscribableEventMessageSource found: {}.", pp.getProducer());

    final String packageName = pp.getAnnotatedMember().getAnnotation(SubscribingEventProcessor.class).packageName();
    final Producer<SubscribableEventMessageSource> subscribableEventMessageSourceProducer = pp.getProducer();
    if (subscribableEventMessageSourceProducers.containsKey(packageName)) {
      log.warn("Two SubscribableEventMessageSource producers found for the same package {}. {} is used, {} is ignored.", packageName,
        subscribableEventMessageSourceProducers.get(packageName), subscribableEventMessageSourceProducer);
    } else {
      this.subscribableEventMessageSourceProducers.put(packageName, subscribableEventMessageSourceProducer);
    }
  }

  /**
   * Scans all beans and collects beans with {@link EventHandler} annotated methods.
   *
   * @param pb bean processing event.
   */
  <T> void processBean(@Observes final ProcessBean<T> pb) {
    final Bean<?> bean = pb.getBean();
    if (CDIUtils.hasAnnotatedMethod(bean, EventHandler.class)) {
      eventHandlers.add(bean);
      log.debug("Found event handler {}", bean.getBeanClass().getSimpleName());
    }

    if (CDIUtils.hasAnnotatedMember(bean, PersistenceUnit.class, EventStoreEnginePersistenceUnit.class)) {
      log.debug("Found persistent unit for event store {}", pb);
    }
  }

  /**
   * Registration of axon components in CDI registry.
   *
   * @param abd after bean discovery event.
   * @param bm  bean manager
   */
  void afterBeanDiscovery(@Observes final AfterBeanDiscovery abd, final BeanManager bm) {
    log.info("Starting AxonFramework configuration.");

    // configurer
    final Configurer configurer;
    if (this.configurerProducer != null) {
      configurer = this.configurerProducer.produce(bm.createCreationalContext(null));
    } else {
      configurer = DefaultConfigurer.defaultConfiguration();
    }

    // entity manager provider
    if (this.entityManagerProviderProducer != null) {
      final EntityManagerProvider emProvider = this.entityManagerProviderProducer.produce(bm.createCreationalContext(null));
      log.info("Registering entity manager provider {}", emProvider.getClass().getSimpleName());
      configurer.registerComponent(EntityManagerProvider.class, c -> emProvider);
    }

    // serializer
    if (this.serializerProducer != null) {
      final Serializer serializer = this.serializerProducer.produce(bm.createCreationalContext(null));
      log.info("Registering serializer {}", serializer.getClass().getSimpleName());
      configurer.configureSerializer(c -> serializer);
    }

    // transaction manager
    if (this.txManagerProducer != null) {
      final TransactionManager txManager = this.txManagerProducer.produce(bm.createCreationalContext(null));
      log.info("Registering transaction manager {}", txManager.getClass().getSimpleName());
      configurer.configureTransactionManager(c -> txManager);
    } else {
      log.warn("No transaction manager found, using Logging Transaction Manager");
      configurer.configureTransactionManager(c -> LoggingTransactionManager.INSTANCE);
    }

    // command bus registration
    if (this.commandBusProducer != null) {
      final CommandBus commandBus = this.commandBusProducer.produce(bm.createCreationalContext(null));
      log.info("Registering command bus {}", commandBus.getClass().getSimpleName());
      configurer.configureCommandBus(c -> commandBus);
    } else {
      log.info("No command bus producer found, using default simple command bus.");
    }


    // event handling configuration
    final EventHandlingConfiguration eventHandlerConfiguration;
    if (this.eventHandlingConfigurationProducer != null) {
      eventHandlerConfiguration = this.eventHandlingConfigurationProducer.produce(bm.createCreationalContext(null));
    } else {
      eventHandlerConfiguration = new EventHandlingConfiguration();
    }

    // register event sources
    subscribableEventMessageSourceProducers.forEach((packageName, producer) -> {
      final SubscribableEventMessageSource eventSource = producer.produce(bm.createCreationalContext(null));
      log.info("Registering event processor {} attaching to event source {}", packageName, eventSource);
      eventHandlerConfiguration.registerSubscribingEventProcessor(packageName, c -> eventSource);
    });

    // register event handlers
    eventHandlers.forEach(eventHandler -> {
      log.info("Registering event handler {}", eventHandler.getBeanClass().getName());
      eventHandlerConfiguration.registerEventHandler(c -> eventHandler.create(bm.createCreationalContext(null)));
    });

    // event handler configuration
    configurer.registerModule(eventHandlerConfiguration);

    // event bus
    if (this.eventBusProducer != null) {
      final EventBus eventBus = this.eventBusProducer.produce(bm.createCreationalContext(null));
      log.info("Registering event bus {}", eventBus.getClass().getSimpleName());
      configurer.configureEventBus(c -> eventBus);
    }

    // token store
    if (this.tokenStoreProducer != null) {
      final TokenStore tokenStore = this.tokenStoreProducer.produce(bm.createCreationalContext(null));
      log.info("Registering token store {}", tokenStore.getClass().getSimpleName());
      configurer.registerComponent(TokenStore.class, c -> tokenStore);
    }

    // event storage engine
    if (this.eventStorageEngineProducer != null) {
      final EventStorageEngine eventStorageEngine = this.eventStorageEngineProducer.produce(bm.createCreationalContext(null));
      log.info("Registering event storage {}", eventStorageEngine.getClass().getSimpleName());
      configurer.configureEmbeddedEventStore(c -> eventStorageEngine);
    }

    // register aggregates
    aggregates.stream().forEach(aggregate -> {
      log.info("Registering aggregate {}", aggregate.getSimpleName());
      configurer.configureAggregate(aggregate);
    });


    // build and start configuration
    final Configuration configuration = configurer.buildConfiguration();
    configuration.start();

    // register
    abd.addBean(new BeanWrapper<>(Configuration.class, () -> configuration));
    abd.addBean(new BeanWrapper<>(CommandBus.class, configuration::commandBus));
    abd.addBean(new BeanWrapper<>(CommandGateway.class, configuration::commandGateway));
    abd.addBean(new BeanWrapper<>(EventBus.class, configuration::eventBus));

    // if there were no serializer producer, register the serializer as a bean
    if (this.serializerProducer == null) {
      abd.addBean(new BeanWrapper<>(Serializer.class, configuration::serializer));
    }

    // register message consumers
    if (this.messsageProcessorProducer != null) {
      final Consumer<List<? extends EventMessage<?>>> messageProcessor = this.messsageProcessorProducer.produce(bm.createCreationalContext(null));
      log.info("Registering a message processor produced in {}", messsageProcessorProducer.getClass().getSimpleName());
      this.messageProcessorSubscriptions.add(configuration.eventBus().subscribe(messageProcessor));
    }

    log.info("AxonFramework configuration complete.");
  }

  void beforeShutdown(@Observes @Destroyed(ApplicationScoped.class) final Object event) {
    messageProcessorSubscriptions.stream().forEach(mps -> mps.cancel());
  }
}
