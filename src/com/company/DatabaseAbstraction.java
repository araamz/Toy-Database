package com.company;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/*
    Author: Araam Zaremehrjardi
    Date Created: April 21, 2022
    Date Edited: May 6, 2022
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
    3. transactionEnable: boolean
       Purpose: The variable transactionEnable is used to define the enabling of transaction mode
       in which data is not directly wrote into the database until it is committed. Behavior of the
       Database Abstraction layer changes based upon the value of the bit.
    4. errorOccurance: boolean
       Purpose: the varaible errorOccurance is used to define if a error has occured in which is
       used to rollback changes in the database. This changes the commit behavior of the
       Database Abstraction layer in which does not persist changes to the database when in
       transaction mode.
    - Functions:
    1. setCurrentDatabase(database: String): String
    2. createDatabase(database: String): boolean
    3. dropDatabase(database: String): boolean
    4. createTable(table: String): boolean
    5. dropTable(table: String): boolean
    6. addColumn(table: String, label: String, type: String): boolean
    7. selectColumn(table: String): String[]
    8. selectColumn(table: String, key: String, value: String): Queue<String[]>
    9. selectColumn(left_hand_side_table: String[], right_hand_side_table: String[])
    10. selectColumn(left_hand_side_table: String[], right_hand_side_table: String[], join: String)
    11. deleteRow_greaterThan(table: String, key: String, value: String): int
    12. deleteRow_equality(table: String, key: String, value: String): int
    13. updateTable_equality(table: String, value: String, selected_column: String, new_value: String): int
    14. appendRow(table: String, values: String[]): boolean
    15. createRow(table: String, values: String[]): String
    16. getHeadings(table: String): String[]
    17. filterRow_equality(values: String[], key_index: int, value: String): boolean
    18. filterRow_greaterThan(values: String[], key_index: int, value: String): boolean
    19. updateRow(values: String[], column_index: int, value: String): String[]
    20. beginTransaction(): boolean
    21. commitTransaction(): boolean
    22. create_cacheTable(table: String): boolean
    23. lockTable(table: String): boolean
    24. unlockTable(table: String): boolean
    25. tableLocked(table: String): boolean
    26. persist_cacheTable(table: String): boolean
*/
public class DatabaseAbstraction {

  private static final String databasesDirectory = "databases/";
  private String currentDatabase = null;
  private boolean transactionEnable = false;
  private boolean errorOccurance = false;

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
  Function: selectColumn
  Purpose: The purpose of selectColumn() is to select rows within a table given a set of variables of a lhs 
  table and rhs table. The functions then use given parameters of each respective side being table name, variable 
  symbol, and column name. The function uses the given information to generate a table with that matches a 
  equality condition of the selected column from each side. When the equality condition is not met, the row
  being scanned between the two files is ignored and not added. Scanning between the files occurs by first 
  accessing the files in the File System with the lhs table driving the scanning. Each row of the lhs table is compared
  to all rows of the rhs table during scanning. Once complete, both files are closed with the rows buffer 
  returned for printing. 
  - Parameters:
  1. left_hand_side_table: String[]
  2. right_hand_side_table: String[]
  - Return Type: Queue<String[]>
  */
  public Queue<String[]> selectColumn(String[] left_hand_side_table, String[] right_hand_side_table) {
	  String lhs_tablePath = currentDatabase + left_hand_side_table[0].toLowerCase() + ".txt";
	  String rhs_tablePath = currentDatabase + right_hand_side_table[0].toLowerCase() + ".txt";
	  File lhs_location = new File(lhs_tablePath);
	  File rhs_location = new File(rhs_tablePath);
	  BufferedReader lhs_reader = null;
	  BufferedReader rhs_reader = null;
	  String[] headings = null;
	  Integer lhs_keyIndex = null;
	  Integer rhs_keyIndex = null;
	  Queue<String[]> rows = new LinkedList<String[]>();
	  
	  try {
		  lhs_reader = new BufferedReader(new FileReader(lhs_location));
		  rhs_reader = new BufferedReader(new FileReader(rhs_location));
		  
	  } catch (Exception exception) {
	      exception.printStackTrace();
	  }
	  
	  try {
		  
		  String heading = "";
		  
		  String[] lhs_tableHeadings = getHeadings(left_hand_side_table[0]);
		  String[] rhs_tableHeadings = getHeadings(right_hand_side_table[0]);
		  
		  for (int column_index = 0; column_index < lhs_tableHeadings.length; column_index++) {
			  heading += lhs_tableHeadings[column_index] + "\t";
		  }
		  for (int column_index = 0; column_index < rhs_tableHeadings.length; column_index++) {
			  heading += rhs_tableHeadings[column_index] + "\t";
		  }
		  
		    for (int headingIndex = 0; headingIndex < lhs_tableHeadings.length; headingIndex++) {
		        String heading_value = lhs_tableHeadings[headingIndex].split(" ")[0];
		        if (heading_value.matches(left_hand_side_table[2])) {
		          lhs_keyIndex = headingIndex;
		        }
		      }
		    
		    for (int headingIndex = 0; headingIndex < rhs_tableHeadings.length; headingIndex++) {
		        String heading_value = rhs_tableHeadings[headingIndex].split(" ")[0];
		        if (heading_value.matches(right_hand_side_table[2])) {
		          rhs_keyIndex = headingIndex;
		        }
		      }

		    headings = heading.split("\t");
		    rows.add(headings);
		    
	} catch (Exception e) {
		e.printStackTrace();
	}
	  
	  try {
		String lhs_cursor = "";
		for (int lhs_lineIndex = 0; lhs_cursor != null; lhs_lineIndex++) {
			
			lhs_cursor = lhs_reader.readLine();
			 
			if ((lhs_lineIndex != 0) && (lhs_cursor != null) ) {
				
				rhs_reader = new BufferedReader(new FileReader(rhs_location));
				String rhs_cursor = "";
				for (int rhs_lineIndex = 0; rhs_cursor != null; rhs_lineIndex++) {
					rhs_cursor = rhs_reader.readLine();
					if ((rhs_lineIndex != 0) && (rhs_cursor != null) ) {
					
						String[] lhs_dataRow = lhs_cursor.split("\t");
						String[] rhs_dataRow = rhs_cursor.split("\t");
					
						if (lhs_dataRow[lhs_keyIndex].equals(rhs_dataRow[rhs_keyIndex])) {
							
							String[] row = (lhs_cursor + rhs_cursor).split("\t");
							rows.add(row);

						}
					
					}
					
				}
				
			}
			  
		  }
		lhs_reader.close();
		rhs_reader.close();
	} catch (IOException e) {

		e.printStackTrace();
	}
	  
	  return rows;
	  
  }
  
  /*
  Function: selectColumn
  Purpose: The purpose of selectColumn() is to select rows within a table given a set of variables of lhs
  table and rhs table. This version of selectColumn() provides join options being either "inner join" or
  "left outer join."  The function uses the given information to generate a table with that matches a 
  equality condition of the selected column from each side. The equality condition being different based 
  upon the given join option. When the equality condition is not met, the row being scanned between the two 
  files is ignored and not added. Scanning between the files occurs by first accessing the files in the File 
  System with the lhs table driving the scanning. Each row of the lhs table is compared to all rows of the 
  rhs table during scanning. Once complete, both files are closed with the rows buffer returned for printing. 
  - Parameters:
  1. left_hand_side_table: String[]
  2. right_hand_side_table: String[]
  - Return Type: Queue<String[]>
  */  
  public Queue<String[]> selectColumn(String[] left_hand_side_table, String[] right_hand_side_table, String join) {
	  String lhs_tablePath = currentDatabase + left_hand_side_table[0].toLowerCase() + ".txt";
	  String rhs_tablePath = currentDatabase + right_hand_side_table[0].toLowerCase() + ".txt";
	  File lhs_location = new File(lhs_tablePath);
	  File rhs_location = new File(rhs_tablePath);
	  BufferedReader lhs_reader = null;
	  BufferedReader rhs_reader = null;
	  String[] headings = null;
	  Integer lhs_keyIndex = null;
	  Integer rhs_keyIndex = null;
	  Queue<String[]> rows = new LinkedList<String[]>();
	  
	  try {
		  lhs_reader = new BufferedReader(new FileReader(lhs_location));
		  rhs_reader = new BufferedReader(new FileReader(rhs_location));
		  
	  } catch (Exception exception) {
	      exception.printStackTrace();
	  }
	  
	  try {
		  
		  String heading = "";
		  
		  String[] lhs_tableHeadings = getHeadings(left_hand_side_table[0]);
		  String[] rhs_tableHeadings = getHeadings(right_hand_side_table[0]);
		  
		  for (int column_index = 0; column_index < lhs_tableHeadings.length; column_index++) {
			  heading += lhs_tableHeadings[column_index] + "\t";
		  }
		  for (int column_index = 0; column_index < rhs_tableHeadings.length; column_index++) {
			  heading += rhs_tableHeadings[column_index] + "\t";
		  }
		  
		    for (int headingIndex = 0; headingIndex < lhs_tableHeadings.length; headingIndex++) {
		        String heading_value = lhs_tableHeadings[headingIndex].split(" ")[0];
		        if (heading_value.matches(left_hand_side_table[2])) {
		          lhs_keyIndex = headingIndex;
		        }
		      }
		    
		    for (int headingIndex = 0; headingIndex < rhs_tableHeadings.length; headingIndex++) {
		        String heading_value = rhs_tableHeadings[headingIndex].split(" ")[0];
		        if (heading_value.matches(right_hand_side_table[2])) {
		          rhs_keyIndex = headingIndex;
		        }
		      }

		    headings = heading.split("\t");
		    rows.add(headings);
		    
	} catch (Exception e) {
		e.printStackTrace();
	}
	  
	  if (join.equals("inner join")) {
		  
		  try {
			String lhs_cursor = "";
			for (int lhs_lineIndex = 0; lhs_cursor != null; lhs_lineIndex++) {
				
				lhs_cursor = lhs_reader.readLine();
				 
				if ((lhs_lineIndex != 0) && (lhs_cursor != null) ) {
					
					rhs_reader = new BufferedReader(new FileReader(rhs_location));
					String rhs_cursor = "";
					for (int rhs_lineIndex = 0; rhs_cursor != null; rhs_lineIndex++) {
						rhs_cursor = rhs_reader.readLine();
						if ((rhs_lineIndex != 0) && (rhs_cursor != null) ) {
						
							String[] lhs_dataRow = lhs_cursor.split("\t");
							String[] rhs_dataRow = rhs_cursor.split("\t");
						
							if (lhs_dataRow[lhs_keyIndex].equals(rhs_dataRow[rhs_keyIndex])) {
								
								String[] row = (lhs_cursor + rhs_cursor).split("\t");
								rows.add(row);

							}
						
						}
						
					}
					
				}
				  
			  }

		} catch (IOException e) {

			e.printStackTrace();
		}
		  
	  } else if (join.equals("left outer join")) {
		  
		  try {
			String lhs_cursor = "";
			for (int lhs_lineIndex = 0; lhs_cursor != null; lhs_lineIndex++) {
				
				boolean added_flag = false;
				
				lhs_cursor = lhs_reader.readLine();
				 
				if ((lhs_lineIndex != 0) && (lhs_cursor != null) ) {
					
					rhs_reader = new BufferedReader(new FileReader(rhs_location));
					String rhs_cursor = "";
					for (int rhs_lineIndex = 0; rhs_cursor != null; rhs_lineIndex++) {
						rhs_cursor = rhs_reader.readLine();
						if ((rhs_lineIndex != 0) && (rhs_cursor != null) ) {
						
							String[] lhs_dataRow = lhs_cursor.split("\t");
							String[] rhs_dataRow = rhs_cursor.split("\t");
						
							if (lhs_dataRow[lhs_keyIndex].equals(rhs_dataRow[rhs_keyIndex])) {
								
								String[] row = (lhs_cursor + rhs_cursor).split("\t");
								rows.add(row);
								added_flag = true;

							} 
						
						}
						
					}
					
					if (added_flag == false) {
						String[] dataRow = new String[headings.length];
						String[] lineData = lhs_cursor.split("\t");
						for (int columnIndex = 0; columnIndex < dataRow.length; columnIndex++) {
							
							if (columnIndex < lineData.length) {
								
								dataRow[columnIndex] = lineData[columnIndex];
								
							} else {
								
								dataRow[columnIndex] = "";
								
							}
							
						}
						 rows.add(dataRow);
					}
					
				}
				
				
				  
			}

		} catch (IOException e) {

			e.printStackTrace();
		}
		  
	  }
		try {
			lhs_reader.close();
			rhs_reader.close();
		} catch (IOException e) {

			e.printStackTrace();
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

    if (transactionEnable) {
      create_cacheTable(table);
      tablePath = currentDatabase + table.toLowerCase() + "_cache.txt";
    }

    if (transactionEnable && tableLocked(table)) {
      errorOccurance = true;
      return -1;
    } else if (transactionEnable && !tableLocked(table)) {
      lockTable(table);
    }

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
        if (transactionEnable) {
          if (!appendRow(table + "_cache", columns)) {
            throw new Exception();
          }
        } else {
          if (!appendRow(table, columns)) {
            throw new Exception();
          }
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

  /*
  Function: beginTransaction
  Purpose: The purpose of beginTransaction() is for operations of the Database Abstraction layer to
  change based upon the change of a mode bit being "transaction_enable." If enabled, the function
  modifies targeted files in the file system to allow for locking of table files and to allow for
  changes of files to not be persisted in the disk until changes are to be comiited by the
  transaction.
  - Parameters:
  */
  public boolean beginTransaction() {

    transactionEnable = true;
    return true;

  }

  /*
  Function: commitTransaction
  Purpose: The purpose of commitTransaction() is for operations of the Database Abstraction layer to
  fully transfer changes to the database from persisted cache table files to the database table
  files and to unlock tables for other processes. The function fully implements atomic functionality
  of changes made to database tables and readings the application to end a transaction enabled
  state. The application firstly checks to ensure a error has not occurred between the start and end
  of the transaction and to ensure a transaction is active. If one or both these checks fails, the
  transaction is aborted and changes are not persisted. If not aborted, the application reads cache
  files from the database yet to be committed saving table names associated to the cache files.
  Using the helper functions, the application then persists each cache file to its respective table,
  and finally for clean up operations removes any locks on tables allowing other processes to write
  to the tables.
  - Parameters:
  */
  public boolean commitTransaction() {

    if (errorOccurance || !transactionEnable) {
      return false;
    }
    String databasesPath = currentDatabase;
    File location = new File(databasesPath);
    String[] directory_files_names = location.list();
    ArrayList<String> cache_table_names = new ArrayList<>();

    for (int file_index = 0; file_index < location.list().length; file_index++) {

      if (directory_files_names[file_index].contains("_cache.txt")) {

        cache_table_names.add(directory_files_names[file_index].split("_cache.txt")[0]);

      }

    }

    for (int table_index = 0; table_index < cache_table_names.size(); table_index++) {
      if (!persist_cacheTable(cache_table_names.get(table_index))) {
        return false;
      };
    }

    for (int table_index = 0; table_index < cache_table_names.size(); table_index++) {
      if (!unlockTable(cache_table_names.get(table_index))) {
        return false;
      };
    }

    transactionEnable = false;

    return true;

  }

  /*
  Function: create_cacheTable
  Purpose: The purpose of create_cacheTable() is for operations in creating a cacheTable during
  database write operations when in transaction mode. When the application has transaction mode
  enabled, changes made to the database are not persisted but rather saved in cache files that are
  used in time coming for committing changes. This function abstracts the needed code for creating
  and ensuring the existence of a cache table file. If successful, the function returns a true
  value denoting a cacheTable was created otherwise returns false. Failures encountered derive from
  the file system being either the cacheTable is already created or another error has occurred.
  - Parameters:
  1. table: String
  */
  private boolean create_cacheTable(String table) {

    String cachePath = currentDatabase + table.toLowerCase() + "_cache.txt";
    File cacheLocation = new File(cachePath);

    if (cacheLocation.exists()) {
      return false;
    } else {

      String tablePath = currentDatabase + table.toLowerCase() + ".txt";
      File tableLocation = new File(tablePath);

      try {
        Files.copy(tableLocation.toPath(), cacheLocation.toPath());
      } catch (IOException exception) {
        exception.printStackTrace();
        return false;
      }

    }

    return true;

  }

  /*
  Function: lockTable
  Purpose: The purpose of lockTable() is to abstract operations required for locking a table in
  which guarantees no other process can access the table unless the lock is removed. The lock for
  a table is established through a file in which takes a table name and then adds "_lock" to denote
  to other processes the application is currently using the table. When the table is locked, the
  function fails and return false, otherwise the function creates a lock file and is successful.
  - Parameters:
  1. table: String
  */
  private boolean lockTable(String table) {

    if (tableLocked(table)) {
      return false;
    }

    String tablePath = currentDatabase + table.toLowerCase() + "_lock";
    File location = new File(tablePath);

    try {
      location.createNewFile();
      return true;
    } catch (IOException exception) {
      exception.printStackTrace();
      return false;
    }

  }

  /*
  Function: unlockTable
  Purpose: The purpose of unlockTable() is to abstract operations required for unlocking a table in
  which guarantees other process can access the table. The function removes the lock file given a
  table name. Assuming the lock exists upon a table file, the file is removed and thus the function
  returns true for a table being unlocked. If the functions returns false, it denotes nothing was
  unlocked due to the table either not existing or the lock for the table not existing hence the
  table was never locked in the first place.
  - Parameters:
  1. table: String
  */
  private boolean unlockTable(String table) {

    if (!tableLocked(table)) {
      return false;
    }

    String tablePath = currentDatabase + table.toLowerCase() + "_lock";
    File location = new File(tablePath);

    return location.delete();

  }

  /*
  Function: tableLocked
  Purpose: The purpose of tableLocked() is to abstract operations required for checking to see if a
  table has a lock on the table file. The function is used as a helper function in which abstract
  file operations for checking if the lock file for a table exists. If the table lock file exists,
  the function returns true otherwise if the lock does not exist and thus the table is unlocked then
  it returns false.
  - Parameters:
  1. table: String
  */
  private boolean tableLocked(String table) {

    String tablePath = currentDatabase + table.toLowerCase() + "_lock";

    File location = new File(tablePath);

    if (location.exists()) {

      return true;

    }

    return false;

  }

  /*
  Function: persist_cacheTable
  Purpose: The purpose of persist_cacheTable() is to abstract operations required for swapping
  a cache table file with a existing table file. This replacement is meant to persist data saved
  from a running transaction and is meant to be a helper function that aids in the commit of
  changes during a transaction. The function opens two file paths being for the table file and the
  cache table file in which it checks for the existence of both, otherwise the function fails and
  return false. Deleting the table file, the function uses the Files API to create and copy
  the cache table file to a new table file. Once completed, the function deletes the cache table
  file and returns true for a successful data persist response.
  - Parameters:
  1. table: Strings
  */
  private boolean persist_cacheTable(String table) {

    String cachePath = currentDatabase + table.toLowerCase() + "_cache.txt";
    File cacheLocation = new File(cachePath);

    String tablePath = currentDatabase + table.toLowerCase() + ".txt";
    File tableLocation = new File(tablePath);

    if (!tableLocation.exists()) {

      return false;
    }

    if (!cacheLocation.exists()) {
      return false;
    }

    tableLocation.delete();

    try {
      Files.copy(cacheLocation.toPath(), tableLocation.toPath());
    } catch (IOException exception) {
      exception.printStackTrace();
      return false;
    }

    cacheLocation.delete();

    return true;

  }

}