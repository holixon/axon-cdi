package org.axonframework.cdi.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Arrays;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class CDIUtils {

    /**
     * Retrieves a object reference of a given type.
     * 
     * @param beanManager
     *            bean manager to use.
     * @param clazz
     *            type of the object.
     * @return instance.
     */
    @SuppressWarnings("unchecked")
    public static final <T> T getReference(final BeanManager beanManager, final Class<T> clazz) {
        final Bean<T> bean = (Bean<T>) beanManager.getBeans(clazz).iterator().next();
        final T instance = (T) beanManager.getReference(bean, clazz, beanManager.createCreationalContext(bean));
        return instance;
    }

    /**
     * Returns an object reference of a given bean.
     * 
     * @param bm
     *            bean manager
     * @param bean
     *            bean
     * @param beanType
     *            bean type.
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T getReference(final BeanManager bm, final Bean<T> bean, final Type beanType) {
        return (T) bm.getReference(bean, beanType, bm.createCreationalContext(bean));
    }

    /**
     * Checks whether a given bean has methods annotated with given annotation.
     * 
     * @param bean
     *            bean to check.
     * @param clazz
     *            annotation class.
     * @return true if at least one annotated method is present.
     */
    public static final boolean hasAnnotatedMethod(final Bean<?> bean, final Class<? extends Annotation> clazz) {
        final boolean hasAnnotation = Arrays.stream(bean.getBeanClass().getMethods()).filter(m -> m.isAnnotationPresent(clazz)).findAny().isPresent();
        return hasAnnotation;
    }

    /**
     * Checks whether a bean has a member annotated with all provided annotations.
     * 
     * @param bean
     *            bean to check.
     * @param classes
     *            annotation classes to check for.
     * @return true if a member of a bean is annotated with all specified annotations.
     */
    @SafeVarargs
    public static boolean hasAnnotatedMember(final Bean<?> bean, final Class<? extends Annotation>... classes) {
        // TODO re-write with lambda
        final Class<?> beanClazz = bean.getBeanClass();
        if (beanClazz.getDeclaredFields().length == 0) {
            return false;
        }

        for (final Field field : beanClazz.getDeclaredFields()) {
            boolean found = true;
            for (final Class<? extends Annotation> clazz : classes) {
                if (!field.isAnnotationPresent(clazz)) {
                    found = false;
                    break;
                }
            }
            if (found) {
                return true;
            }
        }
        return false;
    }

    /**
     * Retrieve the bean manager.
     * 
     * @return bean manager, if any, or <code>null</code>
     */
    public static BeanManager getBeanManager() {
        try {
            final InitialContext ctx = new InitialContext();
            return (BeanManager) ctx.lookup("java:comp/BeanManager");
        } catch (final NamingException e) {

        }
        return null;
    }

    /**
     * Applies CDI context to a object instance (inject all fields, call post construct).
     * @param beanManager bean manager.
     * @param instance instance.
     */
    @SuppressWarnings("unchecked")
    public void applyContext(final BeanManager beanManager, final Object instance) {
        final AnnotatedType<Object> type = (AnnotatedType<Object>) beanManager.createAnnotatedType(instance.getClass());
        final InjectionTarget<Object> target = beanManager.createInjectionTarget(type);
        final CreationalContext<Object> cc = beanManager.createCreationalContext(null);
        target.inject(instance, cc);
        target.postConstruct(instance);
    }
}
