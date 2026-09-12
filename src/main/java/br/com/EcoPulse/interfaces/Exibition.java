package br.com.EcoPulse.interfaces;

import br.com.EcoPulse.controller.UserController;
import br.com.EcoPulse.domain.User;
import java.util.List;
import java.util.Scanner;

public class Exibition {
    private static final Scanner scanner = new Scanner(System.in);
    private static final UserController userController = new UserController();

    public static void main(String[] args) {
        System.out.println(Texts.WELCOME);
        boolean running = true;
        while (running) {
            Menus.showMainMenu();
            switch (scanner.nextLine()) {
                case "1" -> handleUserMenu();
                case "2" -> System.out.println("\n[Funcionalidade de Avatar em desenvolvimento]");
                case "3" -> System.out.println("\n[Funcionalidade de Missões em desenvolvimento]");
                case "0" -> { System.out.println(Texts.EXIT_MESSAGE); running = false; }
                default -> System.out.println(Texts.INVALID_OPTION);
            }
        }
    }

    private static void handleUserMenu() {
        boolean back = false;
        while (!back) {
            Menus.showUserMenu();
            try {
                switch (scanner.nextLine()) {
                    case "1" -> createUser();
                    case "2" -> listUsers(userController.listUsers());
                    case "3" -> searchUsers();
                    case "4" -> findUser();
                    case "5" -> updateUser();
                    case "6" -> showSummary();
                    case "7" -> deleteUser();
                    case "0" -> back = true;
                    default -> System.out.println(Texts.INVALID_OPTION);
                }
            } catch (IllegalArgumentException exception) {
                System.out.println("Erro: " + exception.getMessage());
            }
        }
    }

    private static void createUser() {
        User user = new User();
        System.out.print(Texts.PROMPT_NAME); user.setUsername(scanner.nextLine());
        System.out.print(Texts.PROMPT_EMAIL); user.setEmail(scanner.nextLine());
        userController.createUser(user);
        System.out.println(Texts.SUCCESS_CREATE + " ID: " + user.getId());
    }

    private static void listUsers(List<User> users) {
        if (users.isEmpty()) { System.out.println(Texts.NOT_FOUND); return; }
        users.forEach(Exibition::printUser);
    }

    private static void searchUsers() {
        System.out.print(Texts.PROMPT_SEARCH);
        listUsers(userController.searchUsers(scanner.nextLine()));
    }

    private static void findUser() {
        User user = userController.findUser(readId());
        if (user == null) System.out.println(Texts.NOT_FOUND); else printUser(user);
    }

    private static void updateUser() {
        long id = readId();
        System.out.print(Texts.PROMPT_NAME); String name = scanner.nextLine();
        System.out.print(Texts.PROMPT_EMAIL); String email = scanner.nextLine();
        userController.updateUserProfile(id, name, email);
        System.out.println(Texts.SUCCESS_UPDATE);
    }

    private static void showSummary() {
        System.out.println(userController.getUserProfileSummary(readId()));
    }

    private static void deleteUser() {
        if (userController.removeUser(readId())) System.out.println(Texts.SUCCESS_DELETE);
        else System.out.println(Texts.NOT_FOUND);
    }

    private static long readId() {
        System.out.print(Texts.PROMPT_ID);
        try { return Long.parseLong(scanner.nextLine()); }
        catch (NumberFormatException exception) { throw new IllegalArgumentException("ID deve ser numérico"); }
    }

    private static void printUser(User user) {
        System.out.println("ID: " + user.getId() + " | Nome: " + user.getDisplayName() + " | E-mail: " + user.getEmail());
    }
}
