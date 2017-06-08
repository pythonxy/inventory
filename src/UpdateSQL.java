import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.google.gson.Gson;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class UpdateSQL {
	// public static void main(String[] args) {
	// public static String parser(String filePath) {
	public static void UpdateSQL(BufferedReader reader) {
		try {
			// DATABASE
			String sql = null;
			Connection c = null;
			Statement stmt = null;

			String URL = "jdbc:postgresql://b6inventory:5432/inventory";
			String USER = "inventory";
			String PWD = "inventory";
			String db = "INV_TEST2";

			Class.forName("org.postgresql.Driver");
			c = DriverManager.getConnection(URL, USER, PWD);
			stmt = c.createStatement();
			
			String name, part, material, batch, id;
			int qty;
			boolean checked;

			Gson gson = new Gson();
			ParseJson[] items = gson.fromJson(reader, ParseJson[].class);

			for(ParseJson item : items) {
				if(item != null) {
					id = item.id;
					name = item.name;
					part = item.part;
					material = item.material;
					batch = item.batch;
					qty = item.qty;
					checked = item.checked;
					
					if(!id.equals("null"))
						sql = "UPDATE " + db + " "
							+ "SET NAME='" + name + "', PART='" + part + "', MATERIAL='" + material + "', BATCH='" + batch + "', QTY='" + qty + "' "
							+ "WHERE ID=" + id + ";";
//							+ " WHERE NAME='" + name + "' AND PART='" + part + "' AND MATERIAL='" + material + "'";
					else
						sql = "INSERT INTO " + db + " (ID, NAME, PART, MATERIAL, BATCH, QTY)"
								+ "VALUES (DEFAULT, '" + name + "', '" + part + "', '"
								+ material + "', '" + batch + "', '" + qty + "');";
					
					if(checked)
//						System.out.println(id);
						sql = "DELETE FROM " + db + " "
							+ "WHERE ID=" + id + ";";
					
					stmt.executeUpdate(sql);
				}
			}
			
//			stmt.close();
//			c.close();
		} catch(Exception e) {
			e.printStackTrace();
			// output.append(e);
		}
		// return output.toString();
	}
}