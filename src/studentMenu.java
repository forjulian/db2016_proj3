
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.Scanner;

import com.tmax.tibero.jdbc.*;
import com.tmax.tibero.jdbc.ext.*;


/*written by ¿±¡¯¡÷ */

public class studentMenu {
	public static void studentReport(int stuID) throws SQLException, ClassNotFoundException{
		Class.forName(Main.JDBC_DRIVER);
		Connection conn=DriverManager.getConnection(Main.DATABASE_URL,Main.USERNAME,Main.PASSWORD);
		
		PreparedStatement pstmt1 = conn.prepareStatement("SELECT name, dept_name, tot_cred"+" FROM student "+"Where ID=?");
		pstmt1.setInt(1,stuID);
		ResultSet rs=pstmt1.executeQuery();
		
		while(rs.next()){
			System.out.println("Welcome "+rs.getString(1));
			System.out.println("You are a member of "+rs.getString(2));
			System.out.println("You have taken total "+rs.getInt(3)+" credits");
		}
		
		System.out.println();
		System.out.println("Semester report");
		
		PreparedStatement pstmt2=conn.prepareStatement("SELECT year, semester, course_id, title, dept_name, credits, grade "+"From takes natural join course "+"Where takes.ID=? "
														+"order by year desc,(case semester when 'Spring' then 4 when 'Summer' then 3 when 'Fall' then 2 when 'Winter' then 1 else 5 end)");
		pstmt2.setInt(1,stuID);
		ResultSet rs2=pstmt2.executeQuery();
		
		PreparedStatement pstmt3=conn.prepareStatement("select year, semester, count(course_id), "+"sum((case grade "
																				 +"when 'A+' then 4.3 when 'A' then 4.0 when 'A-' then 3.7 "
																				 +"when 'B+' then 3.3 when 'B' then 3.0 when 'B-' then 2.7 "
																				 +"when 'C+' then 2.3 when 'C' then 2.0 when 'C-' then 1.7 "
																				 +"when 'D+' then 1.3 when 'D' then 1.0 when 'D-' then 0.7 "
																				 +"when 'F' then 0 else NULL end)*credits)"
																				 +" / sum(credits) "
													   +"from takes natural join course "
													   +"where takes.ID=? "
													   +"group by year, semester "
													   +"order by year desc,(case semester when 'Spring' then 4 when 'Summer' then 3 when 'Fall' then 2 when 'Winter' then 1 else 5 end)");
		pstmt3.setInt(1,stuID);
		ResultSet rs3=pstmt3.executeQuery();
		
		while(rs3.next()){
			boolean f;
			int y=rs3.getInt(1);
			String sem=rs3.getString(2);
			if(rs3.getString(4)==null)
				System.out.println("\n"+y+" "+sem+" GPA : "+null);
			else
				System.out.println("\n"+y+" "+sem+" GPA : "+rs3.getFloat(4));
			System.out.println("course_id		title		dept_name		credits		grade");
			for(int i=0;i<rs3.getInt(3);i++){
				if(rs2.next()){
					System.out.println(rs2.getString(3)+"	"+rs2.getString(4)+"	"+rs2.getString(5)+"	"+rs2.getInt(6)+"	"+rs2.getString(7));
				}
			}
		}
		rs3.close();
		rs2.close();
		rs.close();
		pstmt1.close();
		pstmt2.close();
		pstmt3.close();
		conn.close();
	}
	
	public static void viewTimeTable(int stuID) throws ClassNotFoundException, SQLException{
		int i=1,flag=0, year;
		String seme;
		Scanner sc = new Scanner(System.in);
		Class.forName(Main.JDBC_DRIVER);
		Connection conn=DriverManager.getConnection(Main.DATABASE_URL,Main.USERNAME,Main.PASSWORD);
		
		System.out.println("Please select semester to view");
		System.out.println();
		
		PreparedStatement pstmt1 = conn.prepareStatement("select distinct year, semester "
				+"from takes natural join section "
				+"where takes.ID=? "
				+"order by year desc, (case semester when 'Spring' then 4 when 'Summer' then 3 "
										+"when 'Fall' then 2 when 'Winter' then 1 else 5 end)");
		pstmt1.setInt(1,stuID);
		ResultSet rs1=pstmt1.executeQuery();
		
		while(rs1.next()){
			System.out.println((i++)+") "+rs1.getInt(1)+"   "+rs1.getString(2));
		}
		System.out.print(">>");
		
		flag = sc.nextInt();
		
		if(flag<i){
		
			rs1.close();
			rs1=pstmt1.executeQuery();
			
			System.out.println("course_id	title	day		start_time	end_time");
			
			for(int j=0;j<flag;j++)
				rs1.next();
			year=rs1.getInt(1);
			seme=rs1.getString(2);														//
			
			Statement stmt1=conn.createStatement();
			ResultSet rs2=stmt1.executeQuery("select course_id, time_slot_id "
					+"from takes natural join section "
					+"where (takes.ID,year)=("+stuID+", "+year+") and semester="+"'"+seme+"'");
			
			Statement stmt2=conn.createStatement();
			Statement stmt3=conn.createStatement();
			
			String course, tsid,title;
			
			ResultSet rs3, rs4;
			while(rs2.next()){
				course=rs2.getString(1);
				tsid=rs2.getString(2);
				rs3=stmt2.executeQuery("select title from course where course_id = "+"'"+course+"'");
				rs4=stmt3.executeQuery("select day, start_hr,start_min,end_hr, end_min from time_slot where time_slot_id = "+"'"+tsid+"'");
				rs3.next();
				title=rs3.getString(1);
				while(rs4.next()){
					System.out.println(course+"	"+title+"	"+rs4.getString(1)+"	"+rs4.getInt(2)+":"+rs4.getInt(3)+"		"+rs4.getInt(4)+" : "+rs4.getInt(5));
				}	
				rs3.close();
				rs4.close();
			}
		}
	}
}
