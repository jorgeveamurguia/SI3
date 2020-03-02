package es.uned.si3.web.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringBufferInputStream;

import javax.servlet.ServletContext;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.util.ServletContextAware;
import org.apache.struts2.views.freemarker.tags.SetModel;

import com.opensymphony.xwork2.ActionSupport;

import es.uned.si3.basededatos.GestorSql;
import es.uned.si3.basededatos.manager.Manager;
import es.uned.si3.persistencia.Proyecto;
import es.uned.si3.persistencia.Solicitud;

public class DownloadFileController extends BaseController implements
		ServletContextAware {
	private static final long serialVersionUID = 1L;
	private InputStream inputStream;
	ServletContext context;

	String table = "";
	String id = "";
	String column = "";
	String fileName = "";

	public String getFileName() {
		return this.fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getTable() {
		return this.table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getColumn() {
		return this.column;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	@Override
	public String execute() throws Exception {
		GestorSql gestor = GestorSql.getInstance();
		try {
			if (getTable().equals("Solicitud")) {
				Solicitud solicitud = new Solicitud();
				solicitud.ID = Integer.parseInt(getId());
				solicitud = Manager.getInstanceFromSql(gestor, solicitud, true);
				if (solicitud == null)
					return ERROR;
				if (getColumn().equals("pdfConocimientos")) {
					inputStream = gestor.getFile(solicitud.ID, getTable(),
							getColumn());
					if (inputStream == null) {
						this.addActionError("No se puede descargar el fichero");
						return ERROR;
					}
					return SUCCESS;
				}
				
				return SUCCESS;
			} else if (getTable().equals("Proyecto")) {
				Proyecto proyecto = new Proyecto();
				proyecto.ID = Integer.parseInt(getId());
				proyecto = Manager.getInstanceFromSql(gestor, proyecto, true);
				if (proyecto == null)
					return ERROR;
				if (getColumn().equals("memoria_pdf")) {
					inputStream = gestor.getFile(proyecto.ID, getTable(),
							getColumn());
					if (inputStream == null) {
						this.addActionError("No se puede descargar el fichero");
						return ERROR;
					}
					return SUCCESS;
				}
				if (getColumn().equals("fichero_comprimido")) {
					inputStream = gestor.getFile(proyecto.ID, getTable(),
							getColumn());
					if (inputStream == null) {
						this.addActionError("No se puede descargar el fichero");
						return ERROR;
					}
					return SUCCESS;
				}

				return ERROR;
			}
		} catch (Exception e) {
			this.addActionError("Error downloading file: " + e.getMessage());
			return ERROR;
		}
		return ERROR;
	}

	@Override
	public void setServletContext(ServletContext context) {
		this.context = context;
	}
}
