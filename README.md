# Toy-Database

This GitHub repository is made to store the database project from CS 457: Database Management Systems course at the University of Nevada, Reno. `README.md` is composed of all the Project Assignments documentation that describe the iteration of the toy database.

# Project Assignment 1 Documentation

- Author: Araam Zaremehrjardi
- Assignment: Project Assignment 1 - Metadata Management
- Date: Feb 24, 2022

## Application Instructions

The application has two methods of entering SQL statements for database management. The application can take-in `.sql` file with SQL statements and input them into the application for interpretation through a passed-in arguement. The application must be compiled before use, instructions to compile and run are shown below.

1. `javac src/com/company/*.java`
2. `java -cp ./src com.company.TestDriver PA1_test.sql`

## Database Organization

The design of the databases and tables within the application is based upon using directories to abstract databases while individual text files are used to abstract tables. Each table stores columns with the schema of the table being denoted within the first line of the table. The `\t` escape character is used between columns to denote different columns.

### The Databases Directory

The `/databases` directory is generated within the same directory the application is started. The contents of the directory are subdirectories for each database. An example of the databases directory is shown below.

### Database's Directoy

The `/<database_name>` subdirectory is generated within the `/databases` and is used to be the directory to store a single databases with its tables as text files within the directory. This gives the overall structure of the all databases being `/databases/<database_name>`. There can be no duplicate names of databases within this directory.

### Storing Tables

Each `<table_name>.txt` file is stored within a database directory and contains the columns of a single table within the database. The first line of a table file contains the schema of the table with the following lines being used for actual values. There can be no duplicate names of tables within this directory but duplicate tables can exist among differnt tables. This means if a table is the same within one database and another, as long as two tables of the same name aren't created in the same database directory.

### Example of Database System Structure

A example of the database structure that will be created from `PA1_test.sql` is denoted below.

```
|---/databases                      [MAIN DIRECTORY]
    |---/db_1                       [DATABASE DIRECTORY]
        |---tbl_1.txt               [TABLE]
        |---tbl_2.txt               [TABLE]
    |---/db_2                       [DATABASE DIRECTORY]
        |---tbl_1.txt               [TABLE]
```

## Application Overview

The application contains three main parts being a test driver layer, the database system layer and database abstraction layer. The test driver layer is within the `Main` class and is mainly used to determine which input stream to use when reading lines of SQL statements to be executed by the database system layer. The database system layer is meant to be the abstraction of the database fuctions which is where the execution function for SQL statements is defined. The database abstraction layer is meant to hide file system operations such as creating and deleting of directories and editing of files. This layer is meant to be where primitive operations to the database are meant to be provding barely enough functionality to read and interpet data saved within the file system for database operations. Layering between the three different systems is build through composition by each layer having a instance of the previous layer.

```
*______________________________*
*                              *
| Test Driver Layer            |
| * has instance: Database     |
| System Layer                 |
^______________________________^
^                              ^
| Database System Layer        |
| * has instance: Database     |
| Abstraction Layer            |
^______________________________^
^                              ^
| Database Abstraction Layer   |
|______________________________|
```

### Test Driver Layer

The test driver layer provides the logic to read from either standard input or a file input stream to execute SQL statements. The class `TestDriver` is the Test Driver Layer.

#### Functions

1. **main(args: String[]): void**

   The function is a starting point for the application and checks if a file has been passed into the application, this determines which stream does the application read to input into the Database System's execute function.

### Database System Layer

The database system layer abstracts necessary functions used by the user. These functions are used to configure the overarching database application itself without defining specific file system logic.

1. **execute(command: String): void**

   The function is meant to be the parser for SQL statements entered into the Database System and then executed depedning upon the SQL statement. The function uses the Database Abstraction layer to create/drop databases, create/drop tables, and query tables within the database depending upon the SQL statement. The function tokenizes each keyterm in the SQL statement and uses a series of switch statements to interpet incoming tokens.

### The Database Abstraction Layer

The database abstraction layer to provide an abstraction for primitive functionality of the database. Primitive functionality is direct interaction of the file system to create, delete, and edit files for database functionality such as creating and deleting tables, databases, and directly writing and editing text files.

1. **setCurrentDatabase(database: String): String**

   The function set the current database being pointed to for read and write operations for tables.

2. **createDatabase(database: String): boolean**

   The function creates a new database. A new database is created by creating a new folder within the defined databases' directory

3. **dropDatabase(database: String): boolean**

   The function delete an existing database. A database is deleted if the directory exists with the matching database string passed into the function.

4. **createTable(table: String): boolean**

   The function creates a new text file within a database directory that is used to be a table for the database. A new text file is created with the passed in table value.

5. **dropTable(table: String): boolean**

   The function delete a text file within a database directory that is used to be a table for the database. The deleted text file is identified with the passed in table value.

6. **addColumn(table: String, label: String, type: String): boolean**

   The function creates a new column within a selected table that takes in both the column label and type. The function defines the schema for a table by using a defined expression format of a label and type separated by a space for one column definition.

7. **selectColumn(table: String): String[]**

   The function returns the values of column using the passed in table name. The function reads a text file within the selected database directory and returns a string of the table's schema.

# Project Assignment 2 Documentation

- Author: Araam Zaremehrjardi
- Assignment: Project Assignment 2 - Basic Data Manipulation
- Date: April 1, 2022 (EXTENDED DUE DATE - ORIGINAL MARCH 29, 2022)

## Updates Since Project Assignment 1

The application has been updated to support standard SQL commands such as `insert`, `update`, `delete`, and have more advanced parsing functionality such as reading SQL statements that span multiple lines. 

* Updated Stream Reader to support multiple line statements.
* Support for SQL `insert` statement.
* Support for SQL `update` statement.
* Support for SQL `delete` statement. 
* Support for limited SQL `select` statement.

### Technical Functionalities

Support for the recent updates to the application have major technical methods to satisfying expected behavior. Each new behavior will be described in the following subsections with a  high level overview of the implementations.

#### `insert` Implementation
The `insert` SQL command is implemented by using various functions to transform values to proper form and file system operations to ensure the changes made presist through modifying the denoted table file. A row buffer is created to store values in which each value is given through the form of an array is added into the row buffer. Once given array of values is fully transformed and saved into the row buffer, the row is then appended to the table file.

#### `update` Implementation
The `update` SQL command is implemented by using various functions to read table data, transform rows that meet the update condition, and then the selected table is rebuilt is modified data. A rows buffer is created to store each rows from a table which is used to save modified and unmodified data from the selected table. The headings of the selected table is checked to ensure to find the proper indexes to search for a key value that matches the value is being searched and the index for the selected column in which will be where the current attribute of a record is modified with a new value. The table is then read row by row to searching to see if the update condition is meant in which if the update condition is meant, then the row is then modified with new update data as described in the SQL command. When a row is modified, a count of the number of modified rows is counted up for tracking. For both unmodified and modified data, each row read is saved into the rows buffer. This rows buffer is used when the table is cleared and the new table data is appended in-place of the older table data. 

#### `delete` Implementation
The `delete` SQL command is implemented by using various functions to read table data, save rows that do not meet delete conditions to a buffer, and finally have the buffer replace older table data with new table data. A rows buffer is created to save non-deleted rows during search. The headings of the selected table is checked to ensure to find the proper indexes to search for a key value that matches the value is being searched and the index for the selected column in which a row that does adheres to the delete conditions is not saved to the rows buffer. If a row is deleted, the deleted record count is counted up through the search. As the table is read, row-by-row, rows that do not adhere to the delete condition are saved to the buffer. Once searching is completed, the table is cleared with only the headings remaining in which rows saved from the rows buffers are appended to the cleared table. 

## Application Instructions

The application has two methods of entering SQL statements for database management. The application can take-in `.sql` file with SQL statements and input them into the application for interpretation through a passed-in arguement. The application must be compiled before use, instructions to compile and run are shown below.

1. `javac src/com/company/*.java`
2. `java -cp ./src com.company.TestDriver PA2_test.sql`

## Database Organization

The design of the databases and tables within the application is based upon using directories to abstract databases while individual text files are used to abstract tables. Each table stores columns with the schema of the table being denoted within the first line of the table. The `\t` escape character is used between columns to denote different columns.

### The Databases Directory

The `/databases` directory is generated within the same directory the application is started. The contents of the directory are subdirectories for each database. An example of the databases directory is shown below.

### Database's Directoy

The `/<database_name>` subdirectory is generated within the `/databases` and is used to be the directory to store a single databases with its tables as text files within the directory. This gives the overall structure of the all databases being `/databases/<database_name>`. There can be no duplicate names of databases within this directory.

### Storing Tables

Each `<table_name>.txt` file is stored within a database directory and contains the columns of a single table within the database. The first line of a table file contains the schema of the table with the following lines being used for actual values. There can be no duplicate names of tables within this directory but duplicate tables can exist among differnt tables. This means if a table is the same within one database and another, as long as two tables of the same name aren't created in the same database directory.

### Example of Database System Structure

A example of the database structure that will be created from `PA1_test.sql` is denoted below.

```
|---/databases                      [MAIN DIRECTORY]
    |---/db_1                       [DATABASE DIRECTORY]
        |---tbl_1.txt               [TABLE]
        |---tbl_2.txt               [TABLE]
    |---/db_2                       [DATABASE DIRECTORY]
        |---tbl_1.txt               [TABLE]
```

## Application Overview

The application contains three main parts being a test driver layer, the database system layer and database abstraction layer. The test driver layer is within the `Main` class and is mainly used to determine which input stream to use when reading lines of SQL statements to be executed by the database system layer. The database system layer is meant to be the abstraction of the database fuctions which is where the execution function for SQL statements is defined. The database abstraction layer is meant to hide file system operations such as creating and deleting of directories and editing of files. This layer is meant to be where primitive operations to the database are meant to be provding barely enough functionality to read and interpet data saved within the file system for database operations. Layering between the three different systems is build through composition by each layer having a instance of the previous layer.

```
*______________________________*
*                              *
| Test Driver Layer            |
| * has instance: Database     |
| System Layer                 |
^______________________________^
^                              ^
| Database System Layer        |
| * has instance: Database     |
| Abstraction Layer            |
^______________________________^
^                              ^
| Database Abstraction Layer   |
|______________________________|
```

### Test Driver Layer

The test driver layer provides the logic to read from either standard input or a file input stream to execute SQL statements. The class `TestDriver` is the Test Driver Layer.

#### Functions

1. **main(args: String[]): void**

   The function is a starting point for the application and checks if a file has been passed into the application, this determines which stream does the application read to input into the Database System's execute function.

### Database System Layer

The database system layer abstracts necessary functions used by the user. These functions are used to configure the overarching database application itself without defining specific file system logic.

1. **execute(command: String): void**

   The function is meant to be the parser for SQL statements entered into the Database System and then executed depedning upon the SQL statement. The function uses the Database Abstraction layer to create/drop databases, create/drop tables, and query tables within the database depending upon the SQL statement. The function tokenizes each keyterm in the SQL statement and uses a series of switch statements to interpet incoming tokens.

2. **renderRows(rows: Queue<String[]>): void**

   The function prints the table including a table header and records. The functions print each row of the given rows buffer until the rows buffer is empty. The function as a part of the render process render pipe characters used to differentiate between the metadata of a record.

3. **lexicalAnalysis(string: String): Queue<String>**

   The function is used to parse a given SQL statement and remove any unnecessary characters to leave vital strings that are used as key terms. Vital key terms are terms that denote a commands nouns such as tables, columns, and variables. The function also modifies SQL comamnds to be capitalized ensuring the parser has to only parse capitalized commands rather than both lowercase and capitalized commands.

### The Database Abstraction Layer

The database abstraction layer to provide an abstraction for primitive functionality of the database. Primitive functionality is direct interaction of the file system to create, delete, and edit files for database functionality such as creating and deleting tables, databases, and directly writing and editing text files.

1. **setCurrentDatabase(database: String): String**

   The function set the current database being pointed to for read and write operations for tables.

2. **createDatabase(database: String): boolean**

   The function creates a new database. A new database is created by creating a new folder within the defined databases' directory

3. **dropDatabase(database: String): boolean**

   The function delete an existing database. A database is deleted if the directory exists with the matching database string passed into the function.

4. **createTable(table: String): boolean**

   The function creates a new text file within a database directory that is used to be a table for the database. A new text file is created with the passed in table value.

5. **dropTable(table: String): boolean**

   The function delete a text file within a database directory that is used to be a table for the database. The deleted text file is identified with the passed in table value.

6. **addColumn(table: String, label: String, type: String): boolean**

   The function creates a new column within a selected table that takes in both the column label and type. The function defines the schema for a table by using a defined expression format of a label and type separated by a space for one column definition.

7. **selectColumn(table: String): String[]**

   The function returns the values of column using the passed in table name. The function reads a text file within the selected database directory and returns a string of the table's schema.

8. **selectColumn(table: String, key: String, value: String): Queue<String[]>**

   The function returns the values of a column with constraints given a table. The function uses the File Library to create a point in the file system used to check if the table exists within the database directory If the table does not exist, an exception is thrown. The function uses the parameters to create a constraint based upon the function signature. The constraint denoted is based upon the selection of columns based upon an inequality condition.

9. **deleteRow_greaterThan(table: String, key: String, value: String): int**

    The function deletes a row within a table given a key identifier and key-value. The function identifies the heading of the given table and the index of the key to begin scanning the column for the matched value. During this process of analyzing the column, the function saves every row to the rows buffer and is used to write back the new modified table once searching for rows that match the given constraint are found and skipped. When a row is skipped, this means the constraint has been satisfied and therefore the deleted column count increases in which is to be returned. Once searching is completed the file is cleared with headings of the table appended and each row within the rows buffer appended into the table file.

10. **deleteRow_equality(table: String, key: String, value: String): int**

    The function deletes a row within a table given a key identifier and key-value. The function identifies the heading of the given table and the index of the key to begin scanning the column for the matched value. During this process of analyzing the column, the function saves every row to the rows buffer and is used to write back the new modified table once searching for rows that match the given constraint are found and skipped. When a row is skipped, this means the constraint has been satisfied and therefore the deleted_column count increases in which is to be returned. Once searching is completed the file is cleared with headings of the table appended and each row within the rows buffer appended into the table file.

11. **updateTable_equality(table: String, value: String, selected_column: String, new_value: String)**

    The function updates a row within a table given a key identifier and key-value target and change the same or different set of values given a selected column and new replacement value. The function reads the heading values from the table which are then used to find indexes. Indexes to be found are for the location of the key value and selected column. These indexes are used to then analyze the table to satisfy the equality constraint being given a key-value identifier update the selected column with the replacement value. As the table is searched, the rows are added into a rows buffer used along with the updated rows. The number of rows modified is counted with records modified count, this is returned by the function. The rows buffer is then used to write back into the table by first clearing the table with only the header intact. The rows from the rows buffer are then appended into the table until there are no remaining rows.

12. **appendRow(table: String, values: String[])**

    The function abstracts the File System operations related to adding row data to a table. Using a given table and a set of values, the function reads the table headings to ensure given values abide by the table schema. If so, the values are transformed into a row to be appended into a table. If data appended to the table is successful, then the function returns true otherwise the function has failed and returns false. This is mainly a helper function.

13. **createRow(table: String, values: String[]): String**

    The function abstracts operations related to ensuring raw given data abides to table schema and transform raw data values into a row string. The transformed values are then returned by the function as one string to be appended to a table. This function is responsible for defining the overall structure of a table.

14. **getHeadings(table: String): String[]**

    The function abstracts operations to getting table schema such File System access to the table and reading operations. The function reads the first line of a table to determine its headings and then processes headings into a array for easy access of a single heading.

15. **filterRow_equality(values: String[], key_index: int, value: String): boolean**

    The function abstracts operations to adding constraints to table queries. Given a set of values, the given constraint is checked against a passed index value of the values and a value to be compared to the key index value. This function serves to be a helper function for constraint based queries. If the constraint is successful, the function returns true.

16. **filterRow_greaterThan(values: String[], key_index: int, value: String): boolean**

    The function abstracts operations to adding constraints to table queries. Given a set of values, the given constraint is checked against a passed index value of the values and a value to be compared to the key index value. This function serves to be a helper function for constraint based queries. If the constraint is successful, the function returns true.

17. **updateRow(values: String[], column_index: int, value: String): String[]**

    The function abstracts operations to adding a value in-place of row values. Given a set of values and column_index, the function edits values of an array given the index with the replacement value. This function returns the modified row with the updated value.

# Project Assignment 3 Documentation

- Author: Araam Zaremehrjardi
- Assignment: Project Assignment 3 - Table Joins
- Date: April 19, 2022 

## Updates Since Project Assignment 2

The application has been updated to support sophisticated SQL commands such as advanced `select` statements, `inner join`, `outer left join`, and have more advanced parsing functionality.

* Updated Stream Reader to support no-space variable input.
* Support for advanced SQL `select` statement.
* Support for SQL `inner join` statement.
* Support for SQL `outer left join` statement. 

### Technical Functionalities

Support for the recent updates to the application have major technical methods to satisfying expected behavior. Each new behavior will be described in the following subsections with a high level overview of the implementations.

#### `inner join` Implementation
The `inner join` SQL command is implemented using one function in which supports both `inner join` and `outer left join`. The implementation divides input data in two parts being left hand side table and right hand side table information. This information is stored in the form of `String[]` arrays in which have the following format.
```
     _______________________________________
    | table_variable | table_column | table |
    |________________|______________|_______|

```
The `table_variable` is used to tag a table with a another string as a variable for the table. The `table_column` is used to select which column is selected by each respective table to be compared. The `table` is used identfiy the table itself used for scanning. Each table is then scanned by first accessing the files where both tables are located and using File System operations to read the file and compare to a condition. In the case of inner join operation, the primary and secondary table is compared. The left hand side table is primary table and the right hand side table is the secondary table. The primary table is read with each line being compared to all lines of the secondary table. Scanning stops once each line of the primary table is read. During scanning, the rows where the quality condition of the specified columns are added to a buffer that is returned.
    
#### `outer left join` Implementation
The `outer left join` SQL command is implemented using one function in which supports both `inner join` and `outer left join`. The implementation divides input data in two parts being left hand side table and right hand side table information. This information is stored in the form of `String[]` arrays in which have the following format.
```
     _______________________________________
    | table_variable | table_column | table |
    |________________|______________|_______|

```
The `table_variable` is used to tag a table with a another string as a variable for the table. The `table_column` is used to select which column is selected by each respective table to be compared. The `table` is used identfiy the table itself used for scanning. Each table is then scanned by first accessing the files where both tables are located and using File System operations to read the file and compare to a condition. In the case of inner join operation, the primary and secondary table is compared. The left hand side table is primary table and the right hand side table is the secondary table. The primary table is read line-by-line in which one line of the primary table is compared to all lines of the secondary table. Through the process, if the equality condition is met between the two tables during scanning, the row from the primary table is added to the buffer. During scanning, each time a scan is ran a `added` flag is turned true if a match is detected between the two tables in which the row from the primary table is added. If the flag is not set by the time of equlity-match, then the flag is used to add any row from the primary table thus ensuring all rows from the primary table (lhs table) area added. 
    
#### `insert` Implementation
The `insert` SQL command is implemented by using various functions to transform values to proper form and file system operations to ensure the changes made presist through modifying the denoted table file. A row buffer is created to store values in which each value is given through the form of an array is added into the row buffer. Once given array of values is fully transformed and saved into the row buffer, the row is then appended to the table file.

#### `update` Implementation
The `update` SQL command is implemented by using various functions to read table data, transform rows that meet the update condition, and then the selected table is rebuilt is modified data. A rows buffer is created to store each rows from a table which is used to save modified and unmodified data from the selected table. The headings of the selected table is checked to ensure to find the proper indexes to search for a key value that matches the value is being searched and the index for the selected column in which will be where the current attribute of a record is modified with a new value. The table is then read row by row to searching to see if the update condition is meant in which if the update condition is meant, then the row is then modified with new update data as described in the SQL command. When a row is modified, a count of the number of modified rows is counted up for tracking. For both unmodified and modified data, each row read is saved into the rows buffer. This rows buffer is used when the table is cleared and the new table data is appended in-place of the older table data. 

#### `delete` Implementation
The `delete` SQL command is implemented by using various functions to read table data, save rows that do not meet delete conditions to a buffer, and finally have the buffer replace older table data with new table data. A rows buffer is created to save non-deleted rows during search. The headings of the selected table is checked to ensure to find the proper indexes to search for a key value that matches the value is being searched and the index for the selected column in which a row that does adheres to the delete conditions is not saved to the rows buffer. If a row is deleted, the deleted record count is counted up through the search. As the table is read, row-by-row, rows that do not adhere to the delete condition are saved to the buffer. Once searching is completed, the table is cleared with only the headings remaining in which rows saved from the rows buffers are appended to the cleared table. 

## Application Instructions

The application has two methods of entering SQL statements for database management. The application can take-in `.sql` file with SQL statements and input them into the application for interpretation through a passed-in arguement. The application must be compiled before use, instructions to compile and run are shown below.

1. `javac src/com/company/*.java`
2. `java -cp ./src com.company.TestDriver PA2_test.sql`

## Database Organization

The design of the databases and tables within the application is based upon using directories to abstract databases while individual text files are used to abstract tables. Each table stores columns with the schema of the table being denoted within the first line of the table. The `\t` escape character is used between columns to denote different columns.

### The Databases Directory

The `/databases` directory is generated within the same directory the application is started. The contents of the directory are subdirectories for each database. An example of the databases directory is shown below.

### Database's Directoy

The `/<database_name>` subdirectory is generated within the `/databases` and is used to be the directory to store a single databases with its tables as text files within the directory. This gives the overall structure of the all databases being `/databases/<database_name>`. There can be no duplicate names of databases within this directory.

### Storing Tables

Each `<table_name>.txt` file is stored within a database directory and contains the columns of a single table within the database. The first line of a table file contains the schema of the table with the following lines being used for actual values. There can be no duplicate names of tables within this directory but duplicate tables can exist among differnt tables. This means if a table is the same within one database and another, as long as two tables of the same name aren't created in the same database directory.

### Example of Database System Structure

A example of the database structure that will be created from `PA1_test.sql` is denoted below.

```
|---/databases                      [MAIN DIRECTORY]
    |---/db_1                       [DATABASE DIRECTORY]
        |---tbl_1.txt               [TABLE]
        |---tbl_2.txt               [TABLE]
    |---/db_2                       [DATABASE DIRECTORY]
        |---tbl_1.txt               [TABLE]
```

## Application Overview

The application contains three main parts being a test driver layer, the database system layer and database abstraction layer. The test driver layer is within the `Main` class and is mainly used to determine which input stream to use when reading lines of SQL statements to be executed by the database system layer. The database system layer is meant to be the abstraction of the database fuctions which is where the execution function for SQL statements is defined. The database abstraction layer is meant to hide file system operations such as creating and deleting of directories and editing of files. This layer is meant to be where primitive operations to the database are meant to be provding barely enough functionality to read and interpet data saved within the file system for database operations. Layering between the three different systems is build through composition by each layer having a instance of the previous layer.

```
*______________________________*
*                              *
| Test Driver Layer            |
| * has instance: Database     |
| System Layer                 |
^______________________________^
^                              ^
| Database System Layer        |
| * has instance: Database     |
| Abstraction Layer            |
^______________________________^
^                              ^
| Database Abstraction Layer   |
|______________________________|
```

### Test Driver Layer

The test driver layer provides the logic to read from either standard input or a file input stream to execute SQL statements. The class `TestDriver` is the Test Driver Layer.

#### Functions

1. **main(args: String[]): void**

   The function is a starting point for the application and checks if a file has been passed into the application, this determines which stream does the application read to input into the Database System's execute function.

### Database System Layer

The database system layer abstracts necessary functions used by the user. These functions are used to configure the overarching database application itself without defining specific file system logic.

1. **execute(command: String): void**

   The function is meant to be the parser for SQL statements entered into the Database System and then executed depedning upon the SQL statement. The function uses the Database Abstraction layer to create/drop databases, create/drop tables, and query tables within the database depending upon the SQL statement. The function tokenizes each keyterm in the SQL statement and uses a series of switch statements to interpet incoming tokens.

2. **renderRows(rows: Queue<String[]>): void**

   The function prints the table including a table header and records. The functions print each row of the given rows buffer until the rows buffer is empty. The function as a part of the render process render pipe characters used to differentiate between the metadata of a record.

3. **lexicalAnalysis(string: String): Queue<String>**

   The function is used to parse a given SQL statement and remove any unnecessary characters to leave vital strings that are used as key terms. Vital key terms are terms that denote a commands nouns such as tables, columns, and variables. The function also modifies SQL comamnds to be capitalized ensuring the parser has to only parse capitalized commands rather than both lowercase and capitalized commands.

### The Database Abstraction Layer

The database abstraction layer to provide an abstraction for primitive functionality of the database. Primitive functionality is direct interaction of the file system to create, delete, and edit files for database functionality such as creating and deleting tables, databases, and directly writing and editing text files.

1. **setCurrentDatabase(database: String): String**

   The function set the current database being pointed to for read and write operations for tables.

2. **createDatabase(database: String): boolean**

   The function creates a new database. A new database is created by creating a new folder within the defined databases' directory

3. **dropDatabase(database: String): boolean**

   The function delete an existing database. A database is deleted if the directory exists with the matching database string passed into the function.

4. **createTable(table: String): boolean**

   The function creates a new text file within a database directory that is used to be a table for the database. A new text file is created with the passed in table value.

5. **dropTable(table: String): boolean**

   The function delete a text file within a database directory that is used to be a table for the database. The deleted text file is identified with the passed in table value.

6. **addColumn(table: String, label: String, type: String): boolean**

   The function creates a new column within a selected table that takes in both the column label and type. The function defines the schema for a table by using a defined expression format of a label and type separated by a space for one column definition.

7. **selectColumn(table: String): String[]**

   The function returns the values of column using the passed in table name. The function reads a text file within the selected database directory and returns a string of the table's schema.

8. **selectColumn(table: String, key: String, value: String): Queue<String[]>**

   The function returns the values of a column with constraints given a table. The function uses the File Library to create a point in the file system used to check if the table exists within the database directory If the table does not exist, an exception is thrown. The function uses the parameters to create a constraint based upon the function signature. The constraint denoted is based upon the selection of columns based upon an inequality condition.

9. **deleteRow_greaterThan(table: String, key: String, value: String): int**

    The function deletes a row within a table given a key identifier and key-value. The function identifies the heading of the given table and the index of the key to begin scanning the column for the matched value. During this process of analyzing the column, the function saves every row to the rows buffer and is used to write back the new modified table once searching for rows that match the given constraint are found and skipped. When a row is skipped, this means the constraint has been satisfied and therefore the deleted column count increases in which is to be returned. Once searching is completed the file is cleared with headings of the table appended and each row within the rows buffer appended into the table file.

10. **deleteRow_equality(table: String, key: String, value: String): int**

    The function deletes a row within a table given a key identifier and key-value. The function identifies the heading of the given table and the index of the key to begin scanning the column for the matched value. During this process of analyzing the column, the function saves every row to the rows buffer and is used to write back the new modified table once searching for rows that match the given constraint are found and skipped. When a row is skipped, this means the constraint has been satisfied and therefore the deleted_column count increases in which is to be returned. Once searching is completed the file is cleared with headings of the table appended and each row within the rows buffer appended into the table file.

11. **updateTable_equality(table: String, value: String, selected_column: String, new_value: String)**

    The function updates a row within a table given a key identifier and key-value target and change the same or different set of values given a selected column and new replacement value. The function reads the heading values from the table which are then used to find indexes. Indexes to be found are for the location of the key value and selected column. These indexes are used to then analyze the table to satisfy the equality constraint being given a key-value identifier update the selected column with the replacement value. As the table is searched, the rows are added into a rows buffer used along with the updated rows. The number of rows modified is counted with records modified count, this is returned by the function. The rows buffer is then used to write back into the table by first clearing the table with only the header intact. The rows from the rows buffer are then appended into the table until there are no remaining rows.

12. **appendRow(table: String, values: String[])**

    The function abstracts the File System operations related to adding row data to a table. Using a given table and a set of values, the function reads the table headings to ensure given values abide by the table schema. If so, the values are transformed into a row to be appended into a table. If data appended to the table is successful, then the function returns true otherwise the function has failed and returns false. This is mainly a helper function.

13. **createRow(table: String, values: String[]): String**

    The function abstracts operations related to ensuring raw given data abides to table schema and transform raw data values into a row string. The transformed values are then returned by the function as one string to be appended to a table. This function is responsible for defining the overall structure of a table.

14. **getHeadings(table: String): String[]**

    The function abstracts operations to getting table schema such File System access to the table and reading operations. The function reads the first line of a table to determine its headings and then processes headings into a array for easy access of a single heading.

15. **filterRow_equality(values: String[], key_index: int, value: String): boolean**

    The function abstracts operations to adding constraints to table queries. Given a set of values, the given constraint is checked against a passed index value of the values and a value to be compared to the key index value. This function serves to be a helper function for constraint based queries. If the constraint is successful, the function returns true.

16. **filterRow_greaterThan(values: String[], key_index: int, value: String): boolean**

    The function abstracts operations to adding constraints to table queries. Given a set of values, the given constraint is checked against a passed index value of the values and a value to be compared to the key index value. This function serves to be a helper function for constraint based queries. If the constraint is successful, the function returns true.

17. **updateRow(values: String[], column_index: int, value: String): String[]**

    The function abstracts operations to adding a value in-place of row values. Given a set of values and column_index, the function edits values of an array given the index with the replacement value. This function returns the modified row with the updated value.

18. **selectColumn(left_hand_side_table: String[], right_hand_side_table: String[])**
    
    The function abstracts selection of rows within a table given a set of variables of a lhs table and rhs table. The function then use given parameters of each respective side being table name, variable symbol, and column name. The function uses the given information to generate a table with that matches a equality condition of the selected column from each side. When the equality condition is not met, the row being scanned between the two files is ignored and not added. Scanning between the files occurs by first accessing the files in the File System with the lhs table driving the scanning. Each row of the lhs table is compared to all rows of the rhs table during scanning. Once complete, both files are closed with the rows buffer returned for printing. 
    
19. **selectColumn(left_hand_side_table: String[], right_hand_side_table: String[], join: String)**
    
    The function abstracts selection of rows within a table given a set of variables of lhs table and rhs table. This version of the function provides join options being either "inner join" or "left outer join."  The function uses the given information to generate a table with that matches a  equality condition of the selected column from each side. The equality condition being different based upon the given join option. When the equality condition is not met, the row being scanned between the two files is ignored and not added. Scanning between the files occurs by first accessing the files in the File System with the lhs table driving the scanning. Each row of the lhs table is compared to all rows of the rhs table during scanning. Once complete, both files are closed with the rows buffer returned for printing. 
   
