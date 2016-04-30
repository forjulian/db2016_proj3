import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.*;

import com.tmax.tibero.jdbc.*;
import com.tmax.tibero.jdbc.ext.*;

public class InstructorMenu {
	static final String driver = "com.tmax.tibero.jdbc.TbDriver";
	static final String url = "jdbc:tibero:thin:@localhost:8629:tibero";

	/**
	 * adviseeReport : the 7th feature of Project3. Prints out basic information
	 * of advisees.
	 * 
	 * @param instID
	 *            : ID of instructor in integer form (5-digit number in this
	 *            project)
	 * @param instName
	 *            : name of instructor in String form
	 * @throws Exception
	 */
	public static void adviseeReport(int instID, String instName) throws Exception {
		/* Loads and establish connections to database */
		Class.forName(driver);
		Connection conn = DriverManager.getConnection(url, "sys", "tibero");

		/* Manipulates SQL statements for searching advisee information */
		PreparedStatement pstmt = conn.prepareStatement(
				"SELECT S.ID, S.name, S.dept_name, S.tot_cred" + " FROM instructor I JOIN advisor A ON (I.ID = A.i_id)"
						+ " JOIN student S ON (S.ID = A.s_id)" + " where I.ID = ?"); // Gets
																						// information
																						// of
																						// advisees
																						// from
																						// (instructor
																						// JOIN
																						// advisor
																						// JOIN
																						// student)
		pstmt.setString(1, String.valueOf(instID)); // Inserts the ID of
													// instructor to SQL query

		/* Gets the results */
		ResultSet rs = pstmt.executeQuery();
		ResultSetMetaData rsmd = rs.getMetaData(); // Gets metadata of the
													// result for printing a
													// table

		/* Prints a formatted table */
		for (int i = 1; i <= rsmd.getColumnCount(); i++) {
			System.out.print(rsmd.getColumnName(i) + "\t\t");
			if (i == rsmd.getColumnCount())
				System.out.println();
		}
		while (rs.next()) {
			System.out.println(
					rs.getInt(1) + "\t\t" + rs.getString(2) + "\t\t" + rs.getString(3) + "\t\t\t" + rs.getInt(4));
		}

		/* Closes the connections */
		rs.close();
		pstmt.close();
		conn.close();
	}

	/**
	 * courseReport : the 6th feature of Project3. Prints out information of the
	 * most recent semester.
	 * 
	 * @param instID
	 *            : ID of instructor in integer form (5-digit number in this
	 *            project)
	 * @param instName
	 *            : name of instructor in String form
	 * @throws Exception
	 */
	public static void courseReport(int instID, String instName) throws Exception {
		/* Loads and establish connections to database */
		Class.forName(driver);
		Connection conn = DriverManager.getConnection(url, "sys", "tibero");

		/* SQL statement used to find lectures of the most recent semester */
		String courseSql = "(WITH max_term AS (SELECT * FROM (SELECT year, semester FROM teaches WHERE ID = " + instID
				+ " ORDER BY year DESC, CASE" + " WHEN substring(semester, 1, 6) IN ('Spring') THEN 1"
				+ " WHEN substring(semester, 1, 6) IN ('Summer') THEN 2"
				+ " WHEN substring(semester, 1, 6) IN ('Fall') THEN 3" + " ELSE 4" + " END) WHERE rownum = 1)"
				+ " SELECT * FROM teaches WHERE ID = " + instID
				+ " AND year IN (SELECT year FROM max_term) AND semester IN (SELECT semester FROM max_term))";

		/*
		 * Gets more information of the lectures by joining tables 'course' and
		 * 'section'
		 */
		Statement stmt1 = conn.createStatement();
		ResultSet rs1 = stmt1
				.executeQuery("SELECT year, semester, course_id, sec_id, title, building, room_number FROM " + courseSql
						+ " NATURAL JOIN course NATURAL JOIN section" + " ORDER BY course_id ASC");

		boolean loopNotExecuted = true;

		while (rs1.next()) {
			int year = rs1.getInt(1);
			String semester = rs1.getString(2);
			String courseID = rs1.getString(3);
			int sectionID = rs1.getInt(4);
			String title = rs1.getString(5);
			String building = rs1.getString(6);
			int roomNumber = rs1.getInt(7);

			if (loopNotExecuted) {
				System.out.println("Course report - " + year + " " + semester + '\n');
				loopNotExecuted = false;
			} else {
				System.out.println();
			}

			Statement stmt2 = conn.createStatement();
			ResultSet rs2 = stmt2.executeQuery("SELECT day, start_hr, start_min, end_hr, end_min"
					+ " FROM section NATURAL JOIN time_slot" + " WHERE course_id = " + "'" + courseID + "'"
					+ " AND sec_id = " + sectionID + " AND semester = " + "'" + semester + "'" + " AND year = " + year);

			String days = "";
			int time[] = new int[4];

			while (rs2.next()) {
				days += rs2.getString(1) + ", ";
				for (int i = 0; i < 4; i++) {
					time[i] = rs2.getInt(i + 2);
				}
			}

			rs2.close();
			stmt2.close();

			System.out.println(courseID + "\t" + title + "\t[" + building + " " + roomNumber + "] (" + days + time[0]
					+ " : " + time[1] + " - " + time[2] + " : " + time[3] + ")");

			Statement stmt3 = conn.createStatement();
			ResultSet rs3 = stmt3.executeQuery("SELECT ID, name, dept_name, grade" + " FROM student NATURAL JOIN takes"
					+ " WHERE course_id = " + "'" + courseID + "'" + " AND sec_id = " + sectionID + " AND semester = "
					+ "'" + semester + "'" + " AND year = " + year);

			ResultSetMetaData rsmd3 = rs3.getMetaData();
			for (int i = 1; i <= rsmd3.getColumnCount(); i++) {
				System.out.print(rsmd3.getColumnName(i) + "\t\t");
				if (i == rsmd3.getColumnCount())
					System.out.println();
			}
			while (rs3.next()) {
				System.out.println(rs3.getString(1) + "\t\t" + rs3.getString(2) + "\t\t" + rs3.getString(3) + "\t\t"
						+ rs3.getString(4));
			}

			rs3.close();
			stmt3.close();
		}

		rs1.close();
		stmt1.close();
		conn.close();
	}

	public static void main(String[] args) throws Exception {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		while (true) {
			String input = br.readLine();

			if (input.compareTo("quit") == 0) {
				break;
			}
			courseReport(Integer.valueOf(input), "");
		}
	}
}
