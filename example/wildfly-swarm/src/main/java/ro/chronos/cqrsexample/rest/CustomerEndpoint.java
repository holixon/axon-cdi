package ro.chronos.cqrsexample.rest;

import org.axonframework.commandhandling.gateway.CommandGateway;
import ro.chronos.cqrsexample.api.CreateCustomerCommand;
import ro.chronos.cqrsexample.query.CustomerView;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.UUID;

@ApplicationScoped
@Path("/customer")
public class CustomerEndpoint {

    @PersistenceContext(name = "MyPU")
    private EntityManager em;

    @Inject
    private CommandGateway commandGateway;

    @GET
    @Path("/:id")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCustomer(@PathParam("id") String id) {
        CustomerView customer = em.createNamedQuery("CustomerView.findById", CustomerView.class).getSingleResult();
        return Response.ok(customer).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/create")
    public Response createCustomer(@QueryParam("fullName") String fullName, @QueryParam("age") Integer age) {
        String customerId = UUID.randomUUID().toString();
        commandGateway.sendAndWait(new CreateCustomerCommand(customerId, fullName, age));
        return Response.created(URI.create("http://localhost:8080/customer/" + customerId)).build();
    }
}