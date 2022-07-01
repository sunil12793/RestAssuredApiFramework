package com.minca.ap.drugservice.util;


//http://relevantcodes.com/Tools/ExtentReports2/javadoc/index.html?com/relevantcodes/extentreports/ExtentReports.html


import java.io.File;
import java.util.Date;
import com.minca.ap.drugservice.common.*;

import com.relevantcodes.extentreports.DisplayOrder;
import com.relevantcodes.extentreports.ExtentReports;

public class ExtentManager {
	private static ExtentReports extent;

	public static ExtentReports getInstance() {
		if (extent == null) {
			Date d=new Date();
			String fileName="AP-DrugService-API-Tests-"+ d.toString().replace(":", "_").replace(" ", "_")+".html";
			String reportPath = Constants.REPORTS_PATH+fileName;
			System.out.println("reportPath " + reportPath);
			extent = new ExtentReports(reportPath, true, DisplayOrder.NEWEST_FIRST);
			//extent.loadConfig(new File(System.getProperty("user.dir")+"//ReportsConfig.xml"));
			extent.loadConfig(new File(System.getProperty("user.dir")+"//src//test//resources//extent-config.xml"));
			
		}
		return extent;
	}
}
