package com.javigs82.banking;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class SavingAccount extends AbstractActor {

    private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    static public Props props(long accountId, long blockedAmount, float interest, long checkingAccountId) {
        return Props.create(SavingAccount.class, accountId, blockedAmount, interest, checkingAccountId);
    }


    final long accountId;
    final long checkingAccountId;

    final float interestRate; //percentage
    final long blockedAmount;


    public SavingAccount(long accountId, long blockedAmount, float interest, long checkingAccountId) {
        this.accountId = accountId;
        this.checkingAccountId = checkingAccountId;
        this.blockedAmount = blockedAmount;
        this.interestRate = interest;
    }

    static public class CalculateInterestSavings {

        public CalculateInterestSavings() {

        }
    }

    static public class ResponseInterestSavings {

        final long checkingAccountId;
        final long amount;

        public ResponseInterestSavings(long amount, long checkingAccountId) {
            this.amount = amount;
            this.checkingAccountId = checkingAccountId;


        }
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(CalculateInterestSavings.class, cis -> {
                    long amount = ((long) (blockedAmount * this.interestRate));
                    this.log.info("calculating interest of savings {}", amount);
                    getSender().tell(new ResponseInterestSavings(amount, this.checkingAccountId), getSelf());
                })
                .build();
    }

}
