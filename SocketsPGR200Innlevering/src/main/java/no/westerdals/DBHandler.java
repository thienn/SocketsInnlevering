package no.westerdals;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.*;

public class DBHandler {
    private static String dbName;
    private static String serverName;
    private static String userName;
    private static String password;

    private Properties properties;


    public DBHandler() {
        try
        {
            properties = new Properties();
            FileInputStream fileInputStream = new FileInputStream("config.properties");
            properties.load(fileInputStream);
            dbName = properties.getProperty("dbName");
            serverName = properties.getProperty("serverName");
            userName = properties.getProperty("userName");
            password = properties.getProperty("password");

            fileInputStream.close();

        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }


    public Connection getConnection() {
        MysqlDataSource ds = new MysqlDataSource();
        ds.setDatabaseName(dbName);
        ds.setServerName(serverName);
        ds.setUser(userName);
        ds.setPassword(password);
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

    public String readTablePrint(ResultSet rs) {
        String message = null;
        try {
            while(rs.next()) {
                String name = rs.getString("name");
                String subjectid = rs.getString("subjectid");
                String lecturer = rs.getString("lecturer");
                String starttime = rs.getString("starttime");
                String endtime = rs.getString("endtime");

                message = "Emnenavn: " + name + " Emnekode: " + subjectid + " Foreleser: " + lecturer + " Startdato: " + starttime + " Sluttdato: " + endtime ;

                System.out.println("Emnenavn: " + name + " Emnekode: " + subjectid + " Foreleser: " + lecturer + " Startdato: " + starttime + " Sluttdato: " + endtime );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println("Print message: " + message);
        return message;
        //System.out.println("Table read - Finished");

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

    public String userInputBasic2(String values) {
        String emnekode = values;
        String message = null;
       /* if(values.equals("All") || values.equals("all")) {
            readTable();
        } else {
        */
            try (Connection con = getConnection()) {
                String subjectid = emnekode;
                PreparedStatement prepStmt = con.prepareStatement("select * from EMNER where subjectid = ?");
                prepStmt.setString(1, subjectid);
                ResultSet rs = prepStmt.executeQuery();

                //
                while(rs.next()) {
                    String name = rs.getString("name");
                    subjectid = rs.getString("subjectid");
                    String lecturer = rs.getString("lecturer");
                    String starttime = rs.getString("starttime");
                    String endtime = rs.getString("endtime");

                    message = "Emnenavn: " + name + " Emnekode: " + subjectid + " Foreleser: " + lecturer + " Startdato: " + starttime + " Sluttdato: " + endtime ;
                    System.out.println("Print Message " + message);

                //    System.out.println("Emnenavn: " + name + " Emnekode: " + subjectid + " Foreleser: " + lecturer + " Startdato: " + starttime + " Sluttdato: " + endtime );
                }
                System.out.println("return 1" + message);
                return message;
            } catch (SQLException e) {
                e.printStackTrace();
            }


               // readTablePrint(rs);
              //  message = readTablePrint(message);
        /*    } catch (SQLException e) {
                e.printStackTrace();
            } */
        System.out.println("return 2" + message);
            return message;
       // }
    }


}
