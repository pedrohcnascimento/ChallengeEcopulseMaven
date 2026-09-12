package br.com.EcoPulse.interfaces;

import br.com.EcoPulse.controller.*;
import br.com.EcoPulse.domain.*;
import java.util.List;
import java.util.Scanner;

public class Exibition {
    private static final Scanner scanner=new Scanner(System.in);
    private static final UserController userController=new UserController();
    private static final AvatarController avatarController=new AvatarController();
    private static final MissionController missionController=new MissionController();
    public static void main(String[] args){System.out.println(Texts.WELCOME);boolean running=true;while(running){Menus.showMainMenu();try{switch(scanner.nextLine()){case "1"->handleUserMenu();case "2"->handleAvatarMenu();case "3"->handleMissionMenu();case "0"->{System.out.println(Texts.EXIT_MESSAGE);running=false;}default->System.out.println(Texts.INVALID_OPTION);}}catch(IllegalArgumentException e){System.out.println("Erro: "+e.getMessage());}}}
    private static void handleUserMenu(){boolean back=false;while(!back){Menus.showUserMenu();try{switch(scanner.nextLine()){case "1"->createUser();case "2"->listUsers(userController.listUsers());case "3"->searchUsers();case "4"->findUser();case "5"->updateUser();case "6"->System.out.println(userController.getUserProfileSummary(readId()));case "7"->deleteUser();case "0"->back=true;default->System.out.println(Texts.INVALID_OPTION);}}catch(IllegalArgumentException e){System.out.println("Erro: "+e.getMessage());}}}
    private static void createUser(){User u=new User();System.out.print(Texts.PROMPT_NAME);u.setUsername(scanner.nextLine());System.out.print(Texts.PROMPT_EMAIL);u.setEmail(scanner.nextLine());userController.createUser(u);System.out.println(Texts.SUCCESS_CREATE+" ID: "+u.getId());}
    private static void listUsers(List<User> users){if(users.isEmpty()){System.out.println(Texts.NOT_FOUND);return;}users.forEach(u->System.out.println("ID: "+u.getId()+" | Nome: "+u.getDisplayName()+" | E-mail: "+u.getEmail()));}
    private static void searchUsers(){System.out.print(Texts.PROMPT_SEARCH);listUsers(userController.searchUsers(scanner.nextLine()));}
    private static void findUser(){User u=userController.findUser(readId());if(u==null)System.out.println(Texts.NOT_FOUND);else System.out.println("ID: "+u.getId()+" | Nome: "+u.getDisplayName()+" | E-mail: "+u.getEmail());}
    private static void updateUser(){long id=readId();System.out.print(Texts.PROMPT_NAME);String name=scanner.nextLine();System.out.print(Texts.PROMPT_EMAIL);String email=scanner.nextLine();userController.updateUserProfile(id,name,email);System.out.println(Texts.SUCCESS_UPDATE);}
    private static void deleteUser(){if(userController.removeUser(readId()))System.out.println(Texts.SUCCESS_DELETE);else System.out.println(Texts.NOT_FOUND);}

    private static void handleAvatarMenu(){boolean back=false;while(!back){Menus.showAvatarMenu();try{switch(scanner.nextLine()){case "1"->createAvatar();case "2"->listAvatars();case "3"->addAvatarExperience();case "4"->registerAvatarInteraction();case "5"->deleteAvatar();case "0"->back=true;default->System.out.println(Texts.INVALID_OPTION);}}catch(IllegalArgumentException e){System.out.println("Erro: "+e.getMessage());}}}
    private static void createAvatar(){Avatar a=new Avatar();System.out.print("ID do usuário: ");a.setUserId(readNumber());System.out.print("Nome do avatar: ");a.setName(scanner.nextLine());System.out.print("Tipo de personalidade: ");a.setPersonalityType(scanner.nextLine());Avatar created=avatarController.create(a);System.out.println("Avatar criado com ID: "+created.getId());}
    private static void listAvatars(){List<Avatar> list=avatarController.list();if(list.isEmpty()){System.out.println(Texts.NOT_FOUND);return;}list.forEach(a->System.out.println("ID: "+a.getId()+" | "+a.getDisplayName()+" | Nível: "+a.getLevel()+" | XP: "+a.getExperiencePoints()+" | Evoluído: "+a.isEvolved()));}
    private static void addAvatarExperience(){long id=readId();System.out.print("Pontos de experiência: ");Avatar a=avatarController.addExperience(id,readNumber());System.out.println("Experiência adicionada. Nível atual: "+a.getLevel()+" | Faltam: "+a.getExperienceToNextLevel()+" XP");}
    private static void registerAvatarInteraction(){Avatar a=avatarController.registerInteraction(readId());System.out.println("Interação registrada para "+a.getDisplayName());}
    private static void deleteAvatar(){if(avatarController.delete(readId()))System.out.println("Avatar excluído com sucesso!");else System.out.println(Texts.NOT_FOUND);}

    private static void handleMissionMenu(){boolean back=false;while(!back){Menus.showMissionMenu();try{switch(scanner.nextLine()){case "1"->createMission();case "2"->listMissions(missionController.list());case "3"->listMissions(missionController.listActive());case "4"->updateMission();case "5"->changeMissionStatus();case "6"->deleteMission();case "0"->back=true;default->System.out.println(Texts.INVALID_OPTION);}}catch(IllegalArgumentException e){System.out.println("Erro: "+e.getMessage());}}}
    private static void createMission(){Mission m=new Mission();System.out.print("Título: ");m.setTitle(scanner.nextLine());System.out.print("Descrição: ");m.setDescription(scanner.nextLine());System.out.print("Tipo: ");m.setType(scanner.nextLine());System.out.print("Pontos de recompensa: ");m.setRewardPoints(readNumber().intValue());Mission created=missionController.create(m);System.out.println("Missão criada com ID: "+created.getId());}
    private static void listMissions(List<Mission> list){if(list.isEmpty()){System.out.println(Texts.NOT_FOUND);return;}list.forEach(m->System.out.println("ID: "+m.getId()+" | "+m.getDisplayTitle()+" | Pontos: "+m.getRewardPoints()+" | Ativa: "+m.isAvailable()));}
    private static void updateMission(){long id=readId();System.out.print("Novo título: ");String title=scanner.nextLine();System.out.print("Nova descrição: ");String description=scanner.nextLine();System.out.print("Nova pontuação: ");missionController.updateDetails(id,title,description,readNumber().intValue());System.out.println("Missão atualizada com sucesso!");}
    private static void changeMissionStatus(){long id=readId();System.out.print("Ativar? (S/N): ");boolean active=scanner.nextLine().equalsIgnoreCase("S");missionController.changeStatus(id,active);System.out.println("Status da missão atualizado!");}
    private static void deleteMission(){if(missionController.delete(readId()))System.out.println("Missão excluída com sucesso!");else System.out.println(Texts.NOT_FOUND);}
    private static long readId(){System.out.print(Texts.PROMPT_ID);return readNumber();}
    private static Long readNumber(){try{return Long.parseLong(scanner.nextLine());}catch(NumberFormatException e){throw new IllegalArgumentException("O valor deve ser numérico");}}
}
