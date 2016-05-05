import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.Scanner;

import com.tmax.tibero.jdbc.*;
import com.tmax.tibero.jdbc.ext.*;


/*written by 윤진주 */

public class studentMenu {
	
	public static void studentReport(int stuID) throws SQLException, ClassNotFoundException{
		
		Class.forName(Main.JDBC_DRIVER);
		Connection conn=DriverManager.getConnection(Main.DATABASE_URL,Main.USERNAME,Main.PASSWORD);
		
		
		/*student(ID, name, dept_name, tot_credit)이 전부 들어있고, ID는  student의 super key 이므로  올바른 studentID이 주워지면 나머지는 정보들을 찾을 수 있다.*/
		
		PreparedStatement pstmt1 = conn.prepareStatement("SELECT name, dept_name, tot_cred"+" FROM student "+"Where ID=?");
		pstmt1.setInt(1,stuID);
		ResultSet rs=pstmt1.executeQuery();
		
		/* rs(name, dept_name, tot_credit) 으로 구성되어져 있고, get***함수를 이용해  rs의 정보를 가져온다.*/
		
		while(rs.next()){
			System.out.println("Welcome "+rs.getString(1));
			System.out.println("You are a member of "+rs.getString(2));
			System.out.println("You have taken total "+rs.getInt(3)+" credits");
		}
		
		System.out.println();
		System.out.println("Semester report");
		
		/*student가 들은 과목을 조사하기 위해,
		 takes.Id=studentID일 경우 takes 와  course_id 를  natural join 해서 (year, semester, course_id ,title, dept_name, credits, grade)로 이루어진 result set을 만든다.
		 단, order by 구문과 case 구문을 을 활용하여 (year, semester)순으로 정렬한다. */
		
		PreparedStatement pstmt2=conn.prepareStatement("SELECT year, semester, course_id, title, dept_name, credits, grade "+"From takes natural join course "+"Where takes.ID=? "
														+"order by year desc,(case semester when 'Spring' then 4 when 'Summer' then 3 when 'Fall' then 2 when 'Winter' then 1 else 5 end)");
		pstmt2.setInt(1,stuID);
		ResultSet rs2=pstmt2.executeQuery();
		
		/*student의 학기당 gpa와 학기당 들은 과목수를 count하는 result set을 구하는 query
		 group by 구문을 활용하여 (year, semester)로 묶어주고 case 구문을 활용하여  A,B,C로 주어진 grade를 숫자로 바꾼다 
		 count(course_id) : 학기당 들은 과목 수 
		 count(grade) : 학기당 들은 과목 수 에서 성적 null인 과목을 제외한 수 
		 */
		
		PreparedStatement pstmt3=conn.prepareStatement("select year, semester, count(course_id),count(grade), "+"sum((case grade "
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
		
		/*rs3에 있는 (year, semester) 별 gpa와 rs2에 정렬된 (year, semester) 순으로 정렬된 출력해야 하는 정보들을 출력 */
		while(rs3.next()){
			int y=rs3.getInt(1);
			String sem=rs3.getString(2);
			if(rs3.getInt(3)!=rs3.getInt(4))				// count(course_id) 와 count(grade)가 다르면  과목 성적 중 null 값이 존재 한다는 이야기									
				System.out.println("\n"+y+" "+sem+" GPA : ");
			else
				System.out.println("\n"+y+" "+sem+" GPA : "+rs3.getFloat(5));
			System.out.println("course_id		title		dept_name		credits		grade");
			/*rs3의 세번째 열에는 (year, semester)에  들은 과목의 수가 있다.
			 그  수 만큼  rs2에서 과목을 출력해준다
			(rs2와 rs3가 같은 table로 부터 추출된 sql이고 동일한 조건으로 정렬 했으므로  rs2와 rs3의 year와 semester는 비교안해도 된다.(정확성을 위해서는 하는게 좋다.))*/
			for(int i=0;i<rs3.getInt(3);i++){						
				if(rs2.next()){
					if(rs2.getString(7)==null)
						System.out.println(rs2.getString(3)+"	"+rs2.getString(4)+"	"+rs2.getString(5)+"	"+rs2.getInt(6)+"	");
					else
						System.out.println(rs2.getString(3)+"	"+rs2.getString(4)+"	"+rs2.getString(5)+"	"+rs2.getInt(6)+"	"+rs2.getString(7));
				}
			}
		}
		System.out.println();
		
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
		
		/*stuid를 가지고 takes에서 (year, semester) distinct하게 추출 */
		PreparedStatement pstmt1 = conn.prepareStatement("select distinct year, semester "
				+"from takes "
				+"where takes.ID=? "
				+"order by year desc, (case semester when 'Spring' then 4 when 'Summer' then 3 "
										+"when 'Fall' then 2 when 'Winter' then 1 else 5 end)");
		pstmt1.setInt(1,stuID);
		ResultSet rs1=pstmt1.executeQuery();
		/*추출한 (year,semester)을 출력*/
		while(rs1.next()){
			System.out.println((i++)+") "+rs1.getInt(1)+"   "+rs1.getString(2));
		}
		System.out.print(">> ");
		
		flag = sc.nextInt();
		
		if(flag<i){ 			// 주어진 보기 번호가 아닌 다른 번호를 고를 경우 처리, 원래라면 다양하게 해줘야 하는데 그냥 더 큰 번호를 입력했을 때만 처리
		
			/*Result set rs1의 커서를 다시 첫 row로 가르키는 것이 안되서 새롭게 result set을 만들음*/
			rs1.close();
			rs1=pstmt1.executeQuery();
			
			System.out.println("course_id	title	day		start_time	end_time");
			
			/*result set 의 임의의 row를 가르키는 기능이 안되므로 사용자가 선택한 row까지 rs의 커서 이동 */
			for(int j=0;j<flag;j++)
				rs1.next();
			
			year=rs1.getInt(1);
			seme=rs1.getString(2);														//
			/*(year,semester)가 주어졌을 때 (course_id, title, day, start_time, end_time)의 정보를 출력하고 싶은 데
			(course_id, title) 은 course에서 course_id가 super key 이므로 course_id만 알면 추출가능
			(day, start_time, end_time)도 time_slot 에서 time_slot_id가 super key이므로 추출가능
			그래서 takes와 section을 natural join하여 course_id와 time_slot_id를 추출*/
			PreparedStatement pstmt2=conn.prepareStatement("select course_id, time_slot_id "
					+"from takes natural join section "
					+"where (takes.ID,year,semester)=(?,?,?)");
			pstmt2.setInt(1,stuID);
			pstmt2.setInt(2,year);
			pstmt2.setString(3,seme);
			
			ResultSet rs2=pstmt2.executeQuery();
			
		
			PreparedStatement pstmt3=conn.prepareStatement("select title from course where course_id = ?");
			PreparedStatement pstmt4=conn.prepareStatement("select day, start_hr,start_min,end_hr, end_min "
															+ "from time_slot where time_slot_id = ?");
			String course, tsid,title;
			
			ResultSet rs3, rs4;
			while(rs2.next()){
				course=rs2.getString(1);
				tsid=rs2.getString(2);
				/*course_id를 이용해 course_id와 title 추출*/
				
				pstmt3.setString(1,course);
				pstmt4.setString(1,tsid);
				rs3=pstmt3.executeQuery();
				rs4=pstmt4.executeQuery();
				
				/*time slot id를 이용해 (day, start_time, end_time) 추출*/
				rs3.next();
				title=rs3.getString(1);
				while(rs4.next()){
					System.out.println(course+"	"+title+"	"+rs4.getString(1)+"	"+rs4.getInt(2)+" : "+rs4.getInt(3)+"		"+rs4.getInt(4)+" : "+rs4.getInt(5));
				}	
				rs3.close();
				rs4.close();
			}
			rs2.close();
			pstmt2.close();
			pstmt3.close();
			pstmt4.close();
		}
		System.out.println();
		
		rs1.close();
		pstmt1.close();
		conn.close();
	}
}