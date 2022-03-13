package com.company;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/*
    Author: Araam Zaremehrjardi
    Date Created: Feb 21, 2022
    Date Edited: Feb 24, 2022

    Class: DatabaseSystem
    Purpose: The purpose of DatabaseSystem is to abstract necessary functions used by the user. These functions are
    used to configure the overarching database application itself. Currently, only necessary functionality of the
    class is to provide a parsing interface for SQL commands. In the future, the DatabaseSystem will provide more
    programmatic methods to interact with the database.
    - Variables:
    1. databaseAbstraction: DatabaseAbstraction
       Purpose: The purpose of "databaseAbstraction" is to instantiate an instance of the underlying database system.
       The DatabaseSystem class bases itself by using composition for access to the DatabaseAbstraction layer using
       the "databaseAbstraction" variable. Functions exposed by "databaseAbstraction" are functions used to indirectly
       interact with the file system to only do read/write operations for databases and tables within each database.
    - Functions:
    1. execute(): void
*/
public class DatabaseSystem {
    private DatabaseAbstraction databaseAbstraction;

    public DatabaseSystem() {
        databaseAbstraction = new DatabaseAbstraction();
    }

    /*
    Function: execute
    Purpose: The purpose of execute() is to be a parser for SQL commands entered by the user to interact with the
    database. The parser first converts vital information from the SQL statements such as keywords and values used to
    create databases schema. Vital information is processed by removing parentheses and commas to only have tokens.
    These tokens are saved within "tokens" variable and then further processed into a Queue data structure called
    "token_queue." Each token is then processed by the series of switch statements with functions executed within the
    end points of each switch statement. Once the entire Queue is finished processing and is empty, the execute function
    ends for one SQL statement. The execute function never directly interacts with the database and instead uses
    functions provided by "databaseAbstraction" to abstract primitive functionality of the database interactions.
    - Parameters:
    1. command: String
    - Return Type: void
     */
    public void execute(String command) {

        Queue<String> token_queue = lexicalAnalysis(command);

        switch (token_queue.remove()) {
            case "CREATE": {
                switch (token_queue.remove()) {
                    case "DATABASE": {
                        String database = token_queue.remove();

                        if (databaseAbstraction.createDatabase(database)) {
                            System.out.println("Database " + database + " created.");
                        } else {
                            System.out.println("!Failed to create database " + database + " because it already exists.");
                        }

                        return;
                    }

                    case "TABLE": {
                        String table = token_queue.remove();

                        if (databaseAbstraction.createTable(table)) {
                            System.out.println("Table " + table + " created.");
                        } else {
                            System.out.println("!Failed to create database " + table + " because it already exists.");
                        }

                        String terminator = token_queue.peek();

                        if (terminator == ";") {
                            return;
                        }

                        while (!token_queue.isEmpty()) {
                            String label = token_queue.remove();
                            String type = token_queue.remove();

                            if (type.matches("varchar") || type.matches("char")) {
                                String variable = token_queue.remove();
                                type = "%s(%s)".formatted(type, variable);
                                databaseAbstraction.addColumn(table, label, type);
                            } else {
                                databaseAbstraction.addColumn(table, label, type);
                            }
                        }

                        return;
                    }
                }
            }

            case "DROP": {
                switch (token_queue.remove()) {
                    case "DATABASE": {
                        String database = token_queue.remove();

                        if (databaseAbstraction.dropDatabase(database)) {
                            System.out.println("Database " + database + " deleted.");
                        } else {
                            System.out.println("!Failed to delete " + database + " because it does not exist.");
                        }

                        return;
                    }

                    case "TABLE": {
                        String table = token_queue.remove();

                        if (databaseAbstraction.dropTable(table)) {
                            System.out.println("Table " + table + " deleted.");
                        } else {
                            System.out.println("!Failed to delete " + table + " because it does not exist.");
                        }

                        return;
                    }
                }
            }

            case "USE": {
                String database = token_queue.remove();

                if (databaseAbstraction.setCurrentDatabase(database)) {
                    System.out.println("Using database " + database + ".");
                } else {
                    System.out.println("!Failed to use database " + database + " because it does not exist.");
                }

                return;
            }

            case "ALTER": {
                switch (token_queue.remove()) {
                    case "TABLE": {
                        String table = token_queue.remove();

                        switch (token_queue.remove()) {
                            case "ADD": {
                                String label = token_queue.remove();
                                String type = token_queue.remove();

                                if (type.matches("varchar") || type.matches("char")) {
                                    String variable = token_queue.remove();
                                    type = "%s(%s)".formatted(type, variable);
                                    databaseAbstraction.addColumn(table, label, type);
                                } else {
                                    databaseAbstraction.addColumn(table, label, type);
                                }

                                System.out.println("Table " + table + " modified.");

                                return;
                            }
                        }
                    }
                }
            }

            case "SELECT": {
                String column = token_queue.remove();

                switch (column) {
                    case "*": {
                        String FROM = token_queue.remove();
                        String table = token_queue.remove();
                        String[] columns = null;

                        try {
                            columns = databaseAbstraction.selectColumn(table);
                        } catch (Exception exception) {
                            System.out.println("!Failed to query table " + table + " because it does not exist");
                            return;
                        }

                        for (int columnIndex = 0; columnIndex < columns.length; columnIndex++) {
                            System.out.print(columns[columnIndex]);
                            if (columnIndex < columns.length - 1) {
                                System.out.print(" | ");
                            }
                        }

                        System.out.println();

                        return;
                    }
                }
            }

            case "INSERT":

                switch (token_queue.remove()) {

                    case "INTO":

                        String table = token_queue.remove();
                        token_queue.remove(); // Remove values token
                        Queue<String> values = new LinkedList<String>();

                        while (!token_queue.isEmpty()) {
                            String value = token_queue.remove();
                            values.add(value);
                        }

                        String[] row_values = new String[values.size()];
                        values.toArray(row_values);
                        if (databaseAbstraction.appendRow("Product", row_values)) {
                            System.out.println("1 new record inserted.");
                        } else {
                            System.out.println("0 new record inserted.");
                        }

                        return;

                }
        }
    }

    private Queue<String> lexicalAnalysis(String string) {

        String processed_string = string.replace("("," ");
        processed_string = processed_string.replace(")", " ");
        processed_string = processed_string.replace(",", "");
        processed_string = processed_string.replace(";","");
        processed_string = processed_string.replace("\t"," ");
        processed_string = processed_string.replace("'","");

        String[] tokens = processed_string.split(" ");
        Queue<String> token_queue = new LinkedList<String>();

        for (int i = 0; i < tokens.length; i++) {
            if (tokens[i] != "") {

                String token = tokens[i];

                switch (token) {
                    case "select": {
                        token = "SELECT";
                        break;
                    }

                    case "insert": {
                        token = "INSERT";
                        break;
                    }

                    case "into": {
                        token = "INTO";
                        break;
                    }

                }

                token_queue.add(token);
            }
        }

        return token_queue;
    }
}
