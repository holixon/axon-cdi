package org.axonframework.messaging;

import org.axonframework.cdi.util.CDIUtils;
import org.axonframework.messaging.annotation.ParameterResolver;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import java.lang.reflect.Type;

public class CdiParameterResolver implements ParameterResolver<Object> {

  private final BeanManager beanManager;
  private final Bean<?> bean;
  private final Type type;

  public CdiParameterResolver(final BeanManager beanManager, final Bean<?> bean, final Type type) {
    this.beanManager = beanManager;
    this.bean = bean;
    this.type = type;
  }

  @Override
  @SuppressWarnings("rawtypes")
  public Object resolveParameterValue(final Message message) {
    return CDIUtils.getReference(beanManager, bean, type);
  }

  @Override
  @SuppressWarnings("rawtypes")
  public boolean matches(final Message message) {
    return true;
  }
}

