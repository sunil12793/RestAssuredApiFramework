package com.minca.ap.mail;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

//mvn exec:java -Dexec.mainClass=com.minca.misource.mail.SendMailMinca
//mvn exec:java -Dexec.mainClass=com.minca.misource.mail.SendMailMinca -Dexec.args="NOTALL"

//mvn exec:java -Dexec.mainClass=com.minca.ap.mail.SendMailMinca -Dexec.args="NOTALL"
//cd C:\Projects\AutomatingQA\Jan21st2020\rest-api-automation\order-automation-tests && "C:\Program Files\apache-maven-3.0.5\bin\mvn" exec:java -Dexec.mainClass=com.minca.ap.mail.SendMailMinca -Dexec.args="NOTALL" 
//mvn exec:java -Dexec.mainClass="com.minca.ap.mail.SendMailMinca" -Dexec.args="NOTALL" 

//mvn exec:java -Dexec.mainClass=com.minca.ap.mail.SendMailMinca -Dexec.args="QA" 
public class SendMailMinca

{
	public static String sep = File.separator;
	public static final String REPORTS_PATH = System.getProperty("user.dir") + sep + "src" + sep + "test" + sep + "resources" + sep + "Reporting" + sep;
	public static final String EMAILBODY_PATH = System.getProperty("user.dir") + sep + "src" + sep + "test" + sep + "resources" + sep + "EmailBodyFile" + sep;
	
	public static void main(String[] args) throws Exception

	{

		//String reportFolder= Constants.REPORTS_PATH;
		String reportFolder= REPORTS_PATH;
		System.out.println("reportFolder " + reportFolder);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		FileFilterDateIntervalUtils filter =
				new FileFilterDateIntervalUtils("2010-01-04", "2050-01-20");
		File folder =  new File(reportFolder);
		File files[] = folder.listFiles(filter);
		Arrays.sort(files, new Comparator<File>() {
		    public int compare(File f1, File f2) {
		        return Long.compare(f1.lastModified(), f2.lastModified());
		    }
		});
		System.out.println("files " + Arrays.toString(files));
		//date

		String fileName=files[files.length-1].getName();
		String extentFilePath=reportFolder+fileName;
		System.out.println("fileName " + fileName);
		System.out.println("extentFilePath " + extentFilePath);
		String type = args[0];
        String env = args[1];
		//String type="NOTQA";
		List<String> emailList = null;
		
		if (type.equals("ALL"))
			emailList =  Arrays.asList("venkata.kamaraju@mincainc.com", "vijaya.g@photon.com", "madhavan.r@photon.com", "sunil.v@photon.com");
		else if (type.equals("QA"))
			emailList = Arrays.asList("venkata.kamaraju@mincainc.com", "vijaya.g@photon.com", "madhavan.r@photon.com", "sunil.v@photon.com");
			//emailList = Arrays.asList("nivedita.banerjee@mincainc.com");
		else
			emailList = Arrays.asList("venkata.kamaraju@mincainc.com");
			
		String[] to = emailList.toArray( new String[emailList.size()] );
		
		
		String extentReportZip=reportFolder+"Reports.zip";
		//windows
		//Zip.zipDir(System.getProperty("user.dir")+"//Extentreport", extentReportZip);
		
		//mac
		//Zip.zipDir(System.getProperty("user.dir")+"/Extentreport", extentReportZip);
		//String sep = Constants.sep;
		//Zip.zipDir(System.getProperty("user.dir")+sep+"Extentreport", extentReportZip);
		Zip.zipDir(REPORTS_PATH, extentReportZip);
		
		String[] cc={};
		String[] bcc={};
		String emailBody="";
		try{
			String reportFolder1= EMAILBODY_PATH;
			System.out.println("reportFolder1 ***** " + reportFolder1);
			sdf = new SimpleDateFormat("yyyy-MM-dd");
			filter = new FileFilterDateIntervalUtils("2010-01-04", "2050-01-20");
			File folder1 =  new File(reportFolder1);
			File files1[] = folder1.listFiles(filter);
			Arrays.sort(files1, new Comparator<File>() {
			    public int compare(File f1, File f2) {
			        return Long.compare(f1.lastModified(), f2.lastModified());
			    }
			});
			System.out.println("files1 ****  " + Arrays.toString(files1));
			//date

			String fileName1=files1[files1.length-1].getName();
			String extentFilePath1=reportFolder1+fileName1;
			System.out.println("fileName *** " + fileName1);
			System.out.println("extentFilePath **** " + extentFilePath1);
			
			FileInputStream fin=new FileInputStream(extentFilePath1);    
			BufferedInputStream bin=new BufferedInputStream(fin);    
		    int i;    
		    while((i=bin.read())!=-1){   
		    	emailBody += (char)i;
		    }    
		    bin.close();    
		    fin.close();    
		}catch(Exception e){
			e.printStackTrace();
		}
		
		emailBody += "<br> <br>  <b> Please find details in the attached reports. You can also find the reports in jenkins server at http://qv1medcctest2:8080// <br> <br> Regards,<br> QA Automation <b>";

		
		//This is for minca smtp server
		sendMail("no-reply@mincainc.com",
				"",
				"mx.medimpact.com",
				"",
				"true",
				"false",
				true,
				"javax.net.ssl.SSLSocketFactory",
				"false",
				to,
				cc,
				bcc,
				env + " --- Americas Pharmacy - Drug Service API Regression Test Reports",
				emailBody,//"Please find the attached reports. For screenshots please go to the jenkins job at http://qv1medcctest2:8080// \n\nRegards\nQA Automation",
				extentFilePath,
				fileName);
	}

	public  static boolean sendMail(
			final String userName,
			final String passWord,
			String host,
			String port,
			String starttls,
			String auth,
			boolean debug,
			String socketFactoryClass,
			String fallback,
			String[] to,
			String[] cc,
			String[] bcc,
			String subject,
			String text,
			String attachmentPath,
			String attachmentName){



		Properties props = new Properties();
		props.put("mail.smtp.starttls.enable", starttls);
		props.put("mail.smtp.auth",auth);
		props.put("mail.smtp.host", host);
		//props.put("mail.smtp.port", port);
		

		try

		{

			/*Session session = Session.getInstance(props,
					new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(userName, passWord);
				}
			});*/
			//no auth required with minca server
			Session session = Session.getInstance(props);

			MimeMessage msg = new MimeMessage(session);

			msg.setText(text);
			msg.setSubject(subject);
			
			//set the text part of the email
			final MimeBodyPart textPart = new MimeBodyPart();
	        textPart.setContent(text, "text/html"); 
	        
			//attachment start
			// create the message part 

			Multipart multipart = new MimeMultipart();
			MimeBodyPart messageBodyPart = new MimeBodyPart();
			DataSource source = 
					new FileDataSource(attachmentPath);
			messageBodyPart.setDataHandler(
					new DataHandler(source));
			messageBodyPart.setFileName(attachmentName);
			multipart.addBodyPart(messageBodyPart);
			multipart.addBodyPart(textPart);

			// attachment ends

			// Put parts in message
			msg.setContent(multipart);
			msg.setFrom(new InternetAddress(userName));

			for(int i=0;i<to.length;i++){

				msg.addRecipient(Message.RecipientType.TO, new InternetAddress(to[i]));

			}

			for(int i=0;i<cc.length;i++){

				msg.addRecipient(Message.RecipientType.CC, new InternetAddress(cc[i]));

			}

			for(int i=0;i<bcc.length;i++){

				msg.addRecipient(Message.RecipientType.BCC, new InternetAddress(bcc[i]));

			}

			msg.saveChanges();
/*
			Transport transport = session.getTransport("smtp");
			transport.connect(host, userName, passWord);
			transport.sendMessage(msg, msg.getAllRecipients());
			transport.close();
*/
			//Transport transport = session.getTransport("smtp");
			//transport.connect();
			//transport.sendMessage(msg, msg.getAllRecipients());
			Transport.send(msg, msg.getAllRecipients());
			//transport.close();
			return true;

		}

		catch (Exception mex)

		{

			mex.printStackTrace();

			return false;

		}

	}

}
