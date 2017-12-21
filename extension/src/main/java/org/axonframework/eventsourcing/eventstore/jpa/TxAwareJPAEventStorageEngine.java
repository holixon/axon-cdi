package org.axonframework.eventsourcing.eventstore.jpa;

import org.axonframework.common.jdbc.PersistenceExceptionResolver;
import org.axonframework.common.jpa.EntityManagerProvider;
import org.axonframework.common.transaction.TransactionManager;
import org.axonframework.eventhandling.EventMessage;
import org.axonframework.eventsourcing.DomainEventMessage;
import org.axonframework.serialization.Serializer;
import org.axonframework.serialization.upcasting.event.EventUpcaster;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

/**
 * JPA Event Storage Engine encapsulating JPA write operations in transactions using the TransactionManager.
 *
 * @see {@link JpaEventStorageEngine}
 */
public class TxAwareJPAEventStorageEngine extends JpaEventStorageEngine {

  private final TransactionManager transactionManager;

  public TxAwareJPAEventStorageEngine(final EntityManagerProvider entityManagerProvider, final TransactionManager transactionManager) {
    super(entityManagerProvider, transactionManager);
    Objects.requireNonNull(transactionManager, "Transaction manager must not be null");
    this.transactionManager = transactionManager;
  }

  public TxAwareJPAEventStorageEngine(final Serializer serializer, final EventUpcaster upcasterChain, final DataSource dataSource,
                                      final EntityManagerProvider entityManagerProvider, final TransactionManager transactionManager) throws SQLException {
    super(serializer, upcasterChain, dataSource, entityManagerProvider, transactionManager);
    Objects.requireNonNull(transactionManager, "Transaction manager must not be null");
    this.transactionManager = transactionManager;
  }

  public TxAwareJPAEventStorageEngine(final Serializer serializer, final EventUpcaster upcasterChain,
                                      final PersistenceExceptionResolver persistenceExceptionResolver, final Integer batchSize, final EntityManagerProvider entityManagerProvider,
                                      final TransactionManager transactionManager, final Long lowestGlobalSequence, final Integer maxGapOffset, final boolean explicitFlush) {
    super(serializer, upcasterChain, persistenceExceptionResolver, serializer, batchSize, entityManagerProvider, transactionManager, lowestGlobalSequence, maxGapOffset,
      explicitFlush);
    Objects.requireNonNull(transactionManager, "Transaction manager must not be null");
    this.transactionManager = transactionManager;
  }

  @Override
  protected void appendEvents(final List<? extends EventMessage<?>> events, final Serializer serializer) {
    if (events.isEmpty()) {
      return;
    }
    transactionManager.executeInTransaction(() -> super.appendEvents(events, serializer));
  }

  @Override
  protected void storeSnapshot(final DomainEventMessage<?> snapshot, final Serializer serializer) {
    transactionManager.executeInTransaction(() -> super.storeSnapshot(snapshot, serializer));
  }

}
