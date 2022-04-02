package com.company;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

/*
    Author: Araam Zaremehrjardi
    Date Created: March 13, 2022
    Date Edited: April 1, 2022
    Class: TestDriver
    Purpose: The purpose of TestDriver is to be a starting point for the application by opening a file or standard input
    stream for reading SQL statements. The TestDriver continuously reads input from either stream until the application
    is stopped.
    - Variables:
    - Functions:
    1. main(args: String[]): void
*/
public class TestDriver {

  /*
  Function: main
  Purpose: The purpose of main() is to be main entry point for the application by initializing an instance of the
  Database System and initializing a reader stream to read input. The function checks if an argument for a file is
  present in which is used to open a file reader stream otherwise standard input stream is opened to take input.
  Each SQL statement is continuously read until it meets the break condition in the while-loop in which case the
  application stops.
  - Parameters:
  1. database: String
  - Return Type: boolean
   */
  public static void main(String[] args) throws IOException {
    DatabaseSystem database = new DatabaseSystem();
    BufferedReader reader = null;
    // NOTE: Used to check if a file argument is present otherwise standard input is used.
    if (args.length == 1) {
      reader = new BufferedReader(new FileReader(args[0]));
    } else {
      reader = new BufferedReader(new InputStreamReader(System.in));
      System.out.println("=== DATABASE SHELL ===");
    }
    for (String command; (command = reader.readLine()) != null; ) {
      if (command.contains("--")) {
        command = "";
      }
      while (!command.contains(";")) {
        command += reader.readLine();
        if (command.equals(".exit") || command.equals(".EXIT")) {
          break;
        }
      }
      if (command.equals(".exit") || command.equals(".EXIT")) {
        break;
      }
      database.execute(command);
    }
    System.out.println("All Done.");
  }
}
