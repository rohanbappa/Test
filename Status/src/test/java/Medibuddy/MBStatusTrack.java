package Medibuddy;

import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

import org.testng.annotations.BeforeMethod;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.swing.JOptionPane;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;

public class MBStatusTrack {
	 WebDriverWait wait = null;
		WebDriver driver = null;
		WebElement element = null;
		By ele = null;

	String status = "", strDate = "";
	String os;
	List<String> claimid = new ArrayList<String>();
	private int numberOfFiles;
	String url = "https://vnusoftware.com/iclaimportal/api/preauth";
  @Test
  public void mbstatustrack() {
	 
	    String id = "", m ="", hname ="", hid = "LCBP-2009-00337", fileAddress = "", response ="";
	    String PAuth ="",PApprovedAmount="", PAStatus="";
	    int i, k, i1, i2; String j, preauthid; List<WebElement>  rows;
	    String hospitalList[] = {"U74900PN2012PTC144088","LCBP-2009-00337","U74999PN2015PTC156635"};
  	int hListLength = hospitalList.length;
  	int b,q = 0;
  	if(System.getProperty("user.name").contains("LAPTOP-MI1R02SA")) {
  		System.setProperty("user.name", "91871");
  	};
	    JsonNode value = null, actualObj = null, preauth = null, claim = null; int data = 0, a = 0;
	    do {
	    	a= a+1;
	    	b=(a-1)%hListLength;
	    	hid = hospitalList[b];
	    	if(b==0 && a>1) { //1 full round of all portals have happened..so, rest time
	    		log("Going to sleep for 10 minutes");
	    		q++;
	    		System.out.println("Going to sleep for 10 minutes at"+q+"time");
	    		try {
	    			Thread.sleep(600000);
	    		} catch (InterruptedException e) {
	    			// TODO Auto-generated catch block
	    			e.printStackTrace();
	    		}
	    	}
	    RequestSpecification requestspc;
		requestspc = RestAssured.given();
		requestspc.header("Content-Type", "multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW");
	//    requestspc.multiPart("sdate", "15/09/2019");
		requestspc.multiPart("status", "In Progress");
		requestspc.multiPart("insid", "3"); //3 for MB
		requestspc.multiPart("hid", hid); //need to change after every run
	//	requestspc.multiPart("process", "Cl");
		//U74900PN2012PTC144088 - CCH LCBP-2009-00337 - IH   U74999PN2015PTC156635-RM
		io.restassured.response.Response response1 = requestspc.post(url);
		
		ObjectMapper mapper = new ObjectMapper();

		try {
			actualObj = mapper.readTree(response1.asString());
			if(actualObj != null) {
			preauth = actualObj.get("preauth");
			claim = actualObj.get("Claim");
			if(preauth != null) {
				
			}else {
				log("No preauth exist for MB for hospital-"+hid+" at time" +java.time.LocalTime.now().toString());
			}
			if(claim != null){
				
			} else {
				log("No claim exist for MB for hospital-"+hid+" at time" +java.time.LocalTime.now().toString());
				}
			}
			else {
				log("Nothing to track for MB thread for hospital "+hid+" at time "+java.time.LocalTime.now().toString());
			}
		}catch(Exception e) {
			log("Exception occured in reading results of API for MB thread for hospital"+hid);
		}
		// check if preauth or claim exist for this hospital
				if(preauth != null || claim != null) {
	         
		//login	because something from above exist	
	         if(preauth != null) {
					data = preauth.get("pa_count").asInt(); i=0;
					for(i = 0; i < data; i++)
					{ 
						j = Integer.toString(i);
						value = preauth.get(j);
						if(value != null) break;
					}
				}
				if(claim != null && value == null) {
					i1 = claim.get("claim_count").asInt(); k=0;
					for(k = 0; k < i1; k++)
					{ 
						j = Integer.toString(k);
						value = claim.get(j);
					}}
		//launch in headless mode				
			     	try {		
			     		os = System.getProperty("os.name").toLowerCase();
					if(os.contains("mac")) {
						//	System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir")+"/chromedriver");
						}else {
						System.setProperty("webdriver.chrome.driver", "C:\\Users\\" + System.getProperty("user.name") + "\\Documents\\chromedriver.exe");
						}
					    	ChromeOptions options = new ChromeOptions();
					        options.addArguments("headless");
				        System.out.println(System.getProperty("user.name"));
					    System.out.println(os);
						driver = new ChromeDriver(options);

						driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
						log("Chrome Driver launched in Headless mode");
						driver.get("https://plus.medibuddy.in/");
						driver.manage().window().maximize();
						wait = new WebDriverWait(driver, 20);			
			     	} catch (Exception e) {
			     		e.printStackTrace();
						driver.navigate().refresh();
						driver.manage().window().maximize();
						JOptionPane.showMessageDialog(null, e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
						log("Chrome Driver failed to launch in Headless mode");
						driver.quit();
					}
			     // login with hospital credentials
					try {
						String username = value.get("PortalUser").asText();
						String password = value.get("PortalPass").asText();				     	
						// Enter email or Username
						element = driver.findElement(By.xpath("//input[@placeholder='Username or Email']"));

						element.sendKeys(username);

						// Enter password
						element = driver.findElement(By.xpath("//input[@placeholder='Password']"));
						element.sendKeys(password);

						// Click signin
						element = driver.findElement(By.xpath("//button[text()='Sign In']"));
						element.click();

						// Wait to login and wait unitl newadmission buttton is found
						try {
							element = driver.findElement(By.xpath("//*[@id=\'ngdialog1\']/div[2]/button"));
							element.click();

						} catch (Exception e) {
							log("MB first screen issue");
						}
						ele = By.xpath("/html/body/div[1]/aside/ul/li[8]/a");
						wait.until(ExpectedConditions.presenceOfElementLocated(ele));
					} catch (Exception e) {
						log("MB login script failed");
					}     	
//loop all claim ids
					i1 = 0;
					i1 = claimid.size(); k=0;
//for(k = 0; k < i1; k++){
					//before that, get values of hospital and insurer
					if(preauth != null) {
						data = preauth.get("pa_count").asInt(); i=0;
						for(i = 0; i < data; i++)
						{ 
							j = Integer.toString(i);
							value = preauth.get(j);
							id = value.get("RefNo").asText();
							preauthid = value.get("Preauth").asText();
							if(preauthid.equals("0")) {
								preauthid = value.get("claimid").asText();
							}
							//claimid.add(i, preauthid);
							searchmb(driver, id, preauthid, "Pa", i);
						}
					} //preauth finished
					if(claim != null) {
						i1 = claim.get("claim_count").asInt(); k=0;
						for(k = 0; k < i1; k++)
						{ 
							j = Integer.toString(k);
							value = claim.get(j);
							id = value.get("RefNo").asText();
							preauthid = value.get("Preauth").asText();
							if(preauthid.equals("0")) {
								preauthid = value.get("claimid").asText();
							}
							//claimid.add(data +k, preauthid);
							searchmb(driver, id, preauthid, "Cl", k+data);
						}
					} //claim finished
					driver.close();
	    }
				}while(a != 100);
	    
 }

	private void searchmb(WebDriver driver, String id, String preauthid, String type, Integer ik) {
//		Search Option Click:
		String pa_status ="";
		try {
			if(ik==0) {
  		element = driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Dashboard'])[2]/following::select[1]"));//sendKeys("Claim No");
  		element.click();
  		element.sendKeys(Keys.ARROW_DOWN);
  		element.sendKeys(Keys.ENTER);
			}else {
			element =	driver.findElement(By.xpath("//input[@id='claimSearchFormClaimNo']"));
			element.sendKeys(Keys.CONTROL + "a");
			element.sendKeys(Keys.DELETE);
			}
		}catch(Exception e) {}
  		//Enter Value:
  		ele = By.xpath("//input[@id='claimSearchFormClaimNo']");
		wait.until(ExpectedConditions.presenceOfElementLocated(ele));
	
        driver.findElement(By.xpath("//input[@id='claimSearchFormClaimNo']")).sendKeys(preauthid);
//Search Button
        driver.findElement(By.xpath("/html/body/div[1]/section/header/div[1]/div[1]/div/div[2]/form[1]/div[2]/div[1]/span/button[1]")).click();
//get value
        String pa_claim = "";
        do {
         pa_claim = driver.findElement(By.xpath("/html/body/div[1]/section/section/div/div/div[1]/angular-tab/div[1]/div[1]/div/div/div[1]/table/tbody/tr/td[2]")).getText();      
        System.out.println(pa_claim);
       }while(!pa_claim.equals(preauthid));
        if(pa_claim.equals(preauthid)) {
        	String pa_name = driver.findElement(By.xpath("/html/body/div[1]/section/section/div/div/div[1]/angular-tab/div[1]/div[1]/div/div/div[1]/table/tbody/tr/td[1]")).getText();
            System.out.println(pa_name);
        	 pa_status = driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='"+pa_name+"'])[1]/following::td[5]")).getText();
            System.out.println(pa_status);
            if(pa_status.contains("Processed") || pa_status.contains("Awaiting") || pa_status.contains("Denied")) 
            {  	      
        String pa_clamnt = driver.findElement(By.xpath("/html/body/div[1]/section/section/div/div/div[1]/angular-tab/div[1]/div[1]/div/div/div[1]/table/tbody/tr/td[3]")).getText();
        System.out.println(pa_clamnt);
        String pa_apamnt = driver.findElement(By.xpath("/html/body/div[1]/section/section/div/div/div[1]/angular-tab/div[1]/div[1]/div/div/div[1]/table/tbody/tr/td[4]")).getText();
        System.out.println(pa_apamnt);
        
        
        
        driver.findElement(By.xpath("/html/body/div[1]/section/section/div/div/div[1]/angular-tab/div[1]/div[1]/div/div/div[1]/table/tbody/tr/td[1]")).click();
        int i = 0;String url = null, urltext="", m="", d =""; claimid.clear();
        switch(type) {
        case "Pa":
        	do { 
        		url = "";
        		try {
        			if(pa_status.contains("Processed")) {
        		url =  driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Missing documents'])["+i+"]/following::a[1]")).getAttribute("data-letterdownloadlink");
        		urltext =  driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Missing documents'])["+i+"]/following::a[1]")).getText();
        		if(!urltext.contains("Pre-Auth")) {url = "";}
        		if(!url.equals(null) && !url.equals("") ) {
        			  log("link of letter of PA "+preauthid+":- "+url);
        			  status = "Approved";
        	        	//get response time
        		      d = driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Missing documents'])["+i+"]/following::p[1]")).getText();
        		       if(!d.equals("")) {
        		    	   System.out.println(d);
        		    	   String test = url+"  "+d;
            		    	 claimid.add(test);
        			         break; 
        		       }
        			  }}
        			if(pa_status.contains("Awaiting")) {
        				url =  driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Missing documents'])["+i+"]/following::a[1]")).getAttribute("data-letterdownloadlink");
        				urltext =  driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Missing documents'])["+i+"]/following::a[1]")).getText();
                		if(!urltext.contains("Await")) {url = "";}
        				if(!url.equals(null) && !url.equals("")) {
              			  log("link of letter of PA "+preauthid+":- "+url);
              			  status = "Information Awaiting";
          	        	//get response time
          		       d = driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Missing documents'])["+i+"]/following::p[1]")).getText();
          		       if(!d.equals("")) {
          		    	   System.out.println(d);
          		    	 String test = url+"  "+d;
        		    	 claimid.add(test);
          		       }
              			  }}
        			if(pa_status.contains("Denied")) {
        				url =  driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Missing documents'])["+i+"]/following::a[1]")).getAttribute("data-letterdownloadlink");
        				urltext =  driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Missing documents'])["+i+"]/following::a[1]")).getText();
                		if(!urltext.contains("Denied")) {url = "";}
        				if(!url.equals(null) && !url.equals("")) {
              			  log("link of letter of PA "+preauthid+":- "+url);
              			  status = "Rejected";
          	        	//get response time
          		        d = driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Missing documents'])["+i+"]/following::p[1]")).getText();
          		       if(!d.equals("")) {
          		    	   System.out.println(d);
          		    	 String test = url+"  "+d;
          		    	 claimid.add(test);
          		    	 break;
          		       }
              			  }}
        		}catch(Exception e) {
        			//do nothing..keep checking
        			//e.printStackTrace();
        		}
        		i++;
        	}while(i != 20);
        	break;
        case "Cl":
        	do {
        		url ="";
        		try {
        			if(pa_status.contains("Processed")) {
        		url =  driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Missing documents'])["+i+"]/following::a[1]")).getAttribute("data-letterdownloadlink");
        		urltext =  driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Missing documents'])["+i+"]/following::a[1]")).getText();
        		if(!urltext.contains("Enhance")) {url = "";} 
        		if(!url.equals(null) && !url.equals("")) {
        			  log("link of letter of Cl "+preauthid+":- "+url);
        			  status = "Approved";
      	        	//get response time
      		        d = driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Missing documents'])["+i+"]/following::p[1]")).getText();
      		       if(!d.equals("")) {
      		    	   System.out.println(d);
      		    	 String test = url+"  "+d;
    		    	 claimid.add(test);
      		    	 break;
      		       }
        			  }}
        			if(pa_status.contains("Awaiting")) {
        				url =  driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Missing documents'])["+i+"]/following::a[1]")).getAttribute("data-letterdownloadlink");
        				urltext =  driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Missing documents'])["+i+"]/following::a[1]")).getText();
                		if(!urltext.contains("Await")) {url = "";}
        				if(!url.equals(null) && !url.equals("")) {
              			  log("link of letter of Cl "+preauthid+":- "+url);
              			status = "Information Awaiting";
        	        	//get response time
        		       d = driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Missing documents'])["+i+"]/following::p[1]")).getText();
        		       if(!d.equals("")) {
        		    	   System.out.println(d);
        		    	   String test = url+"  "+d;
          		    	 claimid.add(test);
        		       }
              			  }}
        			if(pa_status.contains("Denied")) {
        				url =  driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Missing documents'])["+i+"]/following::a[1]")).getAttribute("data-letterdownloadlink");
        				urltext =  driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Missing documents'])["+i+"]/following::a[1]")).getText();
                		if(!urltext.contains("Enhancement Denied")) {url = "";}
        				if(!url.equals(null) && !url.equals("")) {
              			  log("link of letter of Cl "+preauthid+":- "+url);
              			 status = "Rejected";
         	        	//get response time
         		         d = driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Missing documents'])["+i+"]/following::p[1]")).getText();
         		       if(!d.equals("")) {
         		    	   System.out.println(d);
         		    	  String test = url+"  "+d;
         		    	 claimid.add(test);
         		    	  break;
         		       }
              			  }}
        		}catch(Exception e) {
        			//do nothing..keep checking
        		}
        		i++;
        	}while(i != 20);
        	break;
        default:
        	break;
        }
        url ="";
        if(claimid.size()==0)  {
        	log("claim status has changed- " +id+"-"+preauthid+ " status- "+pa_status+" at time- "+java.time.LocalTime.now().toString()+", but not able to get URL");            
        	String message = "no url, but status has changed for MB "+id;
        	smsmb(message, "9967044874");
        }
        else {  
      //fetch last url from claimid array with its d:-
        String[] urld = null;
        url = claimid.get(claimid.size()-1);
          urld = url.split("  ");
          url = urld[0]; 
          d = urld[1];
          if(!d.equals("")) {
     		 System.out.println(d);
    		 String[] splited2 = null;
    		  String[] splited = d.split("\n");
    		  d = splited[0];
    		  String year = "2019";//response.substring(response.lastIndexOf("/") + 1);
    		  String month = splited[0].substring(splited[0].indexOf(" ") + 1);
    		  SimpleDateFormat inputFormat = new SimpleDateFormat("MMM");
    			Calendar cal = Calendar.getInstance();
    			try {
    				cal.setTime(inputFormat.parse(month));
    			} catch (ParseException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
    			SimpleDateFormat outputFormat = new SimpleDateFormat("MM"); // 01-12
    			 m = outputFormat.format(cal.getTime());
    			System.out.println(m);
    		  String day = d.substring(0, d.indexOf(" "));
    		  splited2 = splited[1].split(" ");
    	    		if(splited2[1].equals("pm")) {
    	    			splited2 = splited[1].split(":");
    	    			if(!splited2[0].equals("12")) {
    	    				int p = Integer.parseInt(splited2[0]);
    	    			splited2[0] = Integer.toString(p+12);
    	    			}
    	    			
    	    			String[] splited3 = splited2[1].split(" ");
    		    		m = day+"/"+m+"/"+year+" "+splited2[0]+":"+splited3[0]+":00";
    	    		}else {
    	    		m = day+"/"+m+"/"+year+" "+splited2[0]+":00";}
    		  System.out.println(m);
    		//  System.out.println(System.getProperty("user.name"));
    		
    	}
          
        	//download the URL
        	BufferedInputStream in;
			try {
				in = new BufferedInputStream(new URL(url).openStream());
	
            FileOutputStream fileOutputStream = new FileOutputStream(new File("C:\\Users\\" + System.getProperty("user.name") + "\\Documents\\MB\\"+pa_status+id+".pdf" ));
			
			byte dataBuffer[] = new byte[1024];
			int bytesRead;
			while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
				fileOutputStream.write(dataBuffer, 0, bytesRead);

			}
			fileOutputStream.close();	
			log("successfully downloaded file "+id);
			// now call update API.	
			if(!m.equals("")) {
			if(!status.equals("") && !status.equals(null)) {
			RequestSpecification requestspc;
			 requestspc = RestAssured.given();
			  requestspc.header("Content-Type", "multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW");
			  requestspc.multiPart("status", status);
			  requestspc.multiPart("doc", new File("C:\\Users\\" + System.getProperty("user.name") + "\\Documents\\MB\\"+pa_status+id+".pdf")); 
			  requestspc.multiPart("lettertime", m);
			  requestspc.multiPart("refno", id); 
			  requestspc.multiPart("process", type);
			  if(status.equals("Approved")) {
				  requestspc.multiPart("amount", pa_apamnt);
				  }
			  log(id+", "+m+" , "+type);
			  io.restassured.response.Response response1 = requestspc.post(url);
			  log("Status updated for" +id+ "as-" +status+ "at time- "+java.time.LocalTime.now().toString());
			}
			else {
				log("changed status is coming blank, so API not called; - "+id);
			}
			}else {
				log("changed time is coming blank, so API not called; - "+id);
			}
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		  }
//close the tab
        driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='New Notification'])[1]/preceding::i[1]")).click();
        		
        }else {
            	log("claim is in process- " +preauthid+ " status- "+pa_status+" at time- "+java.time.LocalTime.now().toString());
            }
            }
        else{
        	log("claim id doesn't match/found: " +preauthid+"- "+id);
        	}
    } 


	public void log(String log) {
		File file = new File("C:\\Users\\" + System.getProperty("user.name") + "\\Documents\\mblog.txt");
		FileWriter fw;

		try {
		if (file.exists()) {
		fw = new FileWriter(file, true);
		fw.write(log+System.getProperty("line.separator"));
		fw.close();
		} else {
		fw = new FileWriter(file);
		fw.write(log);
		fw.close();
		}
		} catch (Exception e) {

		}
		}
	public  void smsmb(String message, String mobile){
		HttpResponse<String> response = Unirest.post("https://control.msg91.com/api/postsms.php")
				  .header("content-type", "application/xml")
				  .body("<MESSAGE>"
				  		+ "<AUTHKEY>167826ARvnR1lKl5cee8065</AUTHKEY>"
				  		+ "<ROUTE>4</ROUTE>"
				  		+ "<COUNTRY>91</COUNTRY>"
				  		+ "<SENDER>iClaim</SENDER>"
				  		+ "<SMS TEXT="+message+">"
				  		+ " <ADDRESS TO="+mobile+"></ADDRESS>"
				  		+ "</SMS> "
				  		+ "</MESSAGE>")
				  .asString();
		log("SMS for Star sent to-"+mobile+"-"+message);
	} 	
  @BeforeMethod
  public void beforeMethod() {
  }

  @AfterMethod
  public void afterMethod() {
  }

}
