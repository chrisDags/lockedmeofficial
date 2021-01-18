package com.dags.lockedmeofficial.menus;

import com.dags.lockedmeofficial.operations.Operations;

import java.io.IOException;
import java.util.*;

public class Menu {

    private final StringBuilder mainMenuAsciiArtStrBuilder = new StringBuilder()
            .append("______________________________________________________________________________\n")
            .append("    _                                       _   _                             \n")
            .append("    /                  /                /   /  /|                             \n")
            .append("---/-------__----__---/-__----__----__-/---/| /-|----__-------__----__---_--_-\n")
            .append("  /      /   ) /   ' /(     /___) /   /   / |/  |  /___)    /   ' /   ) / /  )\n")
            .append("_/____/_(___/_(___ _/___\\__(___ _(___/___/__/___|_(___ __o_(___ _(___/_/_/__/_\n")
            .append("\n     - developed by: Chris D'Agostino\n\n");

    private final StringBuilder mainMenuOptionsStrBuilder = new StringBuilder()
            .append("---------- MAIN MENU -----------\n")
            .append("(1) Show files in ascending order\n")
            .append("(2) Perform file Operations\n")
            .append("(3) Close the application\n")
            .append("-------------------------------");

    private final StringBuilder subMenuOptionsStrBuilder = new StringBuilder()
            .append("---------- OPERATIONS MENU -------------\n")
            .append("(1) Add a file to the directory.\n")
            .append("(2) Delete a file in the directory.\n")
            .append("(3) Search for a file in the directory.\n")
            .append("(4) Back to main menu.\n")
            .append("----------------------------------------");

    private Scanner scanner = new Scanner(System.in);
    private Operations fileOperations;

    /* DEPENDENCY INJECTION

        The Menu needs a Operations object. Many different implementations of Operations can be made.

        For example, I created the "FileOperationsImpl" class which is a subclass
        of FileOperations (which in turn implements Operations), that provides some
        alternative functionality for the search and delete methods through polymorphism.

        For now, I have provided a subclass that contains different functionality for the "searchForFile"
        and "showFilesInDirectory" methods. Some of the instructions for this project can be interpreted in many
        different ways, so this subclass I have made provides a different take on the instructions.
     */
    public Menu(Operations fileOperations){
        this.fileOperations = fileOperations;
    }

    public void displayMainMenu() {

        System.out.println(mainMenuAsciiArtStrBuilder);

        while (true) {
            System.out.println(mainMenuOptionsStrBuilder);

            String selection = scanner.nextLine();

            switch (selection) {
                case "1":
                    fileOperations.showFilesInDirectory();
                    break;
                case "2":
                    displayOperationsMenu();
                    break;
                case "3":
                    displayQuitMenu();
                    break;
                default:
                    System.out.println("Error: " + selection + " is not a valid option." +
                            "Please enter a number from 1 - 3.");

            }
        }
    }

    public void displayOperationsMenu() {

        boolean isExitSelected = false;

        do {
            System.out.println(subMenuOptionsStrBuilder);
            String selection = scanner.nextLine();
            String filePathString = "";

            switch (selection) {
                case "1":
                    System.out.println("Please provide the path of the file: ");
                    filePathString = scanner.nextLine();

                    System.out.println(fileOperations.addFile(filePathString));
                    break;
                case "2":
                    System.out.println("Enter the full path of the file you want to delete (case sensitive!): ");
                    filePathString = scanner.nextLine();

                    try {
                        System.out.println(fileOperations.deleteFile(filePathString));
                    } catch (IOException e) {
                        System.out.println("Unable to delete file.");
                    }

                    break;
                case "3":
                    System.out.println("Enter the full path of the file (case sensitive!): ");
                    filePathString = scanner.nextLine();

                    if (fileOperations.searchForFile(filePathString))
                        System.out.println("File exists!");
                    else
                        System.out.println("File not found.");

                    break;
                case "4":
                    isExitSelected = true;
                    break;
                default:
                    System.out.println("Error: " + selection + " is not a valid option. " +
                            "Please enter a number from 1 - 4.");
            }
        } while (!isExitSelected);
    }

    /*
        A way for the user to return to the main menu in case they selected '3' by mistake.
     */
    public void displayQuitMenu(){
        System.out.println("Are you sure you want to quit? Enter (Y)es to confirm or any other string to continue.");
        String quitSelection = scanner.nextLine();

        if (quitSelection.equalsIgnoreCase("Y") || quitSelection.equalsIgnoreCase("YES")) {
            System.out.println("Closing LockedMe program...");
            scanner.close();
            System.exit(0);
        }
    }

}
