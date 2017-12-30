package org.axonframework.cdi.util;

import org.axonframework.cdi.extension.BeanWrapper;
import org.junit.Test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static org.assertj.core.api.Assertions.assertThat;

public class CDIUtilsTest {

  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.FIELD)
  public @interface FirstAnnotation {}

  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.FIELD)
  public @interface SecondAnnotation {}


  public static class HasAnnotatedField extends BeanWrapper<HasAnnotatedField>{
    @FirstAnnotation
    protected String name;

    public HasAnnotatedField() {
      super(HasAnnotatedField.class, HasAnnotatedField::new);
    }
  }

  public static class HasBothAnnotations extends BeanWrapper<HasBothAnnotations>{
    @FirstAnnotation
    @SecondAnnotation
    protected String name;

    public HasBothAnnotations() {
      super(HasBothAnnotations.class, HasBothAnnotations::new);
    }
  }

  public static class InheritAnnotatedField extends HasAnnotatedField {

    private String other;
  }

  public static class HasNoAnnotatedField extends BeanWrapper<HasNoAnnotatedField>{
    private String name;

    public HasNoAnnotatedField() {
      super(HasNoAnnotatedField.class, HasNoAnnotatedField::new);
    }
  }


  @Test
  public void hasAnnotatedMember_true() {
    assertThat(CDIUtils.hasAnnotatedMember(new HasAnnotatedField(), FirstAnnotation.class)).isTrue();
  }

  @Test
  public void hasAnnotatedMember_false() {
    assertThat(CDIUtils.hasAnnotatedMember(new HasNoAnnotatedField(), FirstAnnotation.class)).isFalse();
  }

  @Test
  public void hasAnnotatedMember_both_false() {
    assertThat(CDIUtils.hasAnnotatedMember(new HasAnnotatedField(), FirstAnnotation.class, SecondAnnotation.class)).isFalse();
  }

  @Test
  public void hasAnnotatedMember_both_true() {
    assertThat(CDIUtils.hasAnnotatedMember(new HasBothAnnotations(), FirstAnnotation.class, SecondAnnotation.class)).isTrue();
  }
}
