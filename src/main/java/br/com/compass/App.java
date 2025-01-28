package br.com.compass;

import br.com.compass.db.DB;
import br.com.compass.model.entities.User;
import br.com.compass.model.services.AccountService;
import br.com.compass.model.services.TransactionService;
import br.com.compass.model.services.UserService;

import java.util.Locale;
import java.util.Scanner;

public class App {

    static User user = null;
    static UserService userService = new UserService();
    static AccountService accountService = new AccountService();
    static TransactionService transactionService = new TransactionService();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Locale.setDefault(Locale.US);

        try {
            mainMenu(scanner);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            DB.closeConnection();
            scanner.close();
            System.out.println("Application closed");
        }
    }

   public static void mainMenu(Scanner scanner) {
        boolean running = true;

        while (running) {
            try{
                System.out.println("========= Main Menu =========");
                System.out.println("|| 1. Login                ||");
                System.out.println("|| 2. Account Opening      ||");
                System.out.println("|| 0. Exit                 ||");
                System.out.println("=============================");
                System.out.print("Choose an option: ");

                int option = scanner.nextInt();
                scanner.nextLine();

                switch (option) {
                    case 1:
                        user = userService.loginUser(scanner);
                        bankMenu(scanner);
                        return;
                    case 2:
                        accountService.createAccount(scanner);
                        user = userService.loginUser(scanner);
                        bankMenu(scanner);
                        break;
                    case 0:
                        running = false;
                        break;
                    default:
                        System.out.println("Invalid option! Please try again.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    public static void bankMenu(Scanner scanner) {
        boolean running = true;

        while (running) {
           try{
               System.out.println("========= Bank Menu =========");
               System.out.println("|| 1. Deposit              ||");
               System.out.println("|| 2. Withdraw             ||");
               System.out.println("|| 3. Check Balance        ||");
               System.out.println("|| 4. Transfer             ||");
               System.out.println("|| 5. Bank Statement       ||");
               System.out.println("|| 0. Exit                 ||");
               System.out.println("=============================");
               System.out.print("Choose an option: ");

               int option = scanner.nextInt();
               scanner.nextLine();

               switch (option) {
                   case 1:
                       accountService.deposit(scanner, user);
                       break;
                   case 2:
                       accountService.withdraw(scanner, user);
                       break;
                   case 3:
                       accountService.viewBalance(scanner, user);
                       break;
                   case 4:
                       transactionService.transferBalance(scanner, user);
                       break;
                   case 5:
                       accountService.viewStatement(user);
                       break;
                   case 0:
                       System.out.println("Exiting...");
                       running = false;
                       mainMenu(scanner);
                       return;
                   default:
                       System.out.println("Invalid option! Please try again.");
               }
           } catch (Exception e) {
               System.out.println("Error: " + e.getMessage());
           }
        }
    }
}
