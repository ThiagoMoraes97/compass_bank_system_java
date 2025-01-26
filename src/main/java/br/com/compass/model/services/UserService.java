package br.com.compass.model.services;

import br.com.compass.model.dao.DaoFactory;
import br.com.compass.model.dao.UserDao;
import br.com.compass.model.dao.impl.UserDaoJDBC;
import br.com.compass.model.entities.User;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class UserService {

    public void createUser(Scanner sc) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        System.out.println("========= Account Opening =========");
        System.out.print("|| Enter your full name: ");
        String name = sc.nextLine();
        System.out.print("|| Enter your CPF (e.g., 000.000.000-00): ");
        String cpf = sc.nextLine();
        System.out.print("|| Enter your password: ");
        String password = sc.nextLine();
        System.out.print("|| Enter your date of birth (e.g., DD/MM/YYYY): ");
        LocalDate dateOfBirth = LocalDate.parse(sc.nextLine(), formatter);
        System.out.print("|| Enter your phone number (e.g., 11 99999-9999): ");
        String phone = sc.nextLine();

        /*System.out.print("|| Enter your account type (e.g., Checking or Payroll or Savings): ");
        String accountType = sc.nextLine();*/


        User user = new User(name, dateOfBirth, cpf, phone, password);

        UserDao userDao = DaoFactory.createUserDao();

        userDao.insert(user);

        System.out.println("User created successfully");

    };



}
