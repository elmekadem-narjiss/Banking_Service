package com.skypay;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class AccountTest {

    private Account account;
    private final ByteArrayOutputStream output = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(output));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        output.reset(); // Nettoie la sortie pour le prochain test
    }

    @Test
    @DisplayName("Deposit should increase balance and record transaction")
    void depositShouldIncreaseBalance() {
        account = new Account();
        account.deposit(1000);
        assertDoesNotThrow(() -> account.withdraw(500));
        assertThrows(IllegalArgumentException.class, () -> account.withdraw(600));
    }

    @Test
    @DisplayName("Deposit with negative or zero amount should throw exception")
    void depositNegativeAmountShouldThrowException() {
        account = new Account();
        assertThrows(IllegalArgumentException.class, () -> account.deposit(0));
        assertThrows(IllegalArgumentException.class, () -> account.deposit(-100));
    }

    @Test
    @DisplayName("Withdraw with insufficient funds should throw exception")
    void withdrawWithInsufficientFundsShouldThrowException() {
        account = new Account();
        account.deposit(500);
        assertThrows(IllegalArgumentException.class, () -> account.withdraw(600));
    }

    @Test
    @DisplayName("Withdraw with negative amount should throw exception")
    void withdrawNegativeAmountShouldThrowException() {
        account = new Account();
        assertThrows(IllegalArgumentException.class, () -> account.withdraw(-100));
    }

    @Test
    @DisplayName("Acceptance Test: Should print statement exactly as specified")
    void shouldPrintStatementMatchingAcceptanceCriteria() {
        account = new Account(); // On utilise un seul compte

        // On utilise la méthode privée via réflexion pour ajouter les transactions avec les bonnes dates
        // (C'est une pratique courante et acceptée en tests pour ce genre de kata)

        try {
            java.lang.reflect.Method addMethod = Account.class.getDeclaredMethod(
                    "addTransactionWithDate", LocalDate.class, int.class);
            addMethod.setAccessible(true);

            // 10/01/2012 - dépôt 1000
            addMethod.invoke(account, LocalDate.of(2012, 1, 10), 1000);

            // 13/01/2012 - dépôt 2000
            addMethod.invoke(account, LocalDate.of(2012, 1, 13), 2000);

            // 14/01/2012 - retrait 500
            addMethod.invoke(account, LocalDate.of(2012, 1, 14), -500);

        } catch (Exception e) {
            fail("Failed to invoke addTransactionWithDate: " + e.getMessage());
        }

        account.printStatement();

        String expected = "Date || Amount || Balance\r\n" +
                "14/01/2012 || -500 || 2500\r\n" +
                "13/01/2012 || 2000 || 3000\r\n" +
                "10/01/2012 || 1000 || 1000\r\n";

        String actual = output.toString();

        String normalizedExpected = expected.replaceAll("\\s+", " ").trim();
        String normalizedActual = actual.replaceAll("\\s+", " ").trim();

        assertEquals(normalizedExpected, normalizedActual);
    }

    @Test
    @DisplayName("Empty account should only print header")
    void emptyAccountShouldPrintOnlyHeader() {
        account = new Account();
        account.printStatement();

        String actual = output.toString();

        // Vérifie que l'en-tête est présent
        assertTrue(actual.contains("Date || Amount || Balance"));

        // Compte le nombre de lignes de manière compatible Java 8
        String[] lines = actual.split("\\r?\\n");
        assertEquals(1, lines.length, "Only the header should be printed");
    }
}