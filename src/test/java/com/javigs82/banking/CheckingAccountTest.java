package com.javigs82.banking;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.testkit.javadsl.TestKit;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CheckingAccountTest {

    static ActorSystem system;

    @BeforeClass
    public static void setup() {
        system = ActorSystem.create();
    }

    @AfterClass
    public static void teardown() {
        TestKit.shutdownActorSystem(system);
        system = null;
    }


    @Test
    public void testDeposit () {
        TestKit probe = new TestKit(system);

        ActorRef accountActor = system.actorOf(CheckingAccount.props(123, 1, 10000));
        accountActor.tell(new CheckingAccount.Deposit(100), probe.getRef());

        assertEquals(10100, probe.expectMsgClass(CheckingAccount.RespondBalance.class).balance);


    }

    @Test
    public void testWithdrawal() {
        TestKit probe = new TestKit(system);

        ActorRef accountActor = system.actorOf(CheckingAccount.props(12, 1, 10000));
        accountActor.tell(new CheckingAccount.Withdrawal(9999), probe.getRef());

        assertEquals(1, probe.expectMsgClass(CheckingAccount.RespondBalance.class).balance);


    }

    @Test
    public void testInvalidWithdrawal() {
        TestKit probe = new TestKit(system);

        ActorRef accountActor = system.actorOf(CheckingAccount.props(12, 1, 10000));
        accountActor.tell(new CheckingAccount.Withdrawal(10001), probe.getRef());

        assertEquals(IllegalArgumentException.class, probe.expectMsgClass(IllegalArgumentException.class).getClass());


    }
}
