package com.javigs82.banking;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Cancellable;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import scala.concurrent.duration.Duration;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class AccountManager extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    final static Map<Long, ActorRef> checkingAccountIdToActor = new HashMap<>();

    public static Props props() {
        return Props.create(AccountManager.class);
    }

    @Override
    public void preStart() {
        log.info("Simulating checking account actors");
        for (int i = 1; i <= 5; i++) {
            ActorRef cha = getContext().actorOf(CheckingAccount.props(Long.valueOf(i), Long.valueOf(i + 1), Long.valueOf(10000 + (i))), "checking-account-" + i);
            checkingAccountIdToActor.put(Long.valueOf(i), cha);
        }
    }

    //account messages
    //deposit here can be positive or negative if is a withdrawal
    static public class Deposit {
        final long amount;
        final long checkingAccountId;

        public Deposit(long amount, long checkingAccountId) {
            this.amount = amount;
            this.checkingAccountId = checkingAccountId;
        }
    }

    static public class CreateSavingAccount {
        final float interestRate; //percentage
        final long blockedAmount;
        final long checkingAccountId;


        public CreateSavingAccount(long blockedAmount, long checkingAccountId, float interestRate) {
            this.blockedAmount = blockedAmount;
            this.checkingAccountId = checkingAccountId;
            this.interestRate = interestRate;
        }
    }

    // No need to handle any messages
    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Deposit.class, deposit -> {
                    if (deposit.amount >= 0)
                        this.checkingAccountIdToActor.get(deposit.checkingAccountId).tell(new CheckingAccount.Deposit(deposit.amount), getSelf());
                    else
                        this.checkingAccountIdToActor.get(deposit.checkingAccountId).tell(new CheckingAccount.Withdrawal(Math.abs(deposit.amount)), getSelf());
                })
                .match(CreateSavingAccount.class, csa -> {
                    log.info("creating saving account for checking account id {}, amount {}, interest {} ", csa.checkingAccountId, csa.blockedAmount, csa.interestRate);
                    this.checkingAccountIdToActor.get(csa.checkingAccountId).tell(new CheckingAccount.Withdrawal(Math.abs(csa.blockedAmount)), getSelf());
                    final ActorRef savingActor = getContext().actorOf(SavingAccount.props(csa.checkingAccountId + 1, csa.blockedAmount, csa.interestRate, csa.checkingAccountId), "saving-account-" + csa.checkingAccountId);
                    Cancellable schedule = getContext().getSystem().scheduler().schedule(Duration.Zero(),
                            Duration.create(10, TimeUnit.SECONDS), savingActor, new SavingAccount.CalculateInterestSavings(),
                            getContext().getSystem().dispatcher(), getSelf());
                })
                .match(SavingAccount.ResponseInterestSavings.class, response -> {
                    this.checkingAccountIdToActor.get(response.checkingAccountId).tell(new CheckingAccount.Deposit(response.amount), getSelf());
                })
                .match(IllegalArgumentException.class, exception -> {
                    log.error("operation cannot be done: " + exception.getMessage());
                })
                .build();
    }


}
