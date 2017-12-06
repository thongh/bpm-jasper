package com.cc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.ExporterInput;
import net.sf.jasperreports.export.OutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;

public class GenerateDocument {

	public static void main(String[] args){
		try {
			try {
				generateReportToDir(args[0], args[1], args[2]);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (JRException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String helloworld(String name) {
		return "Hi " + name;
	}

	public static void generateReportToDir(String pathToTemplate, String outputDir, 
			String exportDocName) throws JRException, SQLException, ClassNotFoundException {
		
		String hostName = "172.16.11.25";
        String dbName = "proteldb";
        String userName = "db2inst1";
        String password = "password";
		
		// Compile JRXML
        // Sample path: "C:/temp/template.jrxml";
        System.out.println("pathToTemplate: " + pathToTemplate);
        System.out.println("outputDir: " + outputDir);
        System.out.println("exportDocName: " + exportDocName);
        System.out.println("compiling jrxml template...");
        JasperReport jasperReport = JasperCompileManager.compileReport(pathToTemplate);
		
		// Connect to BPMDB
        System.out.println("creating db connection...");
		Connection dbConnection = getDB2Connection(
				hostName, dbName, userName, password);
		
        // Setup parameters for template
		System.out.println("defining parameters...");
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("SiteID", "BAL-BAL-0002-J-M");
        
        // Generate
        System.out.println("generating report...");
        JasperPrint print = JasperFillManager.fillReport(jasperReport, parameters, dbConnection);
        
        // Export generated document
        System.out.println("exporting report...");
        JRPdfExporter exporter = new JRPdfExporter();
        ExporterInput exporterInput = new SimpleExporterInput(print);
        exporter.setExporterInput(exporterInput);
        OutputStreamExporterOutput exporterOutput = 
        		new SimpleOutputStreamExporterOutput(outputDir + exportDocName);
        exporter.setExporterOutput(exporterOutput);
        SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
        exporter.setConfiguration(configuration);
        exporter.exportReport();
        
        System.out.println("Done");
	}
	
	
	private static Connection getDB2Connection(String hostName, String dbName, 
			String userName, String password) throws SQLException, ClassNotFoundException {
		Class.forName("com.ibm.db2.jcc.DB2Driver");
		String connectionURL = "jdbc:db2://" + hostName + ":50000/" + dbName;
		Connection conn = DriverManager.getConnection(connectionURL, userName,
                password);
		return conn;
	}
}

