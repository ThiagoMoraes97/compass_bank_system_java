package br.com.compass.model.services;

import br.com.compass.db.DbException;
import br.com.compass.model.dao.AccountDao;
import br.com.compass.model.dao.DaoFactory;
import br.com.compass.model.dao.UserDao;
import br.com.compass.model.entities.User;
import br.com.compass.model.entities.enums.AccountType;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class UserService {

    UserDao userDao = DaoFactory.createUserDao();
    AccountDao accountDao = DaoFactory.createAccountDao();

    public void createUser(Scanner sc, String cpf) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        try {
            System.out.print("Enter your full name: ");
            String name = sc.nextLine().trim();
            if (name.isEmpty()) {
                throw new IllegalArgumentException("Name cannot be empty.");
            }

            System.out.print("Enter your password: ");
            String password = sc.nextLine().trim();
            validatePassword(password);

            System.out.print("Enter your date of birth (e.g., DD/MM/YYYY): ");
            LocalDate dateOfBirth;
            try {
                dateOfBirth = LocalDate.parse(sc.nextLine(), formatter);
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid date format. Please use DD/MM/YYYY.");
            }

            System.out.print("Enter your phone number (e.g., (11) 99999-9999): ");
            String phone = sc.nextLine().trim();
            if (phone.isEmpty()) {
                throw new IllegalArgumentException("Phone number cannot be empty.");
            }
            if (!phone.matches("\\(\\d{2}\\) \\d{5}-\\d{4}")) {
                throw new IllegalArgumentException("Phone number must be in the format (XX) XXXXX-XXXX.");
            }

            System.out.print("Enter your account type (e.g., Checking or Payroll or Savings): ");
            AccountType accountType;
            try {
                accountType = AccountType.valueOf(sc.nextLine().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid account type. Accepted values are: Checking, Payroll, Savings.");
            }

            int user_id = userDao.insert(name, dateOfBirth, cpf, phone, password);
            accountDao.insert(accountType, user_id);

            System.out.println("User and Account created successfully");

        } catch (IllegalArgumentException e) {
            System.err.println("Input error: " + e.getMessage());
        } catch (DbException e) {
            System.err.println("Database error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
        }
    };

    public User loginUser(Scanner sc) {
        System.out.println("========= Account Login =========");

        try {
            System.out.print("Enter your CPF: ");
            String cpf = sc.nextLine().trim();
            if (cpf.isEmpty()) {
                throw new IllegalArgumentException("CPF cannot be empty.");
            }
            if (cpf.length() != 11 || !cpf.matches("\\d+")) {
                throw new IllegalArgumentException("CPF must have exactly 11 numeric digits.");
            }

            System.out.print("Enter your password: ");
            String password = sc.nextLine().trim();
            validatePassword(password);

            User user = userDao.findByCPF(cpf);
            if (user == null) {
                throw new DbException("User or password is incorrect.");
            }

            if (user.getPassword().equals(password)) {
                System.out.println("Login successful!");
            } else {
                throw new DbException("User or password is incorrect.");
            }

            return user;

        } catch (IllegalArgumentException e) {
            System.err.println("Input error: " + e.getMessage());
            return null;
        } catch (DbException e) {
            System.err.println("Authentication error: " + e.getMessage());
            return null;
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
            return null;
        }

    }

    public void validatePassword(String password) {
        if (password.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty.");
        }
        if (password.length() < 6) {
            throw new IllegalArgumentException("Password must have at least 6 characters.");
        }
        if (!password.matches(".*[A-Z].*")) {
            throw new IllegalArgumentException("Password must contain at least one uppercase letter.");
        }
        if (!password.matches(".*[a-z].*")) {
            throw new IllegalArgumentException("Password must contain at least one lowercase letter.");
        }
        if (!password.matches(".*[!#].*")) {
            throw new IllegalArgumentException("Password must contain at least one special character (! or #).");
        }
    }

}
