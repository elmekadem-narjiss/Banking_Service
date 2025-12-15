package com.skypay;

import java.time.Clock;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Account implements AccountService {

    private final List<Transaction> transactions;
    private final Clock clock;
    private int balance;

    /** Classe stock transaction. */
    private static class Transaction {
        private final LocalDate date;
        private final int amount; // positif pour dépôt, négatif pour retrait

        Transaction(LocalDate date, int amount) {
            this.date = date;
            this.amount = amount;
        }
    }

    /**  Constructeur par défaut utilise l'horloge système (pour usage normal).  */
    public Account() {
        this(Clock.systemDefaultZone());
    }

    public Account(Clock clock) {
        this.clock = clock;
        this.transactions = new ArrayList<>();
        this.balance = 0;
    }

    @Override
    public void deposit(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }

        LocalDate today = LocalDate.now(clock);
        transactions.add(new Transaction(today, amount));
        balance += amount;
    }

    @Override
    public void withdraw(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }
        if (amount > balance) {
            throw new IllegalArgumentException("Insufficient funds");
        }

        LocalDate today = LocalDate.now(clock);
        transactions.add(new Transaction(today, -amount));
        balance -= amount;
    }

    @Override
    public void printStatement() {
        System.out.println("Date || Amount || Balance");

        if (transactions.isEmpty()) {
            return; // Rien à afficher sauf l'en-tête
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // On construit les lignes dans l'ordre chronologique (du plus ancien au plus récent)
        List<String> lines = new ArrayList<>();
        int runningBalance = 0;

        for (Transaction t : transactions) {
            runningBalance += t.amount;
            String line = String.format("%s || %d || %d",
                    formatter.format(t.date),
                    t.amount,
                    runningBalance);
            lines.add(line);
        }

        // On inverse pour afficher du plus récent au plus ancien (comme dans l'exemple)
        Collections.reverse(lines);

        // Affichage
        for (String line : lines) {
            System.out.println(line);
        }
    }

    /**
     * Méthode utilitaire privée pour les tests : permet d'ajouter une transaction avec une date spécifique.
     */
    private void addTransactionWithDate(LocalDate date, int amount) {
        if (amount == 0) {
            throw new IllegalArgumentException("Amount cannot be zero");
        }
        transactions.add(new Transaction(date, amount));
        balance += amount;
    }
}