package com.skypay;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

public class Main {
    public static void main(String[] args) {
        Account account;

        // Étape 1 : 10/01/2012 - dépôt 1000
        account = new Account(Clock.fixed(Instant.parse("2012-01-10T12:00:00Z"), ZoneOffset.UTC));
        account.deposit(1000);

        // Étape 2 : 13/01/2012 - on réapplique tout + nouveau dépôt 2000
        account = new Account(Clock.fixed(Instant.parse("2012-01-13T12:00:00Z"), ZoneOffset.UTC));
        account.deposit(1000);  // le premier dépôt
        account.deposit(2000);  // le nouveau

        // Étape 3 : 14/01/2012 - on réapplique tout + retrait 500
        account = new Account(Clock.fixed(Instant.parse("2012-01-14T12:00:00Z"), ZoneOffset.UTC));
        account.deposit(1000);  // ancien
        account.deposit(2000);  // ancien
        account.withdraw(500);  // nouveau retrait

        // Affichage final
        account.printStatement();
    }
}