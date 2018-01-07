package org.axonframework.extension.example.minimal.core.api;

import lombok.Value;
import org.axonframework.commandhandling.TargetAggregateIdentifier;

@Value
public class CreateAccountCommand {
  @TargetAggregateIdentifier
  String accountId;
  Integer overdraftLimit;
}
