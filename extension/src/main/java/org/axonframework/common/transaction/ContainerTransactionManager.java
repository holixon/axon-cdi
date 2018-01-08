package org.axonframework.common.transaction;

import lombok.extern.slf4j.Slf4j;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.transaction.*;

/**
 * Container transaction manager.
 * <p>
 * Uses provided entity manager and accesses JTA UserTransaction from JNDI provided by the container.
 * </p>
 *
 * @author Simon Zambrovski
 */
@Slf4j
public class ContainerTransactionManager implements TransactionManager {

  private final EntityManager entityManager;
  private final String userTransactionJndiName;

  /**
   * Constructs the transaction manager.
   *
   * @param entityManager           entity manager to use.
   * @param userTransactionJndiName JNDI address of the USerTransaction object provided by the container.
   */
  public ContainerTransactionManager(final EntityManager entityManager, final String userTransactionJndiName) {
    this.entityManager = entityManager;
    this.userTransactionJndiName = userTransactionJndiName;
  }

  @Override
  public Transaction startTransaction() {

    // start with empty logging transaction.
    Transaction startedTransaction = LoggingTransactionManager.EMPTY_FAKE;
    try {
      final UserTransaction transaction = (UserTransaction) new InitialContext().lookup(this.userTransactionJndiName);

      if (transaction == null) {
        log.warn("No transaction is available.");
        return startedTransaction;
      }

      if (transaction.getStatus() != Status.STATUS_ACTIVE) {
        log.trace("Creating a new TX.");
        transaction.begin();
      } else {
        log.trace("Re-using running TX with status {}.", transaction.getStatus());
      }

      // join transaction
      if (!this.entityManager.isJoinedToTransaction()) {
        this.entityManager.joinTransaction();
      }


      startedTransaction = new Transaction() {

        @Override
        public void commit() {

          try {
            switch (transaction.getStatus()) {
              case Status.STATUS_ACTIVE:
                log.trace("Committing TX.");
                transaction.commit();
                break;
              case Status.STATUS_MARKED_ROLLBACK:
                log.warn("TX has been marked as rollback-only.");
                rollback();
                break;
              default:
                log.warn("Ignored commit of non-active TX in status {}.", transaction.getStatus());
                break;
            }
          } catch (final IllegalStateException | SystemException | SecurityException | RollbackException | HeuristicMixedException
            | HeuristicRollbackException e) {
            log.error("Error committing TX.", e);
          }
        }

        @Override
        public void rollback() {

          try {
            switch (transaction.getStatus()) {
              case Status.STATUS_ACTIVE:
                // intended no break
              case Status.STATUS_MARKED_ROLLBACK:
                log.trace("Rolling TX back.");
                transaction.rollback();
                break;
              default:
                log.warn("Ignored rollback of non-active TX in status {}.", transaction.getStatus());
                break;
            }
          } catch (final IllegalStateException | SystemException | SecurityException e) {
            log.error("Error roll TX back.", e);
          }
        }
      };

    } catch (final NotSupportedException | SystemException | NamingException e) {
      log.error("Error retrieving user transaction", e);
    }

    return startedTransaction;
  }
}
