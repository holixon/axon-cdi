package org.axonframework.extension.example.minimal.core.api;

import lombok.Value;
import org.axonframework.commandhandling.TargetAggregateIdentifier;

@Value
public class WithdrawMoneyCommand {
  @TargetAggregateIdentifier
  String accountId;
  Integer amount;
  Integer balance;
}
