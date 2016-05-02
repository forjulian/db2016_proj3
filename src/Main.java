
import java.sql.*;
import com.tmax.tibero.jdbc.ext.TbDataSource;

public class Main {

    public static final String JDBC_DRIVER = "com.tmax.tibero.jdbc.TbDriver";
    public static final String HOST = "127.0.0.1";
    public static final String PORT = "8629";
    public static final String DATABASE_URL = "jdbc:tibero:thin:@" + HOST + ":" + PORT + ":tibero";

    //USERNAME is the username to connect database
    public static final String USERNAME = "sys";
    //PASSWORD is the password to connect database
    public static final String PASSWORD = "tibero";

    static Connection connection;

    public static void main(String[] args) throws Exception{
        Welcome welcome = new Welcome();

        TbDataSource dataSource = new TbDataSource();
        try{
            dataSource.setUser(USERNAME);
            dataSource.setPassword(PASSWORD);
            dataSource.setURL(DATABASE_URL);
            connection = dataSource.getConnection();

            System.out.println("Welcome\n");
            welcome.readInput();

        }catch(SQLException e){
            e.printStackTrace();
        }

    }
}
