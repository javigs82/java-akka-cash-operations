# Cash Operations
Cash Operations is a concurrent banking system example built on top of akka framework and java8. 
It aims to show how to use actor model paradigm to implement concurrency operations avoiding locks and blocks (and also their tedious and complex use) 
while focusing in the business requirements.

## Main Requirements

In this bank, there are millions of operations per day. bank offers their clients two types of accounts:

  - Saving Account: A savings account additionally supports interest payments to its owner based on a certain interest rate
                    tied to the account. There should be a possibility to calculate and pay the appropriate interest to a
                    savings account.
                    
  - Checking Account: A checking account additionally supports balance overdrafts restricted by a limit tied to the account.
                      There should be a possibility to do cash transfers between checking accounts.
  

Each account has an owner and a balance. Accounts also support deposits and withdrawals of money amounts.

### Assumptions

  - Account balance cannot be negative. Therefore, an operation may only be applied IF & ONLY IF balance if enough,
  OTHERWISE an exception SHOULD be thrown.  
  - It is possible to open an account with zero balance  
  - Balance and amount are represented like cents defined with long primitive type avoiding float and double for currency operations.
  - in saving account
    - interest is being paid each 10 seconds
    - saving account is associated to a checking account, so payments will be deposit on the checking account.
    - all saving accounts are fixed deposit 
  - testing should cover at least 80% of source, but this is only a quiz.
   

## Actor Architecture 

In order to develop the solution, following actor hierarchy has been defined:

 - AccountManager
    - SavingAccount
    - CheckingAccount

In next sections, actors and their messages are analyzed.
        
### Account Manager
This is the Manager for operation business messages.

 They task are:
 
  - Mimic creation of checking accounts (via postConstructor)
  - Accept Deposit (to simplify if amount is negative it is considered as Withdrawal)
  - Create Saving Account
  - Schedule Saving Payments

### Saving Account
The task of a saving account are:
 
 - Calculate saving payments based on rate and blocked amount.
 - Return saving payment calculation.

### Checking Account
The task of a saving account are:
 - Deposits
 - Withdrawal
 - Account info

### Messages

Messages are defined as static class into actor that belong.

## Prerequisites

What things you need to install the software and how to install them

* Maven
* JDK 1.8

## Installing

A step by step series of examples that tell you have to get a development env running

```
mvn clean install
```

## Running the tests

Explain how to run the automated tests for this system

```
mvn clean test
```

## Run App

Add additional notes about how to deploy this on a live system

```
mvn compile exec:exec
```




