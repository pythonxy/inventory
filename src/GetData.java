import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.sql.ResultSet;

import com.google.gson.Gson;

@WebServlet(urlPatterns = "/getData")
public class GetData extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void processRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			// jsonData invData = new jsonData();
			ArrayList<jsonData> allItems = new ArrayList<>();

			String sql = null;
			Connection c = null;
			Statement stmt = null;

			String URL = "jdbc:postgresql://b6inventory:5432/inventory";
			String USER = "inventory";
			String PWD = "inventory";

			Class.forName("org.postgresql.Driver");
			c = DriverManager.getConnection(URL, USER, PWD);
			stmt = c.createStatement();

			sql = "SELECT * FROM INV_2016" + " ORDER BY NAME";

			ResultSet rs = stmt.executeQuery(sql);
			// ResultSetMetaData rsmd = rs.getMetaData();
			// int columnCount = rsmd.getColumnCount();

			while(rs.next()) {
				String name = rs.getString("name");
				String part = rs.getString("part");
				String material = rs.getString("material");
				int qty = rs.getInt("qty");

				allItems.add(new jsonData(name, part, material, qty));

				// System.out.println(name + "\t" + part + "\t" + material +
				// "\t" + qty);
			}

			String json = new Gson().toJson(allItems);

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
