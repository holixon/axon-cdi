package org.axonframework.cdi.util;

import lombok.Data;
import org.axonframework.cdi.extension.BeanWrapper;
import org.junit.Test;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Default;
import javax.enterprise.util.AnnotationLiteral;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

public class BeanWrapperTest {

  private Supplier<Foo> supplier = () -> new Foo();
  private BeanWrapper<Foo> testee = new BeanWrapper<Foo>(Foo.class, supplier);

  @Test
  public void testId() {
    assertThat(testee.getId()).isEqualTo(Foo.class.toString() + "#" + supplier.toString());
  }

  @Test
  public void testCreate() {
    assertThat(testee.create(null)).isEqualTo(new Foo());
  }

  @Test
  public void testSimpleName() {
    assertThat(testee.getName()).isEqualTo(Foo.class.getSimpleName());
  }

  @Test
  public void testGetStereotypes() {
    assertThat(testee.getStereotypes()).isEmpty();
  }

  @Test
  public void testGetTypes() {
    assertThat(testee.getTypes()).containsOnly(Foo.class, Object.class);
  }

  @Test
  public void testBeanClass() {
    assertThat(testee.getBeanClass()).isEqualTo(Foo.class);
  }

  @Test
  public void testScope() {
    assertThat(testee.getScope()).isEqualTo(ApplicationScoped.class);
  }

  @Test
  public void testQualifiers() {
    assertThat(testee.getQualifiers()).containsOnly(new AnnotationLiteral<Default>() {
    }, new AnnotationLiteral<Any>() {});
  }

  @Test
  public void testInjectionPoints() {
    assertThat(testee.getInjectionPoints()).isEmpty();
  }

  @Test
  public void testIsAlternative() {
    assertThat(testee.isAlternative()).isFalse();
  }

  @Test
  public void testIsNullable() {
    assertThat(testee.isAlternative()).isFalse();
  }

  @Data
  private static class Foo {

  }

}
