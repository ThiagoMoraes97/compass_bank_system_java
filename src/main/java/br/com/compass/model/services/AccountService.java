package br.com.compass.model.services;

import br.com.compass.db.DbException;
import br.com.compass.model.dao.AccountDao;
import br.com.compass.model.dao.DaoFactory;
import br.com.compass.model.dao.TransactionDao;
import br.com.compass.model.dao.UserDao;
import br.com.compass.model.entities.Account;
import br.com.compass.model.entities.Transaction;
import br.com.compass.model.entities.User;
import br.com.compass.model.entities.enums.AccountType;
import br.com.compass.model.entities.enums.TransactionType;

import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class AccountService {
    UserDao userDao = DaoFactory.createUserDao();
    AccountDao accountDao = DaoFactory.createAccountDao();
    TransactionDao transactionDao = DaoFactory.createTransactionDao();
    UserService userService = new UserService();

    public void createAccount(Scanner sc) {
        try{
            System.out.println("========= Account Opening =========");

            System.out.print("Enter your CPF (11 digits): ");
            String cpf = sc.nextLine().trim();

            while (cpf.isEmpty() ||(cpf.length() != 11 || !cpf.matches("\\d+"))) {
                System.out.println("Error: CPF must have exactly 11 digits.");
                System.out.print("Enter your CPF (11 digits): ");
                cpf = sc.nextLine();
            }

            User user = userDao.findByCPF(cpf);

            if (user == null) {
                userService.createUser(sc, cpf);
                return;
            }

            AccountType accountType;

            while (true) {
                System.out.print("Enter your account type (e.g., Checking, Payroll, Savings): ");
                try {
                    accountType = AccountType.valueOf(sc.nextLine().toUpperCase());
                    break;
                } catch (IllegalArgumentException e) {
                    System.out.println("Input error: Invalid account type. Accepted values are: Checking, Payroll, Savings.");
                }
            }

            List<Account> accounts = accountDao.findByUserId(user.getId());

            for (Account account : accounts) {
                if (account.getAccountType().toString().equalsIgnoreCase(accountType.toString())) {
                    System.out.println("This user already has an account of the specified type.");
                    return;
                }
            }

            accountDao.insert(accountType, user.getId());

            System.out.println("Account has been created.");
        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
        }
    }

    public void deposit(Scanner sc, User user) {
        try{
            sc.useLocale(Locale.US);

            System.out.println("========= Deposit =========");
            System.out.print("Enter the amount you want to deposit: ");
            Double amount = sc.nextDouble();
            sc.nextLine();

            if (amount <= 0) {
                throw new IllegalArgumentException("Deposit amount must be greater than zero.");
            }

            System.out.println("Enter the account type (e.g., Checking, Payroll or Savings): ");
            AccountType accountType = AccountType.valueOf(sc.nextLine().toUpperCase());

            TransactionType transactionType = TransactionType.DEPOSIT;

            while(true) {
                for (Account account : user.getAccounts()) {
                    if (account.getAccountType().toString().contains(accountType.toString())) {
                        account.deposit(amount);
                        accountDao.deposit(user.getId(), accountType, amount);
                        System.out.println("Deposit successful. New balance: " + account.getBalance());
                        transactionDao.saveTransaction(user.getId(), account.getId(), transactionType, amount);
                        return;
                    }
                }
                System.out.println("User don't have an account of the specified type. Choose another type. (e.g., Checking, Payroll or Savings) ");
                accountType = AccountType.valueOf(sc.nextLine().toUpperCase());
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
        } catch (DbException e) {
            System.err.println("Database error occurred: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
        }

    };

    public void withdraw(Scanner sc, User user) {
        try {
            sc.useLocale(Locale.US);

            System.out.println("========= Withdraw =========");
            System.out.print("Enter the amount you want to withdraw: ");
            Double amount = sc.nextDouble();
            sc.nextLine();

            if (amount <= 0) {
                throw new IllegalArgumentException("Withdraw amount must be greater than zero.");
            }

            System.out.println("Enter the account type (e.g., Checking, Payroll or Savings): ");
            AccountType accountType = AccountType.valueOf(sc.nextLine().toUpperCase());

            TransactionType transactionType = TransactionType.WITHDRAW;

            while(true) {
                for (Account account : user.getAccounts()) {
                    if (account.getAccountType().toString().contains(accountType.toString())) {
                        try{
                            account.withdraw(amount);
                            accountDao.withdraw(user.getId(), accountType, amount);
                            System.out.println("Withdraw successful. New balance: " + account.getBalance());
                            transactionDao.saveTransaction(user.getId(), account.getId(), transactionType, amount);
                            return;
                        } catch (DbException e){
                            System.out.println("Error: " + e.getMessage());
                            return;
                        }
                    }
                }
                System.out.println("User don't have an account of the specified type. Choose another type. (e.g., Checking, Payroll or Savings) ");
                accountType = AccountType.valueOf(sc.nextLine().toUpperCase());
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
        } catch (DbException e) {
            System.err.println("Database error occurred: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
        }

    };

    public void viewBalance(Scanner sc, User user) {
        try {
            System.out.println("========= View balance =========");
            System.out.print("Enter the account type you want to view the balance (e.g., Checking, Payroll or Savings): ");
            AccountType accountType = AccountType.valueOf(sc.nextLine().toUpperCase());

            while(true) {
                for (Account account : user.getAccounts()) {
                    if (account.getAccountType().toString().contains(accountType.toString())) {
                        System.out.println("The balance of your " + accountType.toString().toLowerCase() + " account is: R$" + String.format("%.2f", account.getBalance()));
                        return;
                    }
                }
                System.out.println("User don't have an account of the specified type. Choose another type. (e.g., Checking, Payroll or Savings) ");
                accountType = AccountType.valueOf(sc.nextLine().toUpperCase());
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
        }
    }

    public void viewStatement(User user) {
        try{
            System.out.println("============= Account Statement ==============");

            List<Transaction> allTransactions = transactionDao.listAllTransactions(user);
            System.out.println("+------------+------------+------------------+");
            System.out.println("| Type       | Amount     | Date             |");
            System.out.println("+------------+------------+------------------+");
            for (Transaction transaction : allTransactions) {
                System.out.println(transaction);
            }
            System.out.println("==============================================");
        } catch (DbException e) {
            System.err.println("Database error occurred while fetching the statement: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
        }
    };
}
