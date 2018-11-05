package com.javigs82.banking;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class CheckingAccount extends AbstractActor {

    private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    final long accountId;
    final long userId;
    private long balance; //cents

    public CheckingAccount(long accountId, long userId, long balance) {
        this.accountId = accountId;
        this.userId = userId;
        this.balance = balance;
    }

    static public Props props(long accountId, long userId, long initBalance) {
        return Props.create(CheckingAccount.class, accountId, userId, initBalance);
    }


    //account messages

    static public class Deposit {
        final long amount;

        public Deposit(long amount) {
            this.amount = amount;
        }
    }

    static public class Withdrawal {
        final long amount;

        public Withdrawal(long amount) {
            this.amount = amount;
        }
    }


    //response-messages
    static public class RespondBalance {
        final long accountId;
        final long userId;
        final long balance;

        public RespondBalance(long accountId, long userId, long balance) {
            this.accountId = accountId;
            this.userId = userId;
            this.balance = balance;
        }
    }

    //end account messages

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Deposit.class, deposit -> {
                    this.balance += deposit.amount;
                    log.info("deposit {} done ", deposit.amount);
                    getSender().tell(new RespondBalance(this.accountId, this.userId, this.balance), getSelf());
                })
                .match(Withdrawal.class, withdrawal -> {
                    if (withdrawal.amount > this.balance) {
                        getSender().tell(new IllegalArgumentException("Invalid Operation: withdrawal amount cannot be greater than account balance"), getSelf());
                    } else {
                        this.balance -= withdrawal.amount;
                        log.info("withdrawal {} done ", withdrawal.amount);
                        getSender().tell(new RespondBalance(this.accountId, this.userId, this.balance), getSelf());
                    }
                })
                .build();
    }


}
