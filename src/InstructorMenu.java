
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.*;

import com.tmax.tibero.jdbc.*;
import com.tmax.tibero.jdbc.ext.*;

/* Written by Jeong Hyunho */

public class InstructorMenu {
	/**
	 * adviseeReport : the 7th feature of Project3. Print out basic information of advisees.
	 * @param instID : ID of instructor in integer form (5-digit number in this project)
	 */
	public static void adviseeReport(int instID) throws Exception {
		/* Load and establish connections to database */
		Class.forName(Main.JDBC_DRIVER);
		Connection conn = DriverManager.getConnection(Main.DATABASE_URL, Main.USERNAME, Main.PASSWORD);

		/* Manipulate SQL statements for searching advisee information */
		PreparedStatement pstmt = conn.prepareStatement(
				"SELECT S.ID, S.name, S.dept_name, S.tot_cred" + " FROM advisor A"
						+ " JOIN student S ON (S.ID = A.s_id)" + " WHERE A.i_id = ?"); // Gets information of advisees from (instructor JOIN advisor JOIN student)
																						
		pstmt.setString(1, String.valueOf(instID)); // Inserts the ID of instructor to SQL query

		/* Get the results */
		ResultSet rs = pstmt.executeQuery();
		ResultSetMetaData rsmd = rs.getMetaData(); // Gets metadata of the result for printing a table

		/* Print a formatted table */
		for (int i = 1; i <= rsmd.getColumnCount(); i++) {
			System.out.print(rsmd.getColumnName(i) + "\t\t");
			if (i == rsmd.getColumnCount())
				System.out.println();
		}
		/* Print results */
		while (rs.next()) {
			System.out.println(
					rs.getString(1) + "\t\t" + rs.getString(2) + "\t\t" + rs.getString(3) + "\t\t\t" + rs.getInt(4));
		}

		/* Close the connections */
		rs.close();
		pstmt.close();
		conn.close();
	}

	/**
	 * courseReport : the 6th feature of Project3. Prints out information of the most recent semester.
	 * @param instID : ID of instructor in integer form (5-digit number in this project)
	 */
	public static void courseReport(int instID) throws Exception {
		/* Load and establish connections to database */
		Class.forName(Main.JDBC_DRIVER);
		Connection conn = DriverManager.getConnection(Main.DATABASE_URL, Main.USERNAME, Main.PASSWORD);

		/* SQL statement used to find lectures of the most recent semester 
		 * using WITH ... AS clause, ORDER BY clause, CASE clause */
		String courseSql = "(WITH max_term AS (SELECT * FROM (SELECT year, semester FROM teaches WHERE ID = " + instID
				+ " ORDER BY year DESC, CASE" + " WHEN substring(semester, 1, 6) IN ('Spring') THEN 4"
				+ " WHEN substring(semester, 1, 6) IN ('Summer') THEN 3"
				+ " WHEN substring(semester, 1, 6) IN ('Fall') THEN 2" + " ELSE 1" + " END) WHERE rownum = 1)"
				+ " SELECT * FROM teaches WHERE ID = " + instID
				+ " AND year IN (SELECT year FROM max_term) AND semester IN (SELECT semester FROM max_term))";

		/* Get more information of the lectures by joining tables 'course' and 'section' 
		 * Get 7 attributes : year, semester, course_id, sec_id, title, building, room_number */
		PreparedStatement pstmt1 = conn.prepareStatement("SELECT year, semester, course_id, sec_id, title, building, room_number FROM " + courseSql
				+ " NATURAL JOIN course NATURAL JOIN section" + " ORDER BY course_id ASC");
		ResultSet rs1 = pstmt1.executeQuery();

		boolean loopNotExecuted = true;		// Determine whether to print phrase "Course report - ..."

		while (rs1.next()) {
			/* Store the result from rs1 temporarily */
			int year = rs1.getInt(1);
			String semester = rs1.getString(2);
			String courseID = rs1.getString(3);
			int sectionID = rs1.getInt(4);
			String title = rs1.getString(5);
			String building = rs1.getString(6);
			int roomNumber = rs1.getInt(7);

			/* Prints out the phrase "Course report - ..." */
			if (loopNotExecuted) {
				System.out.println("Course report - " + year + " " + semester + '\n');
				loopNotExecuted = false;
			} else {
				System.out.println();
			}

			/* Get information of lecture days and time, 
			 * from (section NATURAL JOIN time_slot) */
			PreparedStatement pstmt2 = conn.prepareStatement("SELECT day, start_hr, start_min, end_hr, end_min"
					+ " FROM section NATURAL JOIN time_slot" + " WHERE (course_id, sec_id, semester, year) = (?,?,?,?)");
			pstmt2.setString(1, courseID);
			pstmt2.setInt(2, sectionID);
			pstmt2.setString(3, semester);
			pstmt2.setInt(4, year);
			ResultSet rs2 = pstmt2.executeQuery();
			
			/* Store days and lecture time */
			String days = "";
			int time[] = new int[4];

			/* Get lecture days and time */
			while (rs2.next()) {
				days += rs2.getString(1) + ", ";
				for (int i = 0; i < 4; i++) {
					time[i] = rs2.getInt(i + 2);	// Assume that lecture time of one lecture is same, regardless of day of the week
				}
			}

			/* Close the connection */
			rs2.close();
			pstmt2.close();

			/* Print the basic info of lecture in format
			 * e.g. FIN-201	Investment Banking	[Packard 101] (F, M, W, 9 : 0 - 9 : 50) */
			System.out.println(courseID + "\t" + title + "\t[" + building + " " + roomNumber + "] (" + days + time[0]
					+ " : " + time[1] + " - " + time[2] + " : " + time[3] + ")");

			/* Get information of students who takes the lecture,
			 * from (student NATURAL JOIN takes) */
			PreparedStatement pstmt3 = conn.prepareStatement("SELECT ID, name, dept_name, grade" + " FROM student NATURAL JOIN takes"
					+ " WHERE (course_id, sec_id, semester, year) = (?,?,?,?)");
			pstmt3.setString(1, courseID);
			pstmt3.setInt(2, sectionID);
			pstmt3.setString(3, semester);
			pstmt3.setInt(4, year);
			ResultSet rs3 = pstmt3.executeQuery();

			/* Get metadata for printing formatted table, and print it */
			ResultSetMetaData rsmd3 = rs3.getMetaData();
			for (int i = 1; i <= rsmd3.getColumnCount(); i++) {
				System.out.print(rsmd3.getColumnName(i) + "\t\t");
				if (i == rsmd3.getColumnCount())
					System.out.println();
			}
			/* Print actual information of students,
			 * including their ID, name, dept_name, grade */
			while (rs3.next()) {
				System.out.println(rs3.getString(1) + "\t\t" + rs3.getString(2) + "\t\t" + rs3.getString(3) + "\t\t"
						+ (rs3.getString(4) == null ? "" : rs3.getString(4)));
			}

			/* Close the connection */
			rs3.close();
			pstmt3.close();
		}

		/* Close the connection */
		rs1.close();
		pstmt1.close();
		conn.close();
	}
}
