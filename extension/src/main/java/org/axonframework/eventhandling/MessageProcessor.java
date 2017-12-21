package org.axonframework.eventhandling;

import java.util.List;
import java.util.function.Consumer;

import org.axonframework.eventhandling.EventMessage;

/**
 * Interface hiding the wildcard types in order to be produced by CDI.
 * 
 * @author developer
 *
 */
public interface MessageProcessor extends Consumer<List<? extends EventMessage<?>>> {

}
