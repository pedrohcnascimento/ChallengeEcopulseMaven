package br.com.EcoPulse.interfaces;

public class Menus {
    
    public static void showMainMenu() {
        System.out.println(Texts.MAIN_MENU_TITLE);
        System.out.println(Texts.OPTION_USER);
        System.out.println(Texts.OPTION_AVATAR);
        System.out.println(Texts.OPTION_MISSION);
        System.out.println(Texts.OPTION_EXIT);
        System.out.print("Escolha uma opção: ");
    }

    public static void showUserMenu() {
        System.out.println(Texts.USER_MENU_TITLE);
        System.out.println(Texts.USER_CREATE);
        System.out.println(Texts.USER_LIST);
        System.out.println(Texts.USER_BACK);
        System.out.print("Escolha uma opção: ");
    }
}
