package br.com.compass.model.services;

import br.com.compass.model.dao.AccountDao;
import br.com.compass.model.dao.DaoFactory;
import br.com.compass.model.dao.UserDao;
import br.com.compass.model.entities.Account;
import br.com.compass.model.entities.User;
import br.com.compass.model.entities.enums.AccountType;

import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class AccountService {

    UserDao userDao = DaoFactory.createUserDao();
    AccountDao accountDao = DaoFactory.createAccountDao();
    UserService userService = new UserService();

    public void createAccount(Scanner sc) {
        System.out.println("========= Account Opening =========");
        System.out.print("Enter your CPF (11 digits): ");
        String cpf = sc.nextLine();

        User user = userDao.findByCPF(cpf);

        if (user == null) {
            userService.createUser(sc, cpf);
            return;
        }

        System.out.print("Enter your account type (e.g., Checking or Payroll or Savings): ");
        AccountType accountType = AccountType.valueOf(sc.nextLine().toUpperCase());

        List<Account> accounts = accountDao.findByUserId(user.getId());

        for (Account account : accounts) {
            if (account.getAccountType().toString().equalsIgnoreCase(accountType.toString())) {
                System.out.println("This user already has an account of the specified type.");
                return;
            }
        }

        accountDao.insert(accountType, user.getId());

        System.out.println("Account has been created.");
    }

    public void deposit(Scanner sc, User user) {
        sc.useLocale(Locale.US);

        System.out.println("========= Deposit =========");
        System.out.print("Enter the amount you want to deposit: ");
        Double amount = sc.nextDouble();
        sc.nextLine();
        System.out.println("Enter the account type (e.g., Checking, Payroll or Savings): ");
        AccountType accountType = AccountType.valueOf(sc.nextLine().toUpperCase());

        while(true) {
            for (Account account : user.getAccounts()) {
                if (account.getAccountType().toString().contains(accountType.toString())) {
                    account.deposit(amount);
                    accountDao.deposit(user.getId(), accountType, amount);
                    System.out.println("Deposit successful. New balance: " + account.getBalance());
                    return;
                }
            }
            System.out.println("User don't have an account of the specified type. Choose another type. (e.g., Checking, Payroll or Savings) ");
            accountType = AccountType.valueOf(sc.nextLine().toUpperCase());
        }

    };

}
