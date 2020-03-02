package es.uned.si3.web.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DoSql extends HttpServlet  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//byteArrayStream.writeTo(response.getOutputStream()). 
		
		response.setContentType("APPLICATION/OCTET-STREAM");
		String disHeader = "Attachment;Filename=\"basededatosdetest.sql\"";
		response.setHeader("Content-Disposition", disHeader);
		
			PrintWriter out = response.getWriter();
			//out.println("Hello World");
			
			es.uned.si3.basededatos.CrearBaseDatos baseDedatos;
			try {
				baseDedatos = new es.uned.si3.basededatos.CrearBaseDatos();
			} catch (Exception e) {
				System.out.println("EXCEPTION en main: " + e.getMessage());
				return;
			}

			out.println(baseDedatos.backup());
	}
}
