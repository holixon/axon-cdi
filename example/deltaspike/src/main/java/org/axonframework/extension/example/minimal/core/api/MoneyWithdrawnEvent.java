package org.axonframework.extension.example.minimal.core.api;

import lombok.Value;

@Value
public class MoneyWithdrawnEvent {
  String accountId;
  Integer amount;
}
