package org.axonframework.extension.example.minimal;

import lombok.extern.slf4j.Slf4j;
import org.apache.deltaspike.cdise.api.CdiContainer;
import org.apache.deltaspike.cdise.api.CdiContainerLoader;

import javax.enterprise.inject.spi.CDI;

@Slf4j
public class WeldApplicationRunner {

  public static void main(final String[] args) throws Exception {
    final CdiContainer container = CdiContainerLoader.getCdiContainer();
    try {
      container.boot();
      final CdiApplication application = CDI.current().select(CdiApplication.class).get();
      application.run();
    } catch (Exception e) {
      log.error("Error in example", e);
    } finally {
      container.shutdown();
      log.info("Shutting down...");
    }
  }

}
