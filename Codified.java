package task_2;

import java.sql.SQLException;
import java.util.Scanner;

public class Codified {

    public static void main(String[] args) throws SQLException {
        // flag to change state when program termination is initiated
        boolean running = true;

        // initiate dbHandler session (initiate connection, initiate access)
        DBHandler db = new DBHandler();

        // register user inputs
        Scanner inputMonitor = new Scanner(System.in);

        // overall program loop
        while (running) {
            // menu options displayed in terminal
            System.out.println("\nWelcome to codified!");
            System.out.println("Please select your function from the menu below:");
            System.out.println("'1' to ADD a book to bookBASE.");
            System.out.println("'2' to UPDATE a book in bookBASE.");
            System.out.println("'3' to DELETE a book from bookBASE.");
            System.out.println("'4' to SEARCH a book in bookBASE.");
            System.out.println("'5' to VIEW all bookBASE records.");
            System.out.println("'0' to quit bookBASE.");

            // register user input to determine loop branch
            String userInput = inputMonitor.next();

            // enhanced switch for all branching functionality
            switch (userInput) {
                // quit case
                case "0" -> {
                    System.out.println("\nSession Terminated");
                    System.out.println("Bye.");
                    running = false; // state changed, loop will not renew
                }
                // record entry branch
                case "1" -> db.createRecord();

                // record update branch
                case "2" -> db.updateRecord();

                // record delete branch
                case "3" -> db.deleteRecord();

                // record search branch
                case "4" -> db.searchRecord();

                // view all records branch
                case "5" -> System.out.println(db.toStringDB());

                // handling invalid inputs
                default -> System.out.println("\nInvalid input! Please submit an input from the list below.");
            }
        }
        // close connection, close Scanner
        db.closeConnection();
        inputMonitor.close();
    }
}
