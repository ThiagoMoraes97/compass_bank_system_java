package br.com.compass.model.services;

import br.com.compass.db.DbException;
import br.com.compass.model.dao.AccountDao;
import br.com.compass.model.dao.DaoFactory;
import br.com.compass.model.dao.UserDao;
import br.com.compass.model.entities.Account;
import br.com.compass.model.entities.User;
import br.com.compass.model.entities.enums.AccountType;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class UserService {

    UserDao userDao = DaoFactory.createUserDao();
    AccountDao accountDao = DaoFactory.createAccountDao();

    public void createUser(Scanner sc) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        System.out.println("========= Account Opening =========");
        System.out.print("Enter your full name: ");
        String name = sc.nextLine();
        System.out.print("Enter your CPF (11 digits): ");
        String cpf = sc.nextLine();
        System.out.print("Enter your password: ");
        String password = sc.nextLine();
        System.out.print("Enter your date of birth (e.g., DD/MM/YYYY): ");
        LocalDate dateOfBirth = LocalDate.parse(sc.nextLine(), formatter);
        System.out.print("Enter your phone number (e.g., 1199999-9999): ");
        String phone = sc.nextLine();

        System.out.print("Enter your account type (e.g., Checking or Payroll or Savings): ");
        AccountType accountType = AccountType.valueOf(sc.nextLine().toUpperCase());

        User user = new User(name, dateOfBirth, cpf, phone, password);

        Account account = new Account(accountType, user);

        int user_id = userDao.insert(user);
        user.setId(user_id);

        int account_id = accountDao.insert(account);
        account.setId(account_id);
        account.getUser().setId(user_id);

        System.out.println("User and Account created successfully");
    };

    public void loginUser(Scanner sc) {
        System.out.println("========= Account Login =========");
        System.out.print("Enter your CPF: ");
        String cpf = sc.nextLine();
        System.out.print("Enter your password: ");
        String password = sc.nextLine();

        User user = userDao.findByCPF(cpf);

        if (user == null) {
            throw new DbException("User or password is incorrect");
        }

        if (user.getPassword().equals(password)) {
            System.out.println("Login successful!");
        } else {
            throw new DbException("User or password is incorrect");
        }
    }
}
