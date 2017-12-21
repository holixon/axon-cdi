package org.axonframework.eventhandling;

import java.util.List;
import java.util.function.Consumer;

/**
 * Interface hiding the wildcard types in order to be produced by CDI.
 *
 * @author Simon Zambrovski, Holisticon AG
 */
public interface MessageProcessor extends Consumer<List<? extends EventMessage<?>>> {

}
