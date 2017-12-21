package org.axonframework.cdi.stereotype;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.inject.Named;

import org.axonframework.commandhandling.model.AggregateRoot;

/**
 * Annotation that informs Axon's auto configurer for CDI that a given
 * {@link Named} is an aggregate instance.
 */
@Target({ ElementType.TYPE, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Named
@AggregateRoot
public @interface Aggregate {

  /**
   * Selects the name of the AggregateRepository bean. If left empty a new
   * repository is created. In that case the name of the repository will be
   * based on the simple name of the aggregate's class.
   */
  String repository() default "";

  /**
   * Get the String representation of the aggregate's type. Optional. This
   * defaults to the simple name of the annotated class.
   */
  String type() default "";
}
