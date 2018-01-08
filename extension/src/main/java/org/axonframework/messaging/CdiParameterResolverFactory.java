package org.axonframework.messaging;

import lombok.extern.slf4j.Slf4j;
import org.axonframework.cdi.util.CDIUtils;
import org.axonframework.common.Priority;
import org.axonframework.messaging.annotation.ParameterResolver;
import org.axonframework.messaging.annotation.ParameterResolverFactory;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import java.lang.reflect.Executable;
import java.lang.reflect.Parameter;
import java.util.Set;

/**
 * Parameter resolver factory for instantiating Axon artifacts inside of CDI context.
 *
 * @author Simon Zambrovski
 */
@Slf4j
@Priority(Priority.LOW)
public class CdiParameterResolverFactory implements ParameterResolverFactory {

  private final BeanManager beanManager;

  public CdiParameterResolverFactory() {
    this.beanManager = CDIUtils.getBeanManager();
  }

  @Override
  public ParameterResolver<?> createInstance(final Executable executable, final Parameter[] parameters, final int parameterIndex) {
    final Parameter parameter = parameters[parameterIndex];

    if (this.beanManager == null) {
      log.error("BeanManager was null. This is a fatal error, an instance of {} {} is not created.", parameter.getType(), parameter.getAnnotations());
      return null;
    }

    log.trace("Create instance for {} {}", parameter.getType(), parameter.getAnnotations());
    final Set<Bean<?>> beansFound = beanManager.getBeans(parameter.getType(), parameter.getAnnotations());
    if (beansFound.isEmpty()) {
      return null;
    } else if (beansFound.size() > 1) {
      if (log.isWarnEnabled()) {
        log.warn("Ambiguous reference for parameter type {} with qualifiers {}", parameter.getType().getName(), parameter.getAnnotations());
      }
      return null;
    } else {
      return new CdiParameterResolver(beanManager, beansFound.iterator().next(), parameter.getType());
    }
  }
}
