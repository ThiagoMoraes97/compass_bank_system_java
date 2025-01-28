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
            String name;

            while (true) {
                System.out.print("Enter your full name: ");
                name = sc.nextLine().trim();

                if (name.isEmpty()) {
                    System.out.println("Input error: Name cannot be empty.");
                    continue;
                }

                if (!name.matches("[a-zA-Z\\s]+")) {
                    System.out.println("Input error: Name must contain only letters.");
                    continue;
                }

                break;
            }

            String password;

            while (true) {
                System.out.print("Enter your password: ");
                password = sc.nextLine();
                try {
                    validatePassword(password);
                    break;
                } catch (IllegalArgumentException e) {
                    System.out.println("Input error: " + e.getMessage());
                }
            }

            LocalDate dateOfBirth;
            while (true) {
                System.out.print("Enter your date of birth (e.g., DD/MM/YYYY): ");
                try {
                    dateOfBirth = LocalDate.parse(sc.nextLine(), formatter);
                    break;
                } catch (Exception e) {
                    System.out.println("Input error: Invalid date format. Please use DD/MM/YYYY.");
                }
            }

            String phone;
            while (true) {
                System.out.print("Enter your phone number (e.g., (11) 99999-9999): ");
                phone = sc.nextLine().trim();
                if (!phone.isEmpty() && phone.matches("\\(\\d{2}\\) \\d{5}-\\d{4}")) {
                    break;
                }
                System.out.println("Input error: Phone number must be in the format (XX) XXXXX-XXXX.");
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

        while (true) {
            try {
                System.out.print("Enter your CPF (or type 'exit' to quit): ");
                String cpf = sc.nextLine().trim();


                if (cpf.equalsIgnoreCase("exit")) {
                    System.out.println("Exiting login process...");
                    return null;
                }


                while (cpf.isEmpty() || (cpf.length() != 11 || !cpf.matches("\\d+"))) {
                    System.out.println("Error: CPF must have exactly 11 digits.");
                    System.out.print("Enter your CPF (11 digits, or type 'exit' to quit): ");
                    cpf = sc.nextLine().trim();


                    if (cpf.equalsIgnoreCase("exit")) {
                        System.out.println("Exiting login process...");
                        return null;
                    }
                }

                String password;


                while (true) {
                    System.out.print("Enter your password (or type 'exit' to quit): ");
                    password = sc.nextLine();


                    if (password.equalsIgnoreCase("exit")) {
                        System.out.println("Exiting login process...");
                        return null;
                    }

                    try {
                        validatePassword(password);
                        break;
                    } catch (IllegalArgumentException e) {
                        System.out.println("Input error: " + e.getMessage());
                    }
                }


                User user = userDao.findByCPF(cpf);
                if (user == null) {
                    System.out.println("Authentication error: User not found.");
                    continue;
                }


                if (user.getPassword().equals(password)) {
                    System.out.println("Login successful!");
                    return user;
                } else {
                    System.out.println("Authentication error: Incorrect password.");
                }

            } catch (IllegalArgumentException e) {
                System.err.println("Input error: " + e.getMessage());
            } catch (DbException e) {
                System.err.println("Authentication error: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("An unexpected error occurred: " + e.getMessage());
            }


            System.out.println("Login failed. Please try again or type 'exit' to quit.\n");
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
