import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class ParseExcelToSQL {
	// public static void main(String[] args) {
	// public static String parser(String filePath) {
	public static void parser(String filePath) {
		StringBuffer output = new StringBuffer();

		// reset output to empty
		// output.setLength(0);

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
			
			// Overwrite all data by dropping table
			sql = "DROP TABLE IF EXISTS " + db + " CASCADE;"
					+ "CREATE TABLE " + db + " ("
					+ "ID SERIAL,"
					+ "NAME VARCHAR(255),"
				    + "PART VARCHAR(255),"
					+ "MATERIAL VARCHAR(255),"
				    + "BATCH VARCHAR(255),"
					+ "QTY INT);"
					+ "GRANT ALL PRIVILEGES ON TABLE " + db + " TO INVENTORY;";
			stmt.executeUpdate(sql);

			FileInputStream file = new FileInputStream(new File(filePath));
			// File filePath = new
			// File("C:\\Users\\Administrator\\Desktop\\Eclipse\\inv2016copy.xlsx");
			// FileInputStream file = new FileInputStream(filePath);
			// Create Workbook instance holding reference to .xlsx file
			XSSFWorkbook wb = new XSSFWorkbook(file);

			// Get first/desired sheet from the workbook
			XSSFSheet sheet = wb.getSheetAt(0);

			// Iterate through each rows one by one
			Iterator<Row> rowIterator = sheet.iterator();

			int curRow = 0, curCol = 0;
			// for part
			String allParts = "Part No.";
			int firstPartRow = 0, lastPartRow = 0, partCol = 0, partNext = 1;
			Cell partRowStart = null, part = null;
			// for name
			// int firstNameCol = 0, lastNameCol = 0, nameRow = 0, nameNext = 1;
			String name, namePart, nameMaterial;
			String nameBatch = null;
			Cell batch = null;
			int partQty = 0;
			Cell qty = null;

			// output.append("below parts were add to database:<br><br>");

			while(rowIterator.hasNext()) {
				Row row = rowIterator.next();
				curRow = row.getRowNum();
				// For each row, iterate through all the columns
				Iterator<Cell> cellIterator = row.cellIterator();

				// if (firstPartRow != 0)
				// break;

				while(cellIterator.hasNext()) {
					Cell cell = cellIterator.next();
					curCol = cell.getColumnIndex();

					// Check the cell type and format accordingly
					// getCellType & CELL_TYPE_* is POI 3.14
					// switch (cell.getCellType()){
					// case Cell.CELL_TYPE_NUMERIC:
					// break;
					// case Cell.CELL_TYPE_STRING:

					// POI 3.16 change to getCellTypeEnum
					switch (cell.getCellTypeEnum()) {
						case NUMERIC:
							break;
						case STRING:

							if(cell.getStringCellValue().toString().equals(allParts)) {
								// Find Part No.
								firstPartRow = curRow;
								partCol = curCol;

								while(partRowStart == null || partRowStart.getCellTypeEnum() == CellType.BLANK) {
									partRowStart = sheet.getRow(firstPartRow + partNext).getCell(partCol);
									partNext++;
								}

								firstPartRow += partNext;
								partNext = 0;

								Cell partTemp = sheet.getRow(firstPartRow + partNext).getCell(partCol);
								while(partTemp != null && partTemp.getCellTypeEnum() != CellType.BLANK) {
									partTemp = sheet.getRow(firstPartRow + partNext).getCell(partCol);

									if(partTemp != null) {
										part = partTemp;

										// System.out.println(part + "\t\t\tnext
										// = " + (firstPartRow + partNext));

										// output.append(part + "<br>");

										// sql = "INSERT INTO INV_2016 (part)" +
										// "VALUES ('" + part + "')"
										// + "ON CONFLICT (part) DO NOTHING;";
										// stmt.executeUpdate(sql);
										lastPartRow = firstPartRow + partNext;
									}
									partNext++;
								}
								// Find Part No.

								// Find Name
								while(cellIterator.hasNext()) {
									Cell nameCell = cellIterator.next();
									switch (nameCell.getCellTypeEnum()) {
										case STRING:
											// find all the name
											if(nameCell.getRichStringCellValue().getString().trim().contains(")")
													&& !sheet.isColumnHidden(nameCell.getColumnIndex())) {
												name = nameCell.getRichStringCellValue().getString().trim();

												// loop all the parts
												for(int i = firstPartRow; i <= lastPartRow; i++) {
													qty = sheet.getRow(i).getCell(nameCell.getColumnIndex());

													// if QTY != empty, record
													// Name + Part + QTY
													if(qty.getCellTypeEnum() == CellType.NUMERIC) {
														namePart = sheet.getRow(i).getCell(partCol)
																.getRichStringCellValue().getString().trim();
														nameMaterial = sheet.getRow(i).getCell(partCol + 1)
																.getRichStringCellValue().getString().trim();
														
														batch = sheet.getRow(i).getCell(partCol + 2);
														
														// batch no. are mixing with empty, float, and string
														switch (batch.getCellTypeEnum()) {
															case NUMERIC:
																Float tmp = (float) batch.getNumericCellValue();
																nameBatch = Float.toString(tmp);
																break;
															case STRING:
																nameBatch = batch.getRichStringCellValue().getString().trim();
																break;
															case BLANK:
																nameBatch = "";
																break;
															default:
																break;
														}
														
														partQty = (int) qty.getNumericCellValue();
														System.out.println(name + "\t" + namePart + "\t" + nameMaterial
																+ "\t" + partQty);
														
														sql = "INSERT INTO " + db + " (NAME, PART, MATERIAL, BATCH, QTY)"
																+ "VALUES ('" + name + "', '" + namePart + "', '"
																+ nameMaterial + "', '" + nameBatch + "', '" + partQty + "');";
														stmt.executeUpdate(sql);
													}
												}
											}
											break;
										default:
											break;
									}
								}
								// Find Name
							}
							break;
						default:
							break;
					}
				}
			}

			// copy all data except ID to public table.
//			sql = "DROP TABLE IF EXISTS INV_PUBLIC CASCADE;"
//					+ "CREATE TABLE INV_PUBLIC AS "
//					+ "SELECT * FROM " + db + ";"
//					+ "GRANT ALL PRIVILEGES ON TABLE INV_PUBLIC TO invpublic;";
//
//			stmt.executeUpdate(sql);
			// part no start with ROW=6, COL(getCell)=9

			// System.out.println("\n\npart row = " + firstPartRow);
			// System.out.println("part col = " + partCol);
			// System.out.println("last row = " + part.getRowIndex());
			// System.out.println(sheet.getRow(21).getCell(partCol).getStringCellValue());
			file.close();
			stmt.close();
			c.close();
		} catch(Exception e) {
			e.printStackTrace();
			// output.append(e);
		}
		// return output.toString();
	}
}