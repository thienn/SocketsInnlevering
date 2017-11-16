package no.westerdals;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class DBHandler {


    public Connection getConnection() {
        MysqlDataSource ds = new MysqlDataSource();
        ds.setDatabaseName("myDB");
        ds.setServerName("localhost");
        ds.setUser("root");
        ds.setPassword("gamer1234");
        Connection con = null;
        try {
            con = ds.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return con;
    }

    void createTable() {
        try (Connection con = getConnection()) {
            String q = "CREATE TABLE EMNER(" + "name varchar(50),"
                    + "subjectid varchar(10), "
                    + "lecturer varchar(50),"
                    + "starttime varchar(20),"
                    + "endtime varchar(20)"
                    + ");";
            Statement stmt = con.createStatement();

            stmt = con.createStatement();
            stmt.execute(q);
            System.out.println("Successfully created table");
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    void dropTable() {
        try (Connection con = getConnection()){
            Statement stmt = con.createStatement();
            String sql = "DROP TABLE EMNER";
            stmt.executeUpdate(sql);
            stmt.close();
            System.out.println("Dropped Table");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    void readTable() {
        try (Connection con = getConnection()){
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM EMNER");
            rs = stmt.executeQuery("SELECT * FROM EMNER");
            readTablePrint(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    void readTablePrint(ResultSet rs) {
        try {
            while(rs.next()) {
                String name = rs.getString("name");
                String subjectid = rs.getString("subjectid");
                String lecturer = rs.getString("lecturer");
                String starttime = rs.getString("starttime");
                String endtime = rs.getString("endtime");

                System.out.println("Emnenavn: " + name + " Emnekode: " + subjectid + " Foreleser: " + lecturer + " Startdato: " + starttime + " Sluttdato: " + endtime );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Table read - Finished");

    }

    // Reference for importing of CSV - https://coderanch.com/t/572623/databases/insert-CSV-values-file-MySQL
    //Read from CSV File and input to DB
    void readFile() throws IOException {
        try (Connection con = getConnection()){
            PreparedStatement stmt = con.prepareStatement("INSERT INTO EMNER VALUES(?,?,?,?,?)");

            //Open a file input stream for CSV - Towards a specific file
            BufferedReader CSVreader = new BufferedReader(new FileReader("Emner.csv"));

            String lineRead; //line read from csv
            Scanner scanner;

            CSVreader.readLine(); //ignores the first line

            // Reading file line by line and uploads the information to DB
            try {
                while ((lineRead = CSVreader.readLine()) != null) {
                    scanner = new Scanner(lineRead);
                    scanner.useDelimiter(";");
                    while (scanner.hasNext()) {
                        try {
                            stmt.setString(1, scanner.next());
                            stmt.setString(2, scanner.next());
                            stmt.setString(3, scanner.next());
                            stmt.setString(4, scanner.next());
                            stmt.setString(5, scanner.next());
                            stmt.executeUpdate();
                        } catch (NoSuchElementException e) {

                        }
                    }

                    System.out.println("Subject added to DB" );

                }
                stmt.close();
                CSVreader.close();

            } catch (SQLException e) {
                e.printStackTrace();
            } finally {

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    void userInput() {
        int values;
        int check;
        Scanner input = new Scanner(System.in);
        System.out.println("If you want all of the subjects out type 1, if you want a specific one type 2: ");
        try (Connection con = getConnection()){
            values = input.nextInt();
            if(values == 1) {
                readTable();
            } else if (values == 2) {
                System.out.println("Which subject do you want - (Use subjectid)? ");
                String emnekode = input.next();
                try {
                    // Next make sure this goes into a loop, if invalid, report back to user, then ask for another value
                    String subjectid = emnekode;
                    PreparedStatement prepStmt = con.prepareStatement("select * from EMNER where subjectid = ?");
                    prepStmt.setString(1, subjectid);
                    ResultSet rs = prepStmt.executeQuery();
                    readTablePrint(rs);
                    System.out.println("Want to check again? 1 for yes, 2 for exit");
                    check = input.nextInt();
                    if(check == 1) {
                        userInput();
                    } else if (check == 2) {
                        System.exit(0);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (InputMismatchException e) {
            System.out.println("Input invalid - need to be 1 or 2");
            userInput();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    void userInputBasic(String values) {
        if(values.equals("1")) {
            try (Connection con = getConnection()){
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM EMNER");
                rs = stmt.executeQuery("SELECT * FROM EMNER");
                readTablePrint(rs);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Nothing");
        }

    }


}
