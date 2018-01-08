package org.axonframework.extension.example.minimal.query;

import javax.inject.Named;

import org.axonframework.eventhandling.EventHandler;

import lombok.extern.slf4j.Slf4j;
import org.axonframework.extension.example.minimal.core.api.AccountCreatedEvent;

@Named
@Slf4j
public class AccountListener {

  @EventHandler
  public void on(AccountCreatedEvent event) {
    log.info("Account created event received: {}", event);
  }

}
