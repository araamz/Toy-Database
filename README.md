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

• Design document that clarifies the followings: (5 points)
o How your program organizes multiple databases
o How your program manages multiple tables
o At a high level, how you implement those required functionalities

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
