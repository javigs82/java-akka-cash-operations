package com.javigs82.banking;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.testkit.javadsl.TestKit;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SavingAccountTest {

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
    public void testCalculateInterestSavings () {
        TestKit probe = new TestKit(system);

        ActorRef accountActor = system.actorOf(SavingAccount.props(123, 100, 0.1f, 123));
        accountActor.tell(new SavingAccount.CalculateInterestSavings(), probe.getRef());

        assertEquals(10, probe.expectMsgClass(SavingAccount.ResponseInterestSavings.class).amount);


    }

}
