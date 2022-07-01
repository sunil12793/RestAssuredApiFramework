package com.minca.ap.reports;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.testng.IResultMap;
import org.testng.ISuite;
import org.testng.ISuiteResult;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.xml.XmlSuite;

import com.minca.ap.drugservice.common.Constants;

public class GenerateReport implements org.testng.IReporter{

	@Override
	public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectory) {
	// TODO Auto-generated method stub
	//IReporter.super.generateReport(xmlSuites, suites, outputDirectory);
	
	//Iterating over each suite included in the test
      for (ISuite suite : suites) {
            
         //Following code gets the suite name
         String suiteName = suite.getName();
            
		 //Getting the results for the said suite
		Map<String, ISuiteResult> suiteResults = suite.getResults();
		System.out.println("outputDirectory '" + outputDirectory);
		String emailBody = "<table border = \"1\" cellpadding=\"20\">";
		
		for (ISuiteResult sr : suiteResults.values()) {
			ITestContext tc = sr.getTestContext();
			System.out.println("Passed tests for suite '" + suiteName + "' is:" + tc.getPassedTests().getAllResults().size());
			System.out.println("Failed tests for suite '" + suiteName +   "' is:" + tc.getFailedTests().getAllResults().size());
			System.out.println("Skipped tests for suite '" + suiteName + "' is:" + tc.getSkippedTests().getAllResults().size());
			
			emailBody += "<tr bgcolor=\"#006400\"> <td >  Passed tests for suite '" + suiteName + "' is: </td> <td>" + tc.getPassedTests().getAllResults().size() + "</td></tr> ";
			emailBody += "<tr bgcolor=\"#8B0000\"> <td> Failed tests for suite '" + suiteName +   "' is: </td> <td>" + tc.getFailedTests().getAllResults().size()+ "</td></tr> ";
			emailBody += "<tr bgcolor=\"#00FFFF\"> <td> Skipped tests for suite '" + suiteName + "' is:</td> <td>" + tc.getSkippedTests().getAllResults().size()+ "</td></tr> ";
			emailBody += "</table>";
			
			
			Set<ITestResult> failedTests = tc.getFailedTests().getAllResults();
			if(failedTests != null && failedTests.size() != 0) {
				emailBody += "<br> <br> <table border = \"1\" cellpadding=\"10\">";
				emailBody += "<tr bgcolor=\"#8B0000\"> <td >  <b> List of Failed Tests Below: </td></tr> ";
				for(ITestResult failedTest: failedTests) {
					emailBody += "<tr bgcolor=\"#8B0000\"> <td > " + failedTest.getName() + "</td></tr> ";
				}
				emailBody += "</table>";
			}
			
			Set<ITestResult> skippedtests = tc.getSkippedTests().getAllResults();
			if(skippedtests != null && skippedtests.size() != 0) {
				emailBody += "<br> <br> <table border = \"1\" cellpadding=\"10\">";
				emailBody += "<tr bgcolor=\"#00FFFF\"> <td >  <b>  List of Skipped Tests Below: </td></tr> ";
				for(ITestResult skippedtest: skippedtests) {
					emailBody += "<tr bgcolor=\"#00FFFF\"> <td > " + skippedtest.getName() + "</td></tr> ";
				}
				emailBody += "</table>";
			}
			
					    
		 }
		emailBody += "</table>";
		Date d=new Date();
		String fileName="EmailBody-"+ d.toString().replace(":", "_").replace(" ", "_")+".txt";
		String reportPath = Constants.EMAILBODY_PATH+fileName;
		System.out.println("reportPath " + reportPath);
		try {
			FileOutputStream fout=new FileOutputStream(reportPath);
			fout.write(emailBody.getBytes());
			fout.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}   catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
      }
   }
		
}

	
	
	

