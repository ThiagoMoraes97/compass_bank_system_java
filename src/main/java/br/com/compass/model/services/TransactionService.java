package br.com.compass.model.services;

import br.com.compass.db.DbException;
import br.com.compass.model.dao.AccountDao;
import br.com.compass.model.dao.DaoFactory;
import br.com.compass.model.dao.TransactionDao;
import br.com.compass.model.dao.UserDao;
import br.com.compass.model.entities.Account;
import br.com.compass.model.entities.User;
import br.com.compass.model.entities.enums.AccountType;
import br.com.compass.model.entities.enums.TransactionType;


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

        System.out.println("Digit the type of the destination account (e.g., Checking, Payroll or Savings): ");
        AccountType destinationAccountType = AccountType.valueOf(sc.nextLine().toUpperCase());

        boolean destinationAccountTypeExists = false;
        boolean accountTypeExists = false;

        TransactionType transactionType = TransactionType.TRANSFER;

        while(!destinationAccountTypeExists) {
            for (Account destinationAccount : destinationUser.getAccounts()) {
                if (destinationAccount.getAccountType().toString().contains(destinationAccountType.toString())) {

                    System.out.println("Enter the account type that you want to make the transfer (e.g., Checking, Payroll or Savings): ");
                    AccountType accountType = AccountType.valueOf(sc.nextLine().toUpperCase());

                    while(!accountTypeExists) {
                        for (Account account: user.getAccounts()) {
                            if (account.getAccountType().toString().contains(accountType.toString())) {
                                System.out.println("Enter the amount that you want to transfer: ");
                                double amount = sc.nextDouble();
                                sc.nextLine();
                                transactionDao.transferTransaction(user.getId(), accountType, amount, destinationUser.getId(), destinationAccountType);
                                transactionDao.saveTransferTransaction(user.getId(), account.getId(), transactionType, amount, destinationUser.getId());
                                accountTypeExists = true;
                            }
                        }
                        if (!accountTypeExists) {
                            System.out.println("User doesn't have an account of the specified type. Choose another type. (e.g., Checking, Payroll or Savings) ");
                            accountType = AccountType.valueOf(sc.nextLine().toUpperCase());
                        }
                    }

                    destinationAccountTypeExists = true;
                }
            }
            if (!destinationAccountTypeExists) {
                System.out.println("Destination user doesn't have an account of the specified type. Choose another type. (e.g., Checking, Payroll or Savings) ");
                destinationAccountType = AccountType.valueOf(sc.nextLine().toUpperCase());
            }
        }

    }
}
