package com.company;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

/*
    Author: Araam Zaremehrjardi
    Date Created: April 4, 2022
    Date Edited: April 5, 2022
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
    8. selectColumn(table: String, key: String, value: String): Queue<String[]>
    9. deleteRow_greaterThan(table: String, key: String, value: String): int
    10. deleteRow_equality(table: String, key: String, value: String): int
    11. updateTable_equality(table: String, value: String, selected_column: String, new_value: String): int
    12. appendRow(table: String, values: String[]): boolean
    13. createRow(table: String, values: String[]): String
    14. getHeadings(table: String): String[]
    15. filterRow_equality(values: String[], key_index: int, value: String): boolean
    16. filterRow_greaterThan(values: String[], key_index: int, value: String): boolean
    17. updateRow(values: String[], column_index: int, value: String): String[]
*/
public class DatabaseAbstraction {

  private static final String databasesDirectory = "databases/";
  private String currentDatabase = null;

  public DatabaseAbstraction() {
  }

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
    String[] headings = null;
    FileWriter tableWriter = null;
    if (!location.exists()) {
      return false;
    }
    try {
      headings = getHeadings(table);
    } catch (Exception exception) {
      exception.printStackTrace();
    }
    if (headings != null) {
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

  /*
  Function: selectColumn
  Purpose: The purpose of selectColumn() is to return the values of a column with constraints given
  a table. The function uses the File Library to create a point in the file system used to check if
  the table exists within the database directory. If the table does not exist, an exception is
  thrown. The function uses the parameters to create a constraint based upon the function signature.
  The constraint denoted is based upon the selection of columns based upon an inequality condition.
  The behavior of selectColumn() changes based upon the function signature using polymorphic methods
  to achieve different implementations of selectColumn().
  - Parameters:
  1. table: String
  2. key: String
  3. value: String
  - Return Type: Queue<String[]>
   */
  public Queue<String[]> selectColumn(String table, String key, String value,
      LinkedList<String> selected_columns) throws Exception {
    String tablePath = currentDatabase + table.toLowerCase() + ".txt";
    File location = new File(tablePath);
    BufferedReader tableReader = null;
    Queue<String[]> rows = new LinkedList<>();
    String[] headings = null;
    Integer keyIndex = null;
    Integer[] selectedColumns_indexes = new Integer[selected_columns.size()];
    int selectedColumnsIndexes_index = 0;
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

  /*
  Function: deleteRow_greaterThan
  Purpose: The purpose of deleteRow_greaterThan() is to delete a row within a table given a key
  identifier and key-value. The function identifies the heading of the given table and the index
  of the key to begin scanning the column for the matched value. During this process of analyzing
  the column, the function saves every row to the rows buffer and is used to write back the new modified table
  once searching for rows that match the given constraint are found and skipped. When a row
  is skipped, this means the constraint has been satisfied and therefore the deleted_column count
  increases in which is to be returned. Once searching is completed the file is cleared with
  headings of the table appended and each row within the rows buffer appended into the table file.
  - Parameters:
  1. table: String
  2. key: String
  3. value: String
  - Return Type: int
  */
  public int deleteRow_greaterThan(String table, String key, String value) {
    String tablePath = currentDatabase + table.toLowerCase() + ".txt";
    File location = new File(tablePath);
    BufferedReader tableReader = null;
    String[] headings = null;
    Integer keyIndex = null;
    Integer selectedColumn_index = null;
    int records_deleted = 0;
    int header_skip = 0;
    Queue<String[]> rows = new LinkedList<>();
    BufferedWriter tableWriter = null;
    try {
      tableReader = new BufferedReader(new FileReader(location));
    } catch (Exception exception) {
      exception.printStackTrace();
    }
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
    // NOTE: Remove already saved headings. This allows rows to be strictly row data.
    rows.remove();
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

  /*
  Function: deleteRow_equality
  Purpose: The purpose of deleteRow_equality() is to delete a row within a table given a key
  identifier and key-value. The function identifies the heading of the given table and the index
  of the key to begin scanning the column for the matched value. During this process of analyzing
  the column, the function saves every row to the rows buffer and is used to write back the new modified table
  once searching for rows that match the given constraint are found and skipped. When a row
  is skipped, this means the constraint has been satisfied and therefore the deleted_column count
  increases in which is to be returned. Once searching is completed the file is cleared with
  headings of the table appended and each row within the rows buffer appended into the table file.
  - Parameters:
  1. table: String
  2. key: String
  3. value: String
  - Return Type: int
  */
  public int deleteRow_equality(String table, String key, String value) {
    String tablePath = currentDatabase + table.toLowerCase() + ".txt";
    File location = new File(tablePath);
    BufferedReader tableReader = null;
    String[] headings = null;
    Integer keyIndex = null;
    Integer selectedColumn_index = null;
    Queue<String[]> rows = new LinkedList<>();
    BufferedWriter tableWriter = null;
    int records_deleted = 0;
    try {
      tableReader = new BufferedReader(new FileReader(location));
    } catch (Exception exception) {
      exception.printStackTrace();
    }
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
    // NOTE: Remove already saved headings. This allows rows to be strictly row data.
    rows.remove();
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

  /*
  Function: updateTable_equality
  Purpose: The purpose of deleteRow_equality() is to update a row within a table given a key
  identifier and key-value target and change the same or different set of values given a selected
  column and new replacement value. The function reads the heading values from the table which are
  then used to find indexes. Indexes to be found are for the location of the key value and
  selected column. These indexes are used to then analyze the table to satisfy the equality
  constraint being given a key-value identifier update the selected column with the replacement
  value. As the table is searched, the rows are added into a rows buffer used along with the updated
  rows. The number of rows modified is counted with records modified count, this is returned by the
  function. The rows buffer is then used to write back into the table by first clearing the table with
  only the header intact. The rows from the rows buffer are then appended into the table until
  there are no remaining rows.
  - Parameters:
  1. table: String
  2. key: String
  3. value: String
  - Return Type: int
  */
  public int updateTable_equality(String table, String key, String value, String selected_column,
      String new_value) {
    String tablePath = currentDatabase + table.toLowerCase() + ".txt";
    File location = new File(tablePath);
    BufferedReader tableReader = null;
    String[] headings = null;
    Integer keyIndex = null;
    Integer selectedColumn_index = null;
    Queue<String[]> rows = new LinkedList<>();
    int records_modified = 0;
    BufferedWriter tableWriter = null;
    try {
      tableReader = new BufferedReader(new FileReader(location));
    } catch (Exception exception) {
      exception.printStackTrace();
    }
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

    // NOTE: Remove already saved headings. This allows rows to be strictly row data.
    rows.remove();
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

  /*
  Function: appendRow
  Purpose: The purpose of appendRow() is to abstract File System operations related to adding row
  data to a table. Using a given table and a set of values, the function reads the table headings
  to ensure given values abide by the table schema. If so, the values are transformed into a row
  to be appended into a table. If data appended to the table is successful, then the function
  returns true otherwise the function has failed and returns false. This is mainly a helper
  function.
  - Parameters:
  1. table: String
  2. values: String[]
  - Return Type: boolean
  */
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

  /*
  Function: createRow
  Purpose: The purpose of createRow() is to abstract operations related to ensuring raw given data
  abides to table schema and transform raw data values into a row string. The transformed values
  are then returned by the function as one string to be appended to a table. This function is
  responsible for defining the overall structure of a table.
  - Parameters:
  1. table: String
  2. values: String[]
  - Return Type: boolean
  */
  private String createRow(String table, String[] values) throws Exception {
    String[] types = null;
    String row = "";
    try {
      types = getHeadings(table);
      for (int typesIndex = 0; typesIndex < types.length; typesIndex++) {
        types[typesIndex] = types[typesIndex].split(" ")[1];
      }
    } catch (Exception exception) {
      exception.printStackTrace();
    }
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
        String processed_type = type.replace("(", " ").replace(")", "");
        String[] tokens = processed_type.split(" ");
        Integer varchar_length_constraint = Integer.parseInt(tokens[1]);
        if (value.length() > varchar_length_constraint) {
          throw new Exception("USER EXCEPTION - createRow: varchar(" + varchar_length_constraint
              + ") is not satisfied.");
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
    String[] headings = null;
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

  /*
  Function: filterRow_equality
  Purpose: The purpose of filterRow_equality() is to abstract operations to adding constraints to
  table queries. Given a set of values, the given constraint is checked against a passed index value
  of the values and a value to be compared to the key index value. This function serves to be a
  helper function for constraint based queries. If the constraint is successful, the function
  returns true.
  - Parameters:
  1. values: String[]
  2. key_index: int
  3. value: String
  - Return Type: boolean
  */
  private boolean filterRow_equality(String[] values, int key_index, String value) {
    return values[key_index].equals(value);
  }

  /*
  Function: filterRow_greaterThan
  Purpose: The purpose of filterRow_equality() is to abstract operations to adding constraints to
  table queries. Given a set of values, the given constraint is checked against a passed index value
  of the values and a value to be compared to the key index value. This function serves to be a
  helper function for constraint based queries. If the constraint is successful, the function
  returns true.
  - Parameters:
  1. values: String[]
  2. key_index: int
  3. value: String
  - Return Type: boolean
  */
  private boolean filterRow_greaterThan(String[] values, int key_index, String value) {
    Float key_float = Float.parseFloat(values[key_index]);
    Float value_float = Float.parseFloat(value);
    return key_float > value_float;
  }

  /*
  Function: updateRow
  Purpose: The purpose of updateRow() is to abstract operations to adding a value in-place of row
  values. Given a set of values and column_index, the function edits values of an array given the
  index with the replacement value. This function returns the modified row with the updated value.
  - Parameters:
  1. values: String[]
  2. column_index: int
  3. value: String
  - Return Type: String[]
  */
  private String[] updateRow(String[] values, int column_index, String value) {
    values[column_index] = value;
    return values;
  }
}
