package ro.chronos.cqrsexample.query;

import org.axonframework.eventhandling.EventHandler;
import ro.chronos.cqrsexample.api.CustomerCreatedEvent;

import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Named
public class CustomerEventListener {

    @PersistenceContext(name = "MyPU")
    private EntityManager em;

    @EventHandler
    public void on(CustomerCreatedEvent event) {
        em.persist(new CustomerView(event.getCustomerId(), event.getFullName(), event.getAge()));
    }
}
