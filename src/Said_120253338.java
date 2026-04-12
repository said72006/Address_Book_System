// ===================== Imports =====================

import java.io.File;
import java.io.FileWriter;
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

public class Said_120253338 {

    // ===================== Utility =====================

    public static void SuccessMessage() {
        System.out.println("The process completed successfully.");
    }

    // ===================== CRUD OPERATIONS =====================

    public static void addContact(ArrayList<Contact_info> contactInfos, Scanner input) {
        System.out.println("input contact info: ");
        System.out.print("name: ");

        String name = input.nextLine();
        if(name.isEmpty()){
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
        String number = input.nextLine();
        if(number.isEmpty()){
            System.out.println("Number cannot be empty.");
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
            saveContacts(contactInfos);
            SuccessMessage();
        }
    }

    public static void searchByName(ArrayList<Contact_info> contactInfos, Scanner input) {
        System.out.print("Enter name to search: ");
        String searchName = input.nextLine();

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


    public static void saveContacts(ArrayList<Contact_info> contactInfos) {
        try {
            FileWriter writer = new FileWriter("contacts.txt");

            for (Contact_info c : contactInfos) {
                writer.write(c.name + "|" + c.type + "|" + c.number + "\n");
            }

            writer.close();
            System.out.println("Contacts saved successfully!");

        } catch (Exception e) {
            System.out.println("Error saving file.");
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
                String[] data = line.split("\\|");

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
            int choice = input.nextInt();
            input.nextLine();
            // CASE depend on choice
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
        }
    }
}