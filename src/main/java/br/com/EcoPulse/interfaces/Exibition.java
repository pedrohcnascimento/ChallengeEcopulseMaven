package br.com.EcoPulse.interfaces;

import br.com.EcoPulse.controller.AvatarController;
import br.com.EcoPulse.controller.MissionController;
import br.com.EcoPulse.controller.UserController;
import br.com.EcoPulse.domain.Avatar;
import br.com.EcoPulse.domain.Mission;
import br.com.EcoPulse.domain.User;

import java.util.List;
import java.util.Scanner;

public class Exibition {
    private static final Scanner scanner = new Scanner(System.in);
    private static final UserController userController = new UserController();
    private static final AvatarController avatarController = new AvatarController();
    private static final MissionController missionController = new MissionController();

    public static void main(String[] args) {
        System.out.println(Texts.WELCOME);

        boolean running = true;
        while (running) {
            Menus.showMainMenu();

            try {
                switch (scanner.nextLine()) {
                    case "1" -> handleUserMenu();
                    case "2" -> handleAvatarMenu();
                    case "3" -> handleMissionMenu();
                    case "0" -> {
                        System.out.println(Texts.EXIT_MESSAGE);
                        running = false;
                    }
                    default -> System.out.println(Texts.INVALID_OPTION);
                }
            } catch (IllegalArgumentException exception) {
                System.out.println("Erro: " + exception.getMessage());
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
                    case "6" -> showUserSummary();
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

        System.out.print(Texts.PROMPT_NAME);
        user.setUsername(scanner.nextLine());

        System.out.print(Texts.PROMPT_EMAIL);
        user.setEmail(scanner.nextLine());

        userController.createUser(user);
        System.out.println(Texts.SUCCESS_CREATE + " ID: " + user.getId());
    }

    private static void listUsers(List<User> users) {
        if (users.isEmpty()) {
            System.out.println(Texts.NOT_FOUND);
            return;
        }

        users.forEach(Exibition::printUser);
    }

    private static void searchUsers() {
        System.out.print(Texts.PROMPT_SEARCH);
        String searchTerm = scanner.nextLine();
        listUsers(userController.searchUsers(searchTerm));
    }

    private static void findUser() {
        User user = userController.findUser(readId());

        if (user == null) {
            System.out.println(Texts.NOT_FOUND);
            return;
        }

        printUser(user);
    }

    private static void updateUser() {
        long id = readId();

        System.out.print(Texts.PROMPT_NAME);
        String name = scanner.nextLine();

        System.out.print(Texts.PROMPT_EMAIL);
        String email = scanner.nextLine();

        userController.updateUserProfile(id, name, email);
        System.out.println(Texts.SUCCESS_UPDATE);
    }

    private static void showUserSummary() {
        System.out.println(userController.getUserProfileSummary(readId()));
    }

    private static void deleteUser() {
        if (userController.removeUser(readId())) {
            System.out.println(Texts.SUCCESS_DELETE);
        } else {
            System.out.println(Texts.NOT_FOUND);
        }
    }

    private static void handleAvatarMenu() {
        boolean back = false;

        while (!back) {
            Menus.showAvatarMenu();

            try {
                switch (scanner.nextLine()) {
                    case "1" -> createAvatar();
                    case "2" -> listAvatars();
                    case "3" -> addAvatarExperience();
                    case "4" -> registerAvatarInteraction();
                    case "5" -> deleteAvatar();
                    case "0" -> back = true;
                    default -> System.out.println(Texts.INVALID_OPTION);
                }
            } catch (IllegalArgumentException exception) {
                System.out.println("Erro: " + exception.getMessage());
            }
        }
    }

    private static void createAvatar() {
        Avatar avatar = new Avatar();

        System.out.print("ID do usuário: ");
        avatar.setUserId(readNumber());

        System.out.print("Nome do avatar: ");
        avatar.setName(scanner.nextLine());

        System.out.print("Tipo de personalidade: ");
        avatar.setPersonalityType(scanner.nextLine());

        Avatar createdAvatar = avatarController.create(avatar);
        System.out.println("Avatar criado com ID: " + createdAvatar.getId());
    }

    private static void listAvatars() {
        List<Avatar> avatars = avatarController.list();

        if (avatars.isEmpty()) {
            System.out.println(Texts.NOT_FOUND);
            return;
        }

        avatars.forEach(avatar -> System.out.println(
                "ID: " + avatar.getId()
                        + " | " + avatar.getDisplayName()
                        + " | Nível: " + avatar.getLevel()
                        + " | XP: " + avatar.getExperiencePoints()
                        + " | Evoluído: " + avatar.isEvolved()));
    }

    private static void addAvatarExperience() {
        long id = readId();

        System.out.print("Pontos de experiência: ");
        long points = readNumber();

        Avatar avatar = avatarController.addExperience(id, points);
        System.out.println("Experiência adicionada. Nível atual: " + avatar.getLevel()
                + " | Faltam: " + avatar.getExperienceToNextLevel() + " XP");
    }

    private static void registerAvatarInteraction() {
        Avatar avatar = avatarController.registerInteraction(readId());
        System.out.println("Interação registrada para " + avatar.getDisplayName());
    }

    private static void deleteAvatar() {
        if (avatarController.delete(readId())) {
            System.out.println("Avatar excluído com sucesso!");
        } else {
            System.out.println(Texts.NOT_FOUND);
        }
    }

    private static void handleMissionMenu() {
        boolean back = false;

        while (!back) {
            Menus.showMissionMenu();

            try {
                switch (scanner.nextLine()) {
                    case "1" -> createMission();
                    case "2" -> listMissions(missionController.list());
                    case "3" -> listMissions(missionController.listActive());
                    case "4" -> updateMission();
                    case "5" -> changeMissionStatus();
                    case "6" -> deleteMission();
                    case "0" -> back = true;
                    default -> System.out.println(Texts.INVALID_OPTION);
                }
            } catch (IllegalArgumentException exception) {
                System.out.println("Erro: " + exception.getMessage());
            }
        }
    }

    private static void createMission() {
        Mission mission = new Mission();

        System.out.print("Título: ");
        mission.setTitle(scanner.nextLine());

        System.out.print("Descrição: ");
        mission.setDescription(scanner.nextLine());

        System.out.print("Tipo: ");
        mission.setType(scanner.nextLine());

        System.out.print("Pontos de recompensa: ");
        mission.setRewardPoints(readNumber().intValue());

        Mission createdMission = missionController.create(mission);
        System.out.println("Missão criada com ID: " + createdMission.getId());
    }

    private static void listMissions(List<Mission> missions) {
        if (missions.isEmpty()) {
            System.out.println(Texts.NOT_FOUND);
            return;
        }

        missions.forEach(mission -> System.out.println(
                "ID: " + mission.getId()
                        + " | " + mission.getDisplayTitle()
                        + " | Pontos: " + mission.getRewardPoints()
                        + " | Ativa: " + mission.isAvailable()));
    }

    private static void updateMission() {
        long id = readId();

        System.out.print("Novo título: ");
        String title = scanner.nextLine();

        System.out.print("Nova descrição: ");
        String description = scanner.nextLine();

        System.out.print("Nova pontuação: ");
        int points = readNumber().intValue();

        missionController.updateDetails(id, title, description, points);
        System.out.println("Missão atualizada com sucesso!");
    }

    private static void changeMissionStatus() {
        long id = readId();

        System.out.print("Ativar? (S/N): ");
        boolean active = scanner.nextLine().equalsIgnoreCase("S");

        missionController.changeStatus(id, active);
        System.out.println("Status da missão atualizado!");
    }

    private static void deleteMission() {
        if (missionController.delete(readId())) {
            System.out.println("Missão excluída com sucesso!");
        } else {
            System.out.println(Texts.NOT_FOUND);
        }
    }

    private static long readId() {
        System.out.print(Texts.PROMPT_ID);
        return readNumber();
    }

    private static Long readNumber() {
        try {
            return Long.parseLong(scanner.nextLine());
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("O valor deve ser numérico");
        }
    }

    private static void printUser(User user) {
        System.out.println("ID: " + user.getId()
                + " | Nome: " + user.getDisplayName()
                + " | E-mail: " + user.getEmail());
    }
}
