package org.axonframework.cdi.stereotype;

import org.axonframework.commandhandling.model.AggregateRoot;

import javax.enterprise.util.Nonbinding;
import javax.inject.Named;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that informs Axon's auto configurer for CDI that a given
 * {@link Named} is an subscribing event processor.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Named
@AggregateRoot
public @interface SubscribingEventProcessor {

  @Nonbinding
  String packageName();

}
