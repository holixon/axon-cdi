package org.axonframework.extension.example.minimal.core.api;

import lombok.Value;

@Value
public class AccountCreatedEvent {
  String accountId;
  Integer overdraftLimit;
}
