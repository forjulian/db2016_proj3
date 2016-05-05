
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;

import com.tmax.tibero.jdbc.data.binder.StringBinder;
import com.tmax.tibero.jdbc.ext.TbDataSource;


public class Welcome {
    static Connection connection;
    TbDataSource dataSource = new TbDataSource();

    private int id;
    private String name;

    public void readInput() throws Exception {
        try {
            dataSource.setUser(Main.USERNAME);
            dataSource.setPassword(Main.PASSWORD);
            dataSource.setURL(Main.DATABASE_URL);

            connection = dataSource.getConnection();
            Scanner keyboard = new Scanner(System.in);
            System.out.println("Please sign in");
            System.out.print("ID      : ");
            id = keyboard.nextInt();
            System.out.print("Name    : ");
            name = keyboard.next();
            System.out.println();

            if(isStudent(id, name) == 1) //Case when student is authenticated
            {
                studentMenu(new Student(id, name));
            } else if(isInstructor(id, name) == 1) {  //Case when instructor is authenticated
                instructorMenu(new Instructor(id, name));
            } else {
                System.out.println("Wrong authentication.");
                readInput();
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int isStudent(int stud_id, String stud_name){
        int student = 0;
        try {
            dataSource.setUser(Main.USERNAME);
            dataSource.setPassword(Main.PASSWORD);
            dataSource.setURL(Main.DATABASE_URL);

            connection = dataSource.getConnection();

        PreparedStatement pStmt = connection.prepareStatement("SELECT id, name FROM student WHERE id = ?");

        pStmt.setInt(1, stud_id);
            ResultSet result = pStmt.executeQuery();

            while(result.next()){
                String name = result.getString("NAME");

                //Comparing if the input is equal with the one stored in database
                if(name.equals(stud_name))
                    student = 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return student;
    }

    public int isInstructor(int inst_id, String inst_name){
        int student = 0;
        try {
            dataSource.setUser(Main.USERNAME);
            dataSource.setPassword(Main.PASSWORD);
            dataSource.setURL(Main.DATABASE_URL);

            connection = dataSource.getConnection();

            PreparedStatement pStmt = connection.prepareStatement("SELECT id, name FROM instructor WHERE id = ?");

            pStmt.setInt(1, inst_id);
            ResultSet result = pStmt.executeQuery();

            while(result.next()){
                String name = result.getString("NAME");

                //Comparing if the input is equal with the one stored in database
                if(name.equals(inst_name))
                    student = 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return student;
    }

    static void studentMenu(Student student) throws Exception{
    	BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		while (true) {
			System.out.println("Please select student menu");
	        System.out.println("1) Student report");
	        System.out.println("2) View time table");
	        System.out.println("0) Exit");
	        System.out.print(">> ");
	        
			String input = br.readLine();

			if (input.compareTo("1") == 0) {
				studentMenu.studentReport(student.getId());
			} else if (input.compareTo("2") == 0) {
				studentMenu.viewTimeTable(student.getId());
			} else if (input.compareTo("0") == 0) {
				break;
			} else {
				return;
			}
		}
    }

    static void instructorMenu(Instructor instructor) throws Exception{
    	BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		while (true) {
			System.out.println("Please select instructor menu");
	        System.out.println("1) Course report");
	        System.out.println("2) Advisee report");
	        System.out.println("0) Exit");
	        System.out.print(">> ");
	        
			String input = br.readLine();

			if (input.compareTo("1") == 0) {
				InstructorMenu.courseReport(instructor.getId());
			} else if (input.compareTo("2") == 0) {
				InstructorMenu.adviseeReport(instructor.getId());
			} else if (input.compareTo("0") == 0) {
				break;
			} else {
				return;
			}
		}
    }
}