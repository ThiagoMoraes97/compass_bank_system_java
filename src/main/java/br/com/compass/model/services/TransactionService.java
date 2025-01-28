package br.com.compass.model.services;

import br.com.compass.db.DbException;
import br.com.compass.model.dao.DaoFactory;
import br.com.compass.model.dao.TransactionDao;
import br.com.compass.model.dao.UserDao;
import br.com.compass.model.entities.Account;
import br.com.compass.model.entities.User;
import br.com.compass.model.entities.enums.AccountType;
import br.com.compass.model.entities.enums.TransactionType;


import java.util.InputMismatchException;
import java.util.Locale;
import java.util.Scanner;

public class TransactionService {

    UserDao userDao = DaoFactory.createUserDao();
    TransactionDao transactionDao = DaoFactory.createTransactionDao();

    public void transferBalance(Scanner sc, User user) {
        sc.useLocale(Locale.US);

        System.out.println("========= Bank Transfer =========");

        System.out.println("Digit the CPF of the destination account:");
        String cpf = sc.nextLine();

        User destinationUser = userDao.findByCPF(cpf);

        if (destinationUser == null) {
            throw new DbException("The provided CPF does not have an account.");
        }

        AccountType destinationAccountType = checkIfAccountTypeExists(sc, "destination");

        Account destinationAccount = getAccountByType(destinationUser, destinationAccountType, sc, "destination");
        AccountType accountType = checkIfAccountTypeExists(sc, "origin");

        Account account = getAccountByType(user, accountType, sc, "origin");

        double amount = getTheTranferAmount(sc);

        try {
            processTransfer(user, account, destinationUser, destinationAccount, amount);
        } catch (DbException e) {
            System.out.println("Error during transfer: " + e.getMessage());
        }
    }

    private AccountType checkIfAccountTypeExists(Scanner sc, String type) {
        AccountType accountType;
        while (true) {
            System.out.printf("Digit the type of the %s account (e.g., Checking, Payroll or Savings):%n", type);
            try {
                accountType = AccountType.valueOf(sc.nextLine().toUpperCase());
                break;
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid account type. Please choose from Checking, Payroll or Savings.");
            }
        }
        return accountType;
    }

    private Account getAccountByType(User user, AccountType accountType, Scanner sc, String type) {
        while (true) {
            for (Account account : user.getAccounts()) {
                if (account.getAccountType().equals(accountType)) {
                    return account;
                }
            }
            System.out.printf("User doesn't have an account of the %s type. Choose another type.%n", type);
            accountType = checkIfAccountTypeExists(sc, type);
        }
    }

    private double getTheTranferAmount(Scanner sc) {
        System.out.println("Enter the amount that you want to transfer: ");
        while (true) {
            try {
                double amount = sc.nextDouble();
                sc.nextLine();
                if (amount > 0) {
                    return amount;
                } else {
                    System.out.println("Amount must be greater than zero. Please enter a valid amount.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid numeric value.");
                sc.nextLine();
            }
        }
    }

    private void processTransfer(User user, Account account, User destinationUser, Account destinationAccount, double amount) {

        TransactionType transactionType = TransactionType.TRANSFER;

        try {
            transactionDao.transferTransaction(user.getId(), account.getAccountType(), amount, destinationUser.getId(), destinationAccount.getAccountType());
            transactionDao.saveTransferTransaction(user.getId(), account.getId(), transactionType, amount, destinationUser.getId());
            System.out.println("Transfer successful!");
        } catch (DbException e) {
            System.out.println("Error during the transaction: " + e.getMessage());
            throw e;
        }
    }

}
