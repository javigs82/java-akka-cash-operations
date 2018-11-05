package com.javigs82.banking;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;

import java.io.IOException;

public class CashOperationMain {

    public static void main(String[] args) throws IOException, InterruptedException {


        ActorSystem system = ActorSystem.create("cash-operation-system");


        try {

            System.out.println("Press ENTER to exit the system");

            //checking accounts are created as dummy in the manager
            ActorRef manager = system.actorOf(AccountManager.props(), "cash-operation-manager");


            //create a couple of fixed deposits.
            manager.tell(new AccountManager.CreateSavingAccount(999, 1, 0.1f), ActorRef.noSender());
            manager.tell(new AccountManager.CreateSavingAccount(300, 2, 0.19f), ActorRef.noSender());

            //create randomly checking account operations.
           while (System.in.available() == 0) {

                Thread.sleep(500);
                manager.tell(new AccountManager.Deposit((long) ((Math.random() * ((100 + 200) + 1)) - 200), (long) ((Math.random() * ((5 - 1) + 1)) + 1)), ActorRef.noSender()); //there are only 5 accounts
            }



            System.in.read();

            system.stop(manager);
            system.terminate();

        } finally {
            system.terminate();
        }
    }
}
