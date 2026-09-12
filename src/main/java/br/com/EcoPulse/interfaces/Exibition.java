package br.com.EcoPulse.interfaces;

import br.com.EcoPulse.controller.UserController;
import br.com.EcoPulse.domain.User;
import java.util.Scanner;

public class Exibition {
    private static final Scanner scanner = new Scanner(System.in);
    private static final UserController userController = new UserController();

    public static void main(String[] args) {
        System.out.println(Texts.WELCOME);
        boolean running = true;

        while (running) {
            Menus.showMainMenu();
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    handleUserMenu();
                    break;
                case "2":
                    System.out.println("\n[Funcionalidade de Avatar em desenvolvimento]");
                    break;
                case "3":
                    System.out.println("\n[Funcionalidade de Missões em desenvolvimento]");
                    break;
                case "0":
                    System.out.println(Texts.EXIT_MESSAGE);
                    running = false;
                    break;
                default:
                    System.out.println(Texts.INVALID_OPTION);
            }
        }
    }

    private static void handleUserMenu() {
        boolean back = false;
        while (!back) {
            Menus.showUserMenu();
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    createUser();
                    break;
                case "2":
                    listUsers();
                    break;
                case "0":
                    back = true;
                    break;
                default:
                    System.out.println(Texts.INVALID_OPTION);
            }
        }
    }

    private static void createUser() {
        System.out.print(Texts.PROMPT_NAME);
        String name = scanner.nextLine();
        System.out.print(Texts.PROMPT_EMAIL);
        String email = scanner.nextLine();

        User newUser = new User();
        newUser.setUsername(name);
        newUser.setEmail(email);
        
        userController.createUser(newUser);
        System.out.println(Texts.SUCCESS_CREATE);
    }

    private static void listUsers() {
        System.out.println("\n--- Lista de Usuários ---");
        userController.listUsers().forEach(u -> 
            System.out.println("ID: " + u.getId() + " | Nome: " + u.getUsername() + " | Email: " + u.getEmail())
        );
    }
}
