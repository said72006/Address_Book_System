// ===================== Imports =====================

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

// ===================== Data Model =====================

class Contact_info {
    String name;
    String type;
    String number;

    Contact_info(String name, String type, String number) {
        this.name   = name  ;
        this.type   = type  ;
        this.number = number;
    }
}

// ===================== Main Class =====================

public class AddressBook {

    // ===================== Utility =====================

    public static void SuccessMessage() {
        System.out.println("The process completed successfully.");
    }

    // ===================== CRUD OPERATIONS =====================

    public static void addContact(ArrayList<Contact_info> contactInfos, Scanner input) {
        System.out.println("input contact info: ");
        System.out.print("name: ");

        String name = input.nextLine().trim();
        if(name.isEmpty() || name.contains("|")){
            System.out.println("Name cannot be empty.");
            return;
        }
        System.out.print("type: ");
        String type = input.nextLine().toLowerCase();
        if (!type.equals("family") &&
                !type.equals("personal") &&
                !type.equals("work") &&
                !type.equals("other")) {

            System.out.println("Invalid type. It will be considered as Other.");
            type = "other";
        }
        type = type.substring(0,1).toUpperCase() + type.substring(1);
        System.out.print("number: ");
        String number = input.nextLine().trim();
        if(!number.matches("(?=.*[0-9].*[0-9].*[0-9])[+0-9() -]{3,30}")){
            System.out.println("Invalid phone number: at least three digits are required.");
            return;
        }
        boolean exists = false;

        for (Contact_info c : contactInfos) {
            if (c.number.equals(number)) {
                exists = true;
                break;
            }
        }
        if (exists) {
            System.out.println("This number already exists.");
        } else {
            contactInfos.add(new Contact_info(name, type, number));
            if (saveContacts(contactInfos)) SuccessMessage();
        }
    }

    public static void searchByName(ArrayList<Contact_info> contactInfos, Scanner input) {
        System.out.print("Enter name to search: ");
        String searchName = input.nextLine().trim();
        if (searchName.isEmpty()) { System.out.println("Name cannot be empty."); return; }

        boolean found = false;

        for (Contact_info c : contactInfos) {
            if (c.name.toLowerCase().contains(searchName.toLowerCase())) {
                System.out.println(c.name + " | " + c.type + " | " + c.number);
                found = true;
            }
        }
        if (found) {
            SuccessMessage();

        } else {
            System.out.println("Not found");
        }
    }

    public static void searchByNumber(ArrayList<Contact_info> contactInfos, Scanner input) {
        System.out.print("Enter number to search: ");
        String searchNumber = input.nextLine();
        boolean foundNum = false;

        for (Contact_info c : contactInfos) {
            if (c.number.equals(searchNumber)) {
                System.out.println(c.name + " | " + c.type + " | " + c.number);
                foundNum = true;
            }
        }

        if (foundNum) {
            SuccessMessage();

        } else {
            System.out.println("Not found");
        }
    }

    public static void deleteByName(ArrayList<Contact_info> contactInfos, Scanner input) {
        System.out.print("Enter name to delete: ");
        String deleteName = input.nextLine();
        int countDeleted = 0;

        for (int i = contactInfos.size() - 1; i >= 0; i--) {
            if (contactInfos.get(i).name.equals(deleteName)) {
                contactInfos.remove(i);
                countDeleted++;
            }
        }

        if (countDeleted > 0) {
            System.out.println(countDeleted + " contact(s) deleted.");
            saveContacts(contactInfos);
            SuccessMessage();
        } else {
            System.out.println("Not found");

        }
    }

    public static void deleteByNumber(ArrayList<Contact_info> contactInfos, Scanner input){
        System.out.print("Enter number to delete: ");
        String deleteNumber = input.nextLine();
        int countDeletedNum = 0;

        for (int i = contactInfos.size() - 1; i >= 0; i--) {
            if (contactInfos.get(i).number.equals(deleteNumber)) {
                contactInfos.remove(i);
                countDeletedNum++;
            }
        }

        if (countDeletedNum > 0) {
            System.out.println(countDeletedNum + " contact(s) deleted.");
            saveContacts(contactInfos);
            SuccessMessage();
        } else {
            System.out.println("Not found");

        }
    }

    public static void showAllContacts(ArrayList<Contact_info> contactInfos){
        if (contactInfos.isEmpty()) {
            System.out.println("There are no contacts in the system.");
        } else {
            for (Contact_info c : contactInfos) {
                System.out.println(c.name + " | " + c.type + " | " + c.number);
            }
                SuccessMessage();
        }
    }

    // ===================== FILE HANDLING =====================


    // Write in the same directory, then replace the old file in one move.
    // This avoids destroying the last saved address book on interrupted writes.
    public static boolean saveContacts(ArrayList<Contact_info> contactInfos) {
        Path temp = null;
        try {
            Path target = Path.of("contacts.txt").toAbsolutePath();
            temp = Files.createTempFile(target.getParent(), ".contacts-", ".tmp");
            try {
                Files.setPosixFilePermissions(temp, PosixFilePermissions.fromString("rw-------"));
            } catch (UnsupportedOperationException ignored) {
                // POSIX file permissions are not available on every operating system.
            }

            List<String> lines = new ArrayList<>();
            for (Contact_info c : contactInfos) {
                lines.add(c.name + "|" + c.type + "|" + c.number);
            }
            Files.write(temp, lines, StandardCharsets.UTF_8);
            try {
                Files.move(temp, target, StandardCopyOption.ATOMIC_MOVE,
                           StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temp, target, StandardCopyOption.REPLACE_EXISTING);
            }
            System.out.println("Contacts saved successfully!");
            return true;
        } catch (IOException | SecurityException e) {
            System.out.println("Error saving file. Existing data was not overwritten.");
            return false;
        } finally {
            if (temp != null) {
                try {
                    Files.deleteIfExists(temp);
                } catch (IOException ignored) {
                    // A best-effort cleanup only.
                }
            }
        }
    }

    public static void loadContacts(ArrayList<Contact_info> contactInfos) {
        try {
            File file = new File("contacts.txt");

            if (!file.exists()) {
                return;
            }

            Scanner fileReader = new Scanner(file);

            while (fileReader.hasNextLine()) {
                String line = fileReader.nextLine();
                String[] data = line.split("\\|", -1);
                if (data.length != 3 || data[0].isBlank() || data[2].isBlank()) {
                    System.out.println("Skipping malformed contact entry.");
                    continue;
                }

                contactInfos.add(new Contact_info(data[0], data[1], data[2]));
            }

            fileReader.close();

        } catch (Exception e) {
            System.out.println("Error loading file.");
        }
    }

    // ===================== APPLICATION ENTRY POINT =====================

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        ArrayList<Contact_info> contactInfos = new ArrayList<>();
        loadContacts(contactInfos);
        boolean running = true;
        while (running) {

            System.out.println("===================================================================\n" +
                    "Welcome to our Address book, please to find what you want:");
            System.out.print("1. Add new contact.\n" +
                    "2. Search by name.\n" +
                    "3. Search by number.\n" +
                    "4. Delete contact by name.\n" +
                    "5. Delete contact by number.\n" +
                    "6. Show all contacts.\n" +
                    "7. Exit\n" +
                    "Please to enter your choice: ");
            // STORE choice
            if (!input.hasNextLine()) {
                System.out.println("Input closed.");
                break;
            }
            String choiceText = input.nextLine().trim();
            int choice;
            try {
                choice = Integer.parseInt(choiceText);
            } catch (NumberFormatException e) {
                System.out.println("Invalid choice. Enter a number from 1 to 7.");
                continue;
            }
            // CASE depend on choice
            try {
            switch (choice) {
                case 1:
                    addContact(contactInfos, input);
                    break;
                case 2:
                    searchByName(contactInfos, input);
                    break;
                case 3:
                    searchByNumber(contactInfos, input);
                    break;
                case 4:
                    deleteByName(contactInfos, input);
                    break;

                case 5:
                    deleteByNumber(contactInfos, input);
                    break;
                case 6:
                    showAllContacts(contactInfos);
                    break;
                case 7:
                    System.out.println("thank you for using our address book. ^-^");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
            } catch (java.util.NoSuchElementException e) {
                System.out.println("Input closed.");
                break;
            }
        }
    }
}