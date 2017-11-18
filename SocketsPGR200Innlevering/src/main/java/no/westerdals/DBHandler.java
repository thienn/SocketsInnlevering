package no.westerdals;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.*;

/**
 * Class that connects to a database through JBDC.
 * Methods that can be called upon.
 *
 * References for structure on connection taken from
 * https://www.youtube.com/watch?v=BOUMR85B-V0 series
 *
 * References about properties
 * https://www.mkyong.com/java/java-properties-file-examples/
 * https://www.youtube.com/watch?v=-LrmzNDPVx8
 *
 * @author Thien Cong Pham
 */

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

    /**
     * Reads from file "Supports only CSV then insert into a DB accordingly as long as it has a next line
     * @throws IOException
     */
    void readFile() throws IOException {
        try (Connection con = getConnection()){
            PreparedStatement stmt = con.prepareStatement("INSERT INTO EMNER VALUES(?,?,?,?,?)");

            //Open a file input stream for CSV - Towards a specific file
            BufferedReader CSVreader = new BufferedReader(new FileReader("Emner.csv"));

            String lineRead;
            Scanner scanner;

            CSVreader.readLine(); //ignores the first line

            // Reading file line by line and uploads the information to DB
            try {
                while ((lineRead = CSVreader.readLine()) != null) {
                    scanner = new Scanner(lineRead);
                    // separator for next in CSV file (can change depending on file-type)
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
                            e.printStackTrace();
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

    /**
     *  Method for accepting request from Server then use that request to look up in Database through query
     *  Afterwards turns that into a String that get returned to server
     *
     * @param values get values for subjectID from Server
     * @return String message to Server
     */
    public String clientInput(String values) {
        String subjectid = values;
        String message = null;

            try (Connection con = getConnection()) {
                PreparedStatement prepStmt = con.prepareStatement("select * from EMNER where subjectid = ?");
                prepStmt.setString(1, subjectid);
                ResultSet rs = prepStmt.executeQuery();

                while(rs.next()) {
                    String name = rs.getString("name");
                    subjectid = rs.getString("subjectid");
                    String lecturer = rs.getString("lecturer");
                    String starttime = rs.getString("starttime");
                    String endtime = rs.getString("endtime");

                    message = "Emnenavn: " + name + " Emnekode: " + subjectid + " Foreleser: " + lecturer + " Startdato: " + starttime + " Sluttdato: " + endtime ;
                }
                return message;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        return message;
    }
}
