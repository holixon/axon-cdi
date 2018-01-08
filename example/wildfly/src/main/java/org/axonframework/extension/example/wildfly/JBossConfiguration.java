package org.axonframework.extension.example.wildfly;

import lombok.extern.slf4j.Slf4j;
import org.axonframework.cdi.stereotype.EventStoreEnginePersistenceUnit;
import org.axonframework.common.jpa.EntityManagerProvider;
import org.axonframework.common.jpa.SimpleEntityManagerProvider;
import org.axonframework.common.transaction.ContainerTransactionManager;
import org.axonframework.common.transaction.LoggingTransactionManager;
import org.axonframework.common.transaction.TransactionManager;
import org.axonframework.eventhandling.tokenstore.TokenStore;
import org.axonframework.eventhandling.tokenstore.jpa.JpaTokenStore;
import org.axonframework.eventsourcing.eventstore.EventStorageEngine;
import org.axonframework.eventsourcing.eventstore.jpa.JpaEventStorageEngine;
import org.axonframework.serialization.Serializer;
import org.axonframework.serialization.json.JacksonSerializer;

import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Slf4j
public class JBossConfiguration {
  /**
   * JBoss JNDI address for JTA UserTransaction.
   */
  private static final String JBOSS_USER_TRANSACTION = "java:jboss/UserTransaction";

  @EventStoreEnginePersistenceUnit
  @PersistenceContext
  private EntityManager entityManager;

  /**
   * Produces the entity manager provider.
   *
   * @return manager provider.
   */
  @Produces
  public EntityManagerProvider getEntityManagerProvider() {
    return new SimpleEntityManagerProvider(entityManager);
  }

  /**
   * Produces the transaction manager.
   *
   * @return transaction manager.
   */
  // @Produces
  public TransactionManager containerTxManager() {
    return new ContainerTransactionManager(entityManager, JBOSS_USER_TRANSACTION);
  }

  @Produces
  public TransactionManager txManager() {
    return LoggingTransactionManager.INSTANCE;
  }

  /**
   * Produces container transaction aware JPA storage engine.
   *
   * @return Event storage engine.
   */
  @Produces
  public EventStorageEngine eventStorageEngine() {
    return new JpaEventStorageEngine(getEntityManagerProvider(), txManager());
    // return new TxAwareJPAEventStorageEngine(getEntityManagerProvider(), txManager());
  }

  /**
   * Produces JPA token store.
   *
   * @return token store.
   */
  @Produces
  public TokenStore tokenStore() {
    return new JpaTokenStore(getEntityManagerProvider(), serializer());
  }


  /**
   * Produces Jackson serializer.
   * @return serializer.
   */
  @Produces
  public Serializer serializer() {
    return new JacksonSerializer();
  }

}
