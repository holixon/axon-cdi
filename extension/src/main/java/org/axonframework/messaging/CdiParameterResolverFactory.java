package org.axonframework.messaging;

import java.lang.reflect.Executable;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.Set;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import org.axonframework.cdi.util.CDIUtils;
import org.axonframework.common.Priority;
import org.axonframework.messaging.annotation.ParameterResolver;
import org.axonframework.messaging.annotation.ParameterResolverFactory;

import lombok.extern.slf4j.Slf4j;

/**
 * Parameter resolver factory for instantiating Axon artifacts inside of CDI context.
 * @author Simon Zambrovski, Holisticon AG
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
        log.trace("Create instance for {} {}", parameter.getType(), parameter.getAnnotations());
        if (this.beanManager == null) {
            return null;
        }
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

    private static class CdiParameterResolver implements ParameterResolver<Object> {

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

}
