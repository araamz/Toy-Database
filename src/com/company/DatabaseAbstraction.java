package com.company;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/*
    Author: Araam Zaremehrjardi
    Date Created: Feb 20, 2022
    Date Edited: Feb 24, 2022

    Class: DatabaseAbstraction
    Purpose: The purpose of DatabaseAbstraction is to provide an abstraction for primitive functionality of the
    database. Primitive functionality is direct interaction of the file system to create, delete, and edit files for
    database functionality such as creating and deleting tables, databases, and directly writing and editing text files.
    Other functionality provided by the class is helper functions used to column interaction such as read and write
    operations and schema manipulation.
    - Variables:
    1. currentDatabase: String
       Purpose: The variable currentDatabase is a "pointer" to the current database being a directory that stores text
       files as tables within a database. This focuses the operations to a database and its tables.
    2. databasesDirectory: String
       Purpose: The variable databasesDirectory is used to define the main directory that stores all the databases
       created by the application. Each directory within the defined path for databasesDirectory is a database and files
       within the directory are tables for that specific database.
    - Functions:
    1. setCurrentDatabase(database: String): String
    2. createDatabase(database: String): boolean
    3. dropDatabase(database: String): boolean
    4. createTable(table: String): boolean
    5. dropTable(table: String): boolean
    6. addColumn(table: String, label: String, type: String): boolean
    7. selectColumn(table: String): String[]
*/
public class DatabaseAbstraction {
    private String currentDatabase = null;
    private static final String databasesDirectory = "databases/";

    public DatabaseAbstraction() {}

    /*
    Function: setCurrentDatabase
    Purpose: The purpose of setCurrentDatabase() is to set the current database being pointed to for read and write
    operations for tables. The function firstly uses the File Library to create a point in the file system used to check
    if the directory (and therefore database) exists. If the database exists, the variable currentDatabase is updated
    to the database directory. If directory for the database does not exist the function is unsuccessful and returns
    false and vice versa.
    - Parameters:
    1. database: String
    - Return Type: boolean
     */
    public boolean setCurrentDatabase(String database) {
        String existingDatabasePath = databasesDirectory + database.toLowerCase() + "/";
        File location = new File(existingDatabasePath);

        if (location.isDirectory()) {
            currentDatabase = existingDatabasePath;
            return true;
        }

        return false;
    }

    /*
    Function: createDatabase
    Purpose: The purpose of createDatabase() is to create a new database. A new database is created by creating a new
    folder within the defined databases' directory. The function uses the File library to create a point in the file
    system used to check if the database is already created within the databases' directory. If the directory for the
    database does not exist the function is unsuccessful and vice versa. The result of the File library function to
    create directories is the returned value if a directory was created.
    - Parameters:
    1. database: String
    - Return Type: boolean
    */
    public boolean createDatabase(String database) {
        String databasePath = databasesDirectory + database.toLowerCase();
        File location = new File(databasePath);

        return location.mkdirs();
    }

    /*
    Function: dropDatabase
    Purpose: The purpose of dropDatabase() is to delete an existing database. A database is deleted if the
    directory exists with the matching database string passed into the function. The function uses the File library to
    crate a point in the file system used to check if the database exists in the databases' directory. If the database
    does not exist then the function returns unsuccessful for not being able to delete the denoted database and vice
    versa.
    - Parameters:
    1. database: String
    - Return Type: boolean
    */
    public boolean dropDatabase(String database) {
        String databasePath = databasesDirectory + database.toLowerCase();
        File location = new File(databasePath);

        return location.delete();
    }

    /*
    Function: createTable
    Purpose: The purpose of createTable() is to create a new text file within a database directory that is used to be a
    table for the database. A new text file is created with the passed in table value. The function uses the File
    library to create a point in the file system used to check if a file exists in the selected database directory.
    The table is selected within the current database folder. If the currentDatabase does not exist the function or
    is already created the function is unsuccessful and vice versa. If the table file does not exist, then the function
    is successful.
    - Parameters:
    1. table: String
    - Return Type: boolean
    */
    public boolean createTable(String table) {
        String tablePath = currentDatabase + table.toLowerCase() + ".txt";
        File location = new File(tablePath);

        if (currentDatabase == null) {
            return false;
        }

        if (location.exists()) {
            return false;
        }

        try {
            location.createNewFile();
        } catch (IOException exception) {
            exception.printStackTrace();
            return false;
        }

        return true;
    }

    /*
    Function: dropTable
    Purpose: The purpose of dropTable() is to delete a text file within a database directory that is used to be a
    table for the database. The deleted text file is identified with the passed in table value. The function uses
    the File library to create a point in the file system used to check if a file exists in the selected database directory.
    The table is selected within the current database folder. If the currentDatabase does not exist the function or
    is already created the function is unsuccessful and vice versa. If the table file does not exist and is created,
    then the function is successful.
    - Parameters:
    1. table: String
    - Return Type: boolean
    */
    public boolean dropTable(String table) {
        String tablePath = currentDatabase + table.toLowerCase() + ".txt";
        File location = new File(tablePath);

        return location.delete();
    }

    /*
    Function: addColumn
    Purpose: The purpose of addColumn() is to create a new column within a selected table that takes in both the column
    label and type. The function defines the schema for a table by using a defined expression format of a label and
    type separated by a space for one column definition. A tab between each column separates the schema and values. The
    function first defines the format for a column definition using the variable expression and formatting a header with
    the expression to take in the passed in label and type. The function ensures the table does exist within the
    selected database and reads the schema of the table to ensure the column is not already added in the table (if it is
    the function added column is unsuccessful and fails). If the table exists and there is no duplicate column, the new
    column is appended to the end of the first line of the table file. If added, the function is successful and vice
    versa.
    - Parameters:
    1. table: String
    - Return Type: boolean
    2. label: String
    - Return Type: label
    3. type: String
    - Return Type: type
    */
    public boolean addColumn(String table, String label, String type) {
        String expression = "%s %s\t";
        String header = expression.formatted(label, type);

        String tablePath = currentDatabase + table.toLowerCase() + ".txt";
        File location = new File(tablePath);

        String headings[] = null;

        FileWriter tableWriter = null;

        if (!location.exists()) {
            return false;
        }

        try {
            headings = getHeadings(table);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (headings != null ) {

            for (int headerIndex = 0; headerIndex < headings.length; headerIndex++) {
                String headerLabel = header.split("\t")[0];

                if (headings[headerIndex].equals(headerLabel)) {
                    return false;
                }
            }
        }

        try {
            tableWriter = new FileWriter(location, true);
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        try {
            tableWriter.append(header);
            tableWriter.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return true;
    }

    /*
    Function: selectColumn
    Purpose: The purpose of selectColumn() is to return the values of column using the passed in table name. The
    function uses the File Library to create a point in the file system used to check if the table exists within the
    database directory. If the table does not exist, an exception is thrown. The function returns an array of all the
    columns within the table for this function definition. The behavior of selectColumn() changes based upon the
    function signature using polymorphic methods to achieve different implementations of selectColumn().
    - Parameters:
    1. table: String
    - Return Type: String[]
    */
    public Queue<String[]> selectColumn(String table) throws Exception {
        String tablePath = currentDatabase + table.toLowerCase() + ".txt";
        File location = new File(tablePath);

        BufferedReader tableReader = null;
        Queue<String[]> rows = new LinkedList<>();

        if (!location.exists()) {
            throw new Exception("USER EXCEPTION - selectColumn: table " + table + " does not exist.");
        }

        try {
            tableReader = new BufferedReader(new FileReader(location));
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        try {
            while (true) {
                String row = tableReader.readLine();
                if (row != null) {
                    String[] columns = row.split("\t");
                    rows.add(columns);
                } else {
                    break;
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return rows;
    }

    public Queue<String[]> selectColumn(String table, String key, String value, LinkedList<String> selected_columns) throws Exception {
        String tablePath = currentDatabase + table.toLowerCase() + ".txt";
        File location = new File(tablePath);
        BufferedReader tableReader = null;


        Queue<String[]> rows = new LinkedList<>();
        String[] headings = null;

        if (!location.exists()) {
            throw new Exception("USER EXCEPTION - selectColumn: table " + table + " does not exist.");
        }

        try {
            tableReader = new BufferedReader(new FileReader(location));
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        try {
            headings = getHeadings(table);
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        Integer keyIndex = null;
        Integer[] selectedColumns_indexes = new Integer[selected_columns.size()];
        int selectedColumnsIndexes_index = 0;
        for (int headingIndex = 0; headingIndex < headings.length; headingIndex++) {
            String heading_value = headings[headingIndex].split(" ")[0];
            if (heading_value.matches(key) && keyIndex == null) {
                keyIndex = headingIndex;
            } else if (!selected_columns.isEmpty()) {

                for (int columnIndex = 0; columnIndex < selected_columns.size(); columnIndex++) {
                    if (heading_value.matches(selected_columns.get(columnIndex))) {
                        selectedColumns_indexes[selectedColumnsIndexes_index] = headingIndex;
                        selectedColumnsIndexes_index++;
                    }

                }

            }

        }

        try {

            while (true) {
                String row = tableReader.readLine();
                if (row != null) {
                    String[] columns = row.split("\t");
                    String keyIndex_value = columns[keyIndex].split(" ")[0];
                    if (!keyIndex_value.matches(value)) {

                        String[] data_row = new String[selectedColumns_indexes.length];
                        for (int columnIndex = 0; columnIndex < selectedColumns_indexes.length; columnIndex++) {
                            data_row[columnIndex] = columns[selectedColumns_indexes[columnIndex]];

                        }
                        rows.add(data_row);
                    }
                } else {
                    break;
                }

            }

        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return rows;

    }

    public int deleteRow_greaterThan(String table, String key, String value) {

        String tablePath = currentDatabase + table.toLowerCase() + ".txt";
        File location = new File(tablePath);
        BufferedReader tableReader = null;

        try {
            tableReader = new BufferedReader(new FileReader(location));
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        String[] headings = null;
        Integer keyIndex = null;
        Integer selectedColumn_index = null;
        try {
            headings = getHeadings(table);
        } catch (Exception exception) {
            exception.printStackTrace();
            return 0;
        }

        for (int headingIndex = 0; headingIndex < headings.length; headingIndex++) {
            String heading_value = headings[headingIndex].split(" ")[0];
            if (heading_value.matches(key) && keyIndex == null) {
                keyIndex = headingIndex;
            }

        }

        Queue<String[]> rows = new LinkedList<>();
        int records_deleted = 0;
        int header_skip = 0;

        try {

            while (true) {

                String row = tableReader.readLine();

                if (header_skip == 0) {
                    header_skip++;
                } else {
                    if (row != null) {
                        String[] columns = row.split("\t");
                        if (!filterRow_greaterThan(columns, keyIndex, value)) {

                            rows.add(columns);

                        } else {
                            records_deleted++;
                        }

                    } else {
                        break;
                    }
                }

            }

        } catch (Exception exception) {
            exception.printStackTrace();
        }

        BufferedWriter tableWriter = null;
        try {
            tableWriter = new BufferedWriter(new FileWriter(location, false));
            String heading_row = "";
            for (int headingIndex = 0; headingIndex < headings.length; headingIndex++) {
                String heading = headings[headingIndex] + "\t";
                heading_row += heading;
            }
            tableWriter.write(heading_row);
            tableWriter.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }


        rows.remove(); // Remove already saved headings, strictly data.
        try {
            tableWriter = new BufferedWriter(new FileWriter(location, true));
            while (!rows.isEmpty()) {
                String[] columns = rows.remove();
                if (!appendRow(table, columns)) {
                    throw new Exception();
                }
            }
            tableWriter.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return records_deleted;


    }

    public int deleteRow_equality(String table, String key, String value) {

        String tablePath = currentDatabase + table.toLowerCase() + ".txt";
        File location = new File(tablePath);
        BufferedReader tableReader = null;

        try {
            tableReader = new BufferedReader(new FileReader(location));
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        String[] headings = null;
        Integer keyIndex = null;
        Integer selectedColumn_index = null;
        try {
            headings = getHeadings(table);
        } catch (Exception exception) {
            exception.printStackTrace();
            return 0;
        }

        for (int headingIndex = 0; headingIndex < headings.length; headingIndex++) {
            String heading_value = headings[headingIndex].split(" ")[0];
            if (heading_value.matches(key) && keyIndex == null) {
                keyIndex = headingIndex;
            }

        }

        Queue<String[]> rows = new LinkedList<>();
        int records_deleted = 0;

        try {

            while (true) {
                String row = tableReader.readLine();
                if (row != null) {
                    String[] columns = row.split("\t");
                    if (!filterRow_equality(columns, keyIndex, value)) {

                        rows.add(columns);

                    } else {
                        records_deleted++;
                    }

                } else {
                    break;
                }

            }

        } catch (Exception exception) {
            exception.printStackTrace();
        }

        BufferedWriter tableWriter = null;
        try {
            tableWriter = new BufferedWriter(new FileWriter(location, false));
            String heading_row = "";
            for (int headingIndex = 0; headingIndex < headings.length; headingIndex++) {
                String heading = headings[headingIndex] + "\t";
                heading_row += heading;
            }
            tableWriter.write(heading_row);
            tableWriter.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }


        rows.remove(); // Remove already saved headings, strictly data.
        try {
            tableWriter = new BufferedWriter(new FileWriter(location, true));
            while (!rows.isEmpty()) {
                String[] columns = rows.remove();
                if (!appendRow(table, columns)) {
                    throw new Exception();
                }
            }
            tableWriter.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return records_deleted;

    }

    public int updateTable_equality(String table, String key, String value, String selected_column, String new_value) {

        String tablePath = currentDatabase + table.toLowerCase() + ".txt";
        File location = new File(tablePath);
        BufferedReader tableReader = null;

        try {
            tableReader = new BufferedReader(new FileReader(location));
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        // Read heading values for table
        // Get selected_column_index and keyValueIndex
        // Read Table
        // As reading table values, look at selected_column_index and keyValueIndex to see if  keyValueIndex_value matches value
        // if it matches, replace value with new_value
        // add row to rows buffer
        // write rows to File with apendrow
        // return updated_record count

        String[] headings = null;
        Integer keyIndex = null;
        Integer selectedColumn_index = null;
        try {
            headings = getHeadings(table);
        } catch (Exception exception) {
            exception.printStackTrace();
            return 0;
        }

        for (int headingIndex = 0; headingIndex < headings.length; headingIndex++) {
            String heading_value = headings[headingIndex].split(" ")[0];
            if (heading_value.matches(key) && keyIndex == null) {
                keyIndex = headingIndex;
            }
            if (heading_value.matches(selected_column)) {

                selectedColumn_index = headingIndex;

            }

        }

        Queue<String[]> rows = new LinkedList<>();
        int records_modified = 0;

        try {

            while (true) {
                String row = tableReader.readLine();
                if (row != null) {
                    String[] columns = row.split("\t");
                    if (filterRow_equality(columns, keyIndex, value)) {

                        columns = updateRow(columns, selectedColumn_index, new_value);
                        records_modified++;

                    }

                    rows.add(columns);
                } else {
                    break;
                }

            }

        } catch (Exception exception) {
            exception.printStackTrace();
        }

        BufferedWriter tableWriter = null;
        try {
            tableWriter = new BufferedWriter(new FileWriter(location, false));
            String heading_row = "";
            for (int headingIndex = 0; headingIndex < headings.length; headingIndex++) {
                String heading = headings[headingIndex] + "\t";
                heading_row += heading;
            }
            tableWriter.write(heading_row);
            tableWriter.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }


        rows.remove(); // Remove already saved headings, strictly data.
        try {
            tableWriter = new BufferedWriter(new FileWriter(location, true));
            while (!rows.isEmpty()) {
                String[] columns = rows.remove();
                if (!appendRow(table, columns)) {
                    throw new Exception();
                }
            }
            tableWriter.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return records_modified;

    }

    public boolean appendRow(String table, String[] values) {
        String tablePath = currentDatabase + table.toLowerCase() + ".txt";
        File location = new File(tablePath);

        BufferedWriter tableWriter = null;
        String row = null;

        try {
            row = createRow(table, values);
        } catch (Exception exception) {
            exception.printStackTrace();
            return false;
        }

        if (!location.exists()) {
            return false;
        }

        try {
            tableWriter = new BufferedWriter(new FileWriter(location, true));
        } catch (Exception exception) {
            exception.printStackTrace();
            return false;
        }

        try {
            tableWriter.newLine();
            tableWriter.append(row);
            tableWriter.close();
        } catch (IOException exception) {
            exception.printStackTrace();
            return false;
        }

        return true;

    }

    private String createRow(String table, String[] values) throws Exception {
        String[] types = null;

        try {
            types = getHeadings(table);
            for (int typesIndex = 0; typesIndex < types.length; typesIndex++) {
                types[typesIndex] = types[typesIndex].split(" ")[1];
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        String row = "";

        for (int columnIndex = 0; columnIndex < types.length; columnIndex++) {
            String value = values[columnIndex];
            String type = types[columnIndex];

            if (type.matches("int")) {
                Integer row_value = Integer.parseInt(values[columnIndex]);
                row += row_value;
            } else if (type.matches("float")) {
                Float row_value = Float.parseFloat(values[columnIndex]);
                row += row_value;
            } else if (type.contains("varchar")) {
                String processed_type = type.replace("("," ").replace(")","");
                String[] tokens = processed_type.split(" ");
                Integer varchar_length_constraint = Integer.parseInt(tokens[1]);
                if (value.length() > varchar_length_constraint) {
                    throw new Exception("USER EXCEPTION - createRow: varchar(" + varchar_length_constraint + ") is not satisfied.");
                }

                row += value;
            } else {
                throw new Exception("USER EXCEPTION - createRow: type not recognized.");
            }

            row += "\t";
        }

        return row;

    }

    private String[] getHeadings(String table) throws Exception {
        String tablePath = currentDatabase + table.toLowerCase() + ".txt";
        File location = new File(tablePath);

        BufferedReader headerReader = null;
        String headings[] = null;

        if (!location.exists()) {
            throw new Exception("getHeadings: Can't find table " + table + ".");
        }

        try {
            headerReader = new BufferedReader(new FileReader(location));
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        try {
            String header = headerReader.readLine();
            if (header != null) {
                headings = header.split("\t");
            } else {
                return null;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return headings;

    }

    private boolean filterRow_equality(String[] values, int key_index, String value) {

            if (values[key_index].equals(value)) {
                return true;
            }

        return false;

    }

    private boolean filterRow_greaterThan(String[] values, int key_index, String value) {

        Float key_float = Float.parseFloat(values[key_index]);
        Float value_float = Float.parseFloat(value);

        if (key_float > value_float) {
            return true;
        }

        return false;

    }


    private String[] updateRow(String[] values, int column_index, String value) {

        values[column_index] = value;

        return values;

    }

}
