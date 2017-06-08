import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.sql.ResultSet;

import com.google.gson.Gson;

@WebServlet(urlPatterns = "/getData")
public class GetData extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void processRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			ArrayList<JsonData> allItems = new ArrayList<>();
			DecimalFormat df = new DecimalFormat();
			df.setMinimumFractionDigits(2);

			String sql = null;
			Connection c = null;
			Statement stmt = null;

			String URL = "jdbc:postgresql://localhost:5432/inventory";
			String USER = "inventory";
			String PWD = "inventory";
			String db = "INV_TEST2";

			Class.forName("org.postgresql.Driver");
			c = DriverManager.getConnection(URL, USER, PWD);
			stmt = c.createStatement();

			sql = "SELECT * FROM " + db + " ORDER BY NAME, PART";
			
			System.out.println(sql);

			ResultSet rs = stmt.executeQuery(sql);
			// ResultSetMetaData rsmd = rs.getMetaData();
			// int columnCount = rsmd.getColumnCount();

			while(rs.next()) {
				int id = rs.getInt("id");
				String name = rs.getString("name");
				String part = rs.getString("part");
				String material = rs.getString("material");
				String batch = rs.getString("batch");
				int qty = rs.getInt("qty");

				allItems.add(new JsonData(id, name, part, material, batch, qty));

				// System.out.println(name + "\t" + part + "\t" + material +
				// "\t" + qty);
			}

			// for allname here
			//
			// sql = "SELECT * FROM CUSTODIANS" + " ORDER BY MEMBER";
			// rs = stmt.executeQuery(sql);
			
			
			String json = new Gson().toJson(allItems);
			// System.out.println(json);

			response.setContentType("application/json");
			response.getWriter().write(json);

			stmt.close();
			c.close();
		} catch(Exception e) {
			e.printStackTrace();
			System.out.println(e);
		}
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		processRequest(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		processRequest(request, response);
	}
}
