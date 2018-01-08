package org.axonframework.cdi.util;

import org.jglue.cdiunit.CdiRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(CdiRunner.class)
public class CDIUtilsITest {

  @Test
  public void testBeanManager() {
    assertThat(CDIUtils.getBeanManager()).isNotNull();
  }
}
