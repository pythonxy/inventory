import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

@WebServlet(urlPatterns = "/uploadExcel")
public class UploadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	// upload folder name
	private static final String UPLOAD_DIRECTORY = "uploadFiles";

	// upload size
	private static final int MEMORY_THRESHOLD = 1024 * 1024 * 50; // 30MB
	private static final int MAX_FILE_SIZE = 1024 * 1024 * 50; // 50MB
	private static final int MAX_REQUEST_SIZE = 1024 * 1024 * 60; // 60MB

	protected void processRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// check if file is multi part content
		if(!ServletFileUpload.isMultipartContent(request)) {
			// stop if not multi part content
			PrintWriter writer = response.getWriter();
			writer.println("Error: file enctype=multipart/form-data");
			writer.flush();
			return;
		}

		// set upload parameter
		DiskFileItemFactory factory = new DiskFileItemFactory();
		// set memory size - exceeded will create temp file and save into temp
		// folder
		factory.setSizeThreshold(MEMORY_THRESHOLD);
		// set temp folder to save temp file
		factory.setRepository(new File(System.getProperty("java.io.tmpdir")));

		ServletFileUpload upload = new ServletFileUpload(factory);

		// max file size
		upload.setFileSizeMax(MAX_FILE_SIZE);

		// max request size include file and form
		upload.setSizeMax(MAX_REQUEST_SIZE);

		// for Chinese encoding
		upload.setHeaderEncoding("UTF-8");

		// create temp upload path
		// String uploadPath = request.getServletContext().getRealPath("./") +
		// File.separator + UPLOAD_DIRECTORY;
		String uploadPath = "C:\\temp";

		// create folder if not exists
		File uploadDir = new File(uploadPath);
		if(!uploadDir.exists()) {
			uploadDir.mkdir();
		}

		// complete file path, uses to pass into readExcel.java
		String fileP = "";

		try {
			// Parse the contents of the request to extract the file data
			@SuppressWarnings("unchecked")
			List<FileItem> formItems = upload.parseRequest(request);

			if(formItems != null && formItems.size() > 0) {
				// Iterate form data
				for(FileItem item : formItems) {
					// Handles fields that are not in the form
					if(!item.isFormField()) {
						String fileName = new File(item.getName()).getName();
						String filePath = uploadPath + File.separator + fileName;
						fileP = filePath;
						File storeFile = new File(filePath);
						// output upload path to console
						System.out.println(filePath);
						// save file to hard drive
						item.write(storeFile);
						request.setAttribute("message", "upload successful<br><br>" + "file path = " + filePath);
					}
				}
			}

			ParseExcelToSQL rd = new ParseExcelToSQL();
			// String output = rd.parser(fileP);
			rd.parser(fileP);

			// request.setAttribute("parts", "DONE");
			response.setContentType("text/plain");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write("File uploaded. All data imported to database.");
		} catch(Exception e) {
			// request.setAttribute("message", "Error message: " +
			// e.getMessage());
			System.out.println(e);
		}
		// jump message.jsp
		// request.getServletContext().getRequestDispatcher("/message.jsp").forward(request,
		// response);
	}

	// <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on
	// the + sign on the left to edit the code.">
	/**
	 * Handles the HTTP <code>GET</code> method.
	 *
	 * @param request
	 *            servlet request
	 * @param response
	 *            servlet response
	 * @throws ServletException
	 *             if a servlet-specific error occurs
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// processRequest(request, response);
	}

	/**
	 * Handles the HTTP <code>POST</code> method.
	 *
	 * @param request
	 *            servlet request
	 * @param response
	 *            servlet response
	 * @throws ServletException
	 *             if a servlet-specific error occurs
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		processRequest(request, response);
	}
}
