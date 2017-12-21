package org.axonframework.cdi.stereotype;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.enterprise.util.Nonbinding;
import javax.inject.Named;

import org.axonframework.commandhandling.model.AggregateRoot;

/**
 * Annotation that informs Axon's auto configurer for CDI that a given
 * {@link Named} is an aggregate instance.
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Named
@AggregateRoot
public @interface SubscribingEventProcessor {

  @Nonbinding
  String packageName();

}
