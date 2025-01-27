package br.com.compass.model.services;

import br.com.compass.model.dao.AccountDao;
import br.com.compass.model.dao.DaoFactory;
import br.com.compass.model.dao.UserDao;
import br.com.compass.model.entities.Account;
import br.com.compass.model.entities.User;
import br.com.compass.model.entities.enums.AccountType;

import java.util.List;
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
}
