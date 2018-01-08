# Axon CDI

[![travis](https://travis-ci.org/holisticon/axon-cdi.svg?branch=master)](https://travis-ci.org/holisticon/axon-cdi)
[![codecov](https://codecov.io/gh/holisticon/axon-cdi/branch/master/graph/badge.svg)](https://codecov.io/gh/holisticon/axon-cdi)

[![Quality Gate](https://sonarqube.com/api/badges/gate?key=org.axonframework.extension:axon-cdi-root)](https://sonarcloud.io/dashboard?id=org.axonframework.extension%3Aaxon-cdi-root)
[![Sonar Rating](https://sonarqube.com/api/badges/measure?key=org.axonframework.extension:axon-cdi-root&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=org.axonframework.extension%3Aaxon-cdi-root)
[![Sonar Debt Ration](https://sonarqube.com/api/badges/measure?key=org.axonframework.extension:axon-cdi-root&metric=sqale_debt_ratio)](https://sonarcloud.io/dashboard?id=org.axonframework.extension%3Aaxon-cdi-root)
[![Sonar Reliability](https://sonarqube.com/api/badges/measure?key=org.axonframework.extension:axon-cdi-root&metric=reliability_rating)](https://sonarcloud.io/dashboard?id=org.axonframework.extension%3Aaxon-cdi-root)


CDI Extension to use AxonFramework 3.x in Container Environments


## Usage of JPA event store inside container

If you want to use the JPA based event store inside of a container (e.g. JBoss or Wildfly), you have to configure the following facilities:

  *  EntityManagerProvider
  *  TransactionManager
  *  EventStorageEngine
  *  TokenStore
  
In order to work together, consider usage of the following components:
  

	public class AxonConfiguration {
	  
	    /**
	     * JBoss JNDI address for JTA UserTransaction.
	     */
	    private static final String JBOSS_USER_TRANSACTION = "java:jboss/UserTransaction";
	
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
	    @Produces
	    public TransactionManager txManager() {
	        return new ContainerTransactionManager(entityManager, JBOSS_USER_TRANSACTION);
	    }
	
	    /**
	     * Produces container transaction aware JPA storage engine.
	     * 
	     * @param entityManagerProvider
	     *            provider for EntityManager (will be injected).
	     * @param txManager
	     *            TransactionManager (will be injected).
	     * @return Event storage engine.
	     */
	    @Produces
	    public EventStorageEngine eventStorageEngine(final EntityManagerProvider entityManagerProvider, final TransactionManager txManager) {
	        return new TxAwareJPAEventStorageEngine(entityManagerProvider, txManager);
	    }
	
	    /**
	     * Produces JPA token store.
	     * 
	     * @param entityManagerProvider
	     *            provider for EntityManager (will be injected).
	     * @param serializer
	     *            Serializer (will be injected).
	     * @return token store.
	     */
	    @Produces
	    public TokenStore tokenStore(final EntityManagerProvider entityManagerProvider, final Serializer serializer) {
	        return new JpaTokenStore(entityManagerProvider, serializer);
	    }
	    
	}

   
