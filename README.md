# SQL to MongoQL Translator

## Project Description

This year's project focuses on the development of a tool for user interaction with MongoDB using SQL queries. The tool will allow users to write SQL queries, which will be validated, parsed, and translated into the appropriate format (MongoQL). The translated query will be executed in the MongoDB, and the resulting collection of documents will be organized into a structure suitable for tabular representation.

## Prerequisites

1. Install NoSQLBooster or a similar MongoDB GUI client.
2. Connect to the database using the credentials provided in the Readme.md of each team on the GitHub classroom.

Example Credentials:
- IP: 134.209.239.154
- Database: bp_tim_1
- Username: writer
- Password: 1234

3. Import individual JSON files for the HR database from the provided materials to have a MongoDB representation of the database.

## GUI

The project will utilize Swing to support user interaction with the database, similar to what is implemented in the Live SQL tool. It will provide a workspace where the user can write code (in a TextArea) and a separate panel to display the results of executed queries (JTable).

## Parser

The parser's role is to take a SQL query in string form and convert it into an object. It's necessary to define a separate class that represents the query. This representation should not be a simple string but an abstraction that captures the query structure. One idea is to create an SQLQuery class that has a list of Clause elements. Each Clause can have keywords and associated parameters. For example, the query "SELECT last_name from departments" consists of two Clause parts: Select and From, with "last_name" and "departments" as parameters. Each type of Clause can have its own class due to different parameterization methods.

## Validator

The created query is passed to the validator, whose role is to check the basic rules of SQL syntax to ensure successful translation. The rules include:
- The query must have all mandatory parts.
- Everything selected and not under an aggregation function must go into the GROUP BY clause.
- The WHERE clause cannot contain aggregation functions.
- Table joins must have a join condition (USING or ON).

If the validator detects an error (Scenario 1), query execution is halted, and a meaningful error message is shown to the user, suggesting how to fix it. If the query is valid, it is forwarded to the adapter for translation.

## Adapter

Valid queries are passed to the adapter for translation. The result of the Adapter component is a MongoQuery object, representing the MongoDB query. The adapter has two phases:

1. Parameter Converter:
   Extract variable parts from the SQLQuery object. For example, if the SELECT Clause contains "select first_name, last_name," the Parameter Converter should prepare parameters for MongoDB (SELECT maps to PROJECT, so it will be "{last_name:1, first_name:1, _id:0}").

2. Mapper:
   The Mapper should take the converted parameters and prepare the MongoQuery. You can use predefined templates for MongoDB queries with placeholders for parameters that the Mapper will populate.

## Documentation

The tool should support the following SQL query features:
- Projection
- Sorting
- Filtering
- Logical operators
- Text and numerical comparisons
- $in operator
- Joining two or more tables
- Subqueries (both scalar and multi-valued) in the WHERE clause
- Aggregation functions and grouping

## Executor and Packager

The Executor component accepts the MongoQuery and executes it on the HR database in the MongoDB server. The result of execution is an unstructured collection of documents. The Packager component then translates this unstructured MongoDB form into a structured dataset suitable for display in a JTable.

## Implementation

- The project should use the MongoDB client and Java Swing libraries.
- Utilize the MVC and Adapter design patterns.
- Separate components and define interfaces for each component's functionality.
- Do not use additional libraries for database access, code generation, validation, etc.
- Do not execute native SQL queries in MongoDB.
- Pay special attention to error handling; the program should not crash or throw errors.

Please note that functionalities that print generated queries and result sets to the console without updating the graphical user interface will not be considered successfully implemented.

**Total Points: -6** (Maximum score: 14)
