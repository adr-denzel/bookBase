package task_2;

import java.sql.*;
import java.util.Scanner;

public class DBHandler {

    // DB connection variables used in most methods
    private Connection connection;
    private Statement statement;
    private ResultSet results;

    // SQL string variables
    String sqlCode;

    // empty signature constructor, object initiated in bookBASE.java before loop starts
    public DBHandler() {
        try {
            // DB environment variables
            String URL = "jdbc:mysql://localhost:3306/ebookstore_db?useSSL=false";
            String USER = "denzel";
            String PASSWORD = "chelseafc";

            // establish connection via driver
            connection = DriverManager.getConnection(URL, USER, PASSWORD);

            // assign statement the connection object
            statement = connection.createStatement();
        } catch (SQLException e) {
            System.out.println("\nConnection to DB was not established.\n");
        }
    }

    // create a new record
    public void createRecord() {
        // register user inputs
        Scanner inputMonitor = new Scanner(System.in);

        // issue instructions to terminal, register responses
        System.out.println("\nPlease follow the prompts and submit the requested information to CREATE NEW RECORD.");

        System.out.println("\nPlease input the book ID:");
        String bookID = inputMonitor.nextLine();

        System.out.println("\nPlease input the book TITLE:");
        String bookTitle = inputMonitor.nextLine();

        System.out.println("\nPlease input the book AUTHOR:");
        String bookAuthor = inputMonitor.nextLine();

        System.out.println("\nPlease input the book QUANTITY:");
        String bookQty = inputMonitor.nextLine();

        System.out.println("\nCREATION PREVIEW");

        // preview creation to user, and trim any trailing/leading whitespace before db commit
        String preview = "Book ID: " + bookID.trim();
        preview += "\nTITLE: " + bookTitle.trim();
        preview += "\nAUTHOR: " + bookAuthor.trim();
        preview += "\nQUANTITY: " + bookQty.trim();

        System.out.println(preview);

        // option to commit or not commit changes
        System.out.println("\nWould you like to commit these changed?");
        System.out.println("'1' for YES");
        System.out.println("'0' for NO");
        String creationSubmit = inputMonitor.next();

        // if-else branch for committing db record creation
        if (creationSubmit.equals("1")) {
            try {
                sqlCode = String.format("(%s, '%s', '%s', %s)",
                        bookID, bookTitle, bookAuthor, bookQty);

                statement.executeUpdate("INSERT INTO books VALUES " + sqlCode);

                System.out.println("\nRecord created.");
                System.out.println("\nSee DB:");
                System.out.println(toStringDB());
            } catch (SQLIntegrityConstraintViolationException ex) {
                System.out.println("\nError! Primary Key (ID) already exists.");
            } catch (SQLSyntaxErrorException exc) {
                System.out.println("Error! Ensure you submit valid data types for the respective parameters.");
            } catch (SQLException e) {
                System.out.println("\nRecord creation failed.");
            }
        } else {
            System.out.println("\nRecord creation abandoned.");
        }
    }

    // update a record
    public void updateRecord() throws SQLException {
        // register user inputs, issue commands via terminal
        Scanner inputMonitor = new Scanner(System.in);
        System.out.println("\nPlease follow the prompts and submit the requested information to UPDATE EXISTING RECORD.");

        // display existing table records to user
        System.out.println("\nSee DB.");
        System.out.println(toStringDB());

        // request ID of record to update
        System.out.println("Please SELECT RECORD by submitting ID.");
        String bookID = inputMonitor.nextLine();

        try {
            results = statement.executeQuery("SELECT id, title, author, qty FROM books WHERE id = " + bookID);

            // if query returns data, prompt user for field to update
            if (results.next()) {
                System.out.println(idQuery(bookID));

                System.out.println("What field would you like UPDATED?");
                System.out.println("'1' for TITLE");
                System.out.println("'2' for AUTHOR");
                System.out.println("'3' for QUANTITY");
                System.out.println("'0' to make NO UPDATE");
                String updateSubmit = inputMonitor.nextLine();

                // switch for selecting which field to update
                switch (updateSubmit) {
                    case "1" -> {
                        System.out.println("Please enter UPDATED TITLE:");
                        String bookTitle = inputMonitor.nextLine();
                        try {
                            statement.executeUpdate("UPDATE books SET title = '%s' WHERE id = %s".formatted(bookTitle.trim(), bookID));

                            System.out.println("\nRecord UPDATED.");
                            System.out.println("\nSee Record:");
                            System.out.println(idQuery(bookID));
                        } catch (SQLException e) {
                            System.out.println("\nRecord update failed.");
                        }
                    }
                    case "2" -> {
                        System.out.println("Please enter UPDATED AUTHOR:");
                        String bookAuthor = inputMonitor.nextLine();

                        try {
                            statement.executeUpdate("UPDATE books SET author = '%s' WHERE id = %s".formatted(bookAuthor.trim(), bookID));

                            System.out.println("\nRecord UPDATED.");
                            System.out.println("\nSee Record:");
                            System.out.println(idQuery(bookID));
                        } catch (SQLException e) {
                            System.out.println("\nRecord update failed.");
                        }
                    }
                    case "3" -> {
                        System.out.println("Please enter UPDATED QUANTITY:");
                        String bookQuantity = inputMonitor.nextLine();

                        try {
                            statement.executeUpdate("UPDATE books SET qty = %s WHERE id = %s".formatted(bookQuantity.trim(), bookID));

                            System.out.println("\nRecord UPDATED.");
                            System.out.println("\nSee Record:");
                            System.out.println(idQuery(bookID));
                        } catch (SQLException e) {
                            System.out.println("\nRecord update failed.");
                        }
                    }
                    default -> System.out.println("\nRecord UPDATE ABANDONED."); // to abandon record update
                }
            } else {
                System.out.println("\nNo matching BOOK ID for: " + bookID); // if null query is returned
            }
        } catch (SQLException e) {
            System.out.println("Error. Please ensure you submit an integer.");
        }
    }

    // delete a record
    public void deleteRecord() throws SQLException {
        // register user inputs, issue prompts to user in terminal
        Scanner inputMonitor = new Scanner(System.in);
        System.out.println("\nPlease follow the prompts and submit the requested information to DELETE EXISTING RECORD.");

        // list all db records
        System.out.println("\nSee DB.");
        System.out.println(toStringDB());

        // request id of record to delete
        System.out.println("Please SELECT RECORD by submitting ID.");
        String bookID = inputMonitor.nextLine();

        try {
            results = statement.executeQuery("SELECT id, title, author, qty FROM books WHERE id = " + bookID);

            // if query executes, selected record to delete exists
            if (results.next()) {
                // prompt options to delete record
                System.out.println("\nAre you sure you want to delete the following record:");
                System.out.println(idQuery(bookID));
                System.out.println("'1' for YES");
                System.out.println("'0' for NO");

                String deleteSubmit = inputMonitor.nextLine();

                // if else to delete based on user response to prompt
                if (deleteSubmit.equals("1")) {
                    try {
                        statement.executeUpdate("DELETE FROM books WHERE id = " + bookID);
                        results = statement.executeQuery("SELECT id, title, author, qty FROM books WHERE id = " + bookID);
                        // if record query is null, deletion successful
                        if (!results.next()) {
                            System.out.println("Record deleted."); // confirmation of record deletion
                        } else {
                            System.out.println("Error! Record not deleted."); // else if query returns result, error in deletion occurred
                        }
                    } catch (SQLException e) {
                        System.out.println("Error! Deletion not completed");
                    }
                } else {
                    System.out.println("Deletion abandoned."); // user abandons deletion
                }
            } else {
                System.out.println("\nNo matching BOOK ID for: " + bookID); // query returns null, record does not exist in db
            }
        } catch (SQLException e) {
            System.out.println("\nError! Please ensure you submit an integer.");
        }
    }

    // search specific records
    public void searchRecord() {
        // register user inputs, and issue instructions to terminal
        Scanner inputMonitor = new Scanner(System.in);

        System.out.println("\nPlease select your book SEARCH PARAMETER:");
        System.out.println("'1' BOOK ID");
        System.out.println("'2' BOOK TITLE");
        System.out.println("'3' BOOK AUTHOR");

        String searchSubmit = inputMonitor.nextLine();

        // switch to determine what field to query when running with search term
        switch (searchSubmit) {
            // id field
            case "1" -> {
                System.out.println("\nPlease submit the BOOK ID 'search term':");
                String searchParameter = inputMonitor.nextLine();
                try {
                    results = statement.executeQuery("SELECT id, title, author, qty FROM books WHERE id = " + searchParameter);
                    if (results.next()) {
                        System.out.println(idQuery(searchParameter));
                    } else {
                       System.out.println(searchParameter + " had no matching record.");
                    }
                } catch (SQLException e) {
                    System.out.println("\nError. Please ensure you submit an integer.");
                }
            }
            // title field
            case "2" -> {
                System.out.println("\nPlease submit the BOOK TITLE 'search term':");
                String searchParameter = inputMonitor.nextLine();
                try {
                    results = statement.executeQuery("SELECT id, title, author, qty FROM books WHERE title = '%s'".formatted(searchParameter));
                    if (results.next()) {
                        System.out.println(titleQuery(searchParameter));
                    } else {
                        System.out.println(searchParameter + " had no matching record.");
                    }
                } catch (SQLException e) {
                    System.out.println("\nConnection error.");
                }
            }
            // author field
            case "3" -> {
                System.out.println("\nPlease submit the BOOK AUTHOR 'search term':");
                String searchParameter = inputMonitor.nextLine();
                try {
                    results = statement.executeQuery("SELECT id, title, author, qty FROM books WHERE author = '%s'".formatted(searchParameter));
                    if (results.next()) {
                        System.out.println(authorQuery(searchParameter));
                    } else {
                        System.out.println(searchParameter + " had no matching record.");
                    }
                } catch (SQLException e) {
                    System.out.println("\nConnection error.");
                }
            }
            default -> System.out.println("\nNo SEARCH PARAMETER selected.");
        }
    }

    // list all DB records
    public String toStringDB () throws SQLException {
        StringBuilder db = new StringBuilder("\nID | TITLE | AUTHOR | QUANTITY\n");
        results = statement.executeQuery("SELECT id, title, author, qty FROM books");

        while (results.next()) {
            db.append(results.getInt("id"));
            db.append(", ").append(results.getString("title"));
            db.append(", ").append(results.getString("author"));
            db.append(", ").append(results.getInt("qty"));
            db.append("\n");
        }
        return db.toString();
    }

    // list single DB record based on id query
    public String idQuery(String bookParameter) throws SQLException {
        StringBuilder db = new StringBuilder("\nID | TITLE | AUTHOR | QUANTITY\n");
        results = statement.executeQuery("SELECT id, title, author, qty FROM books WHERE id = %s".formatted(bookParameter));

        while (results.next()) {
            db.append(results.getInt("id"));
            db.append(", ").append(results.getString("title"));
            db.append(", ").append(results.getString("author"));
            db.append(", ").append(results.getInt("qty"));
            db.append("\n");
        }
        return db.toString();
    }

    // list single DB record based on title query
    public String titleQuery(String bookParameter) throws SQLException {
        StringBuilder db = new StringBuilder("\nID | TITLE | AUTHOR | QUANTITY\n");
        results = statement.executeQuery("SELECT id, title, author, qty FROM books WHERE title = '%s'".formatted(bookParameter));

        while (results.next()) {
            db.append(results.getInt("id"));
            db.append(", ").append(results.getString("title"));
            db.append(", ").append(results.getString("author"));
            db.append(", ").append(results.getInt("qty"));
            db.append("\n");
        }
        return db.toString();
    }

    // list single DB record based on author query
    public String authorQuery(String bookParameter) throws SQLException {
        StringBuilder db = new StringBuilder("\nID | TITLE | AUTHOR | QUANTITY\n");
        results = statement.executeQuery("SELECT id, title, author, qty FROM books WHERE author = '%s'".formatted(bookParameter));

        while (results.next()) {
            db.append(results.getInt("id"));
            db.append(", ").append(results.getString("title"));
            db.append(", ").append(results.getString("author"));
            db.append(", ").append(results.getInt("qty"));
            db.append("\n");
        }
        return db.toString();
    }

    // security consideration, ensure connections close once user elects to quit program
    public void closeConnection() {
        try {
            // closed connection variables
            connection.close();
            statement.close();
        } catch (SQLException e) {
            System.out.println("\nConnection to DB was not established.\n");
        }
    }
}