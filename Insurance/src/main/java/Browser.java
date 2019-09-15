import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.Test;

public class Browser {

	@Test
	
	public void test() {
		// TODO Auto-generated method stub
		
		
		System.setProperty("webdriver.chrome.driver", "C:\\Users\\rmanglik\\OneDrive - Itron\\Desktop\\Images\\Katalon_Studio_Windows_64-6.3.3\\configuration\\resources\\drivers\\chromedriver_win32\\chromedriver.exe");
	    
				
		WebDriver driver = new ChromeDriver();
		
		
		driver.get("https://www.google.com/");

	}

}
