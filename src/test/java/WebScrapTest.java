import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bson.Document;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import io.github.bonigarcia.wdm.WebDriverManager;


public class WebScrapTest {
	
	WebDriver driver;
	MongoCollection<Document> webCollection;
	@BeforeSuite
	public void connectMongoDB() {
		
		Logger mongoLogger =Logger.getLogger("org.mongodb.driver");
		
		MongoClient mongoclient =MongoClients.create("mongodb://127.0.0.1:27017/");
		MongoDatabase database =mongoclient.getDatabase("autoDB");
		//create collection
		webCollection=database.getCollection("web");
	}
	
	@BeforeTest
	public void setUp() {
		WebDriverManager.chromedriver().setup();
		ChromeOptions co =new ChromeOptions();
		co.addArguments("--headless");
		driver=new ChromeDriver(co);
	}
	
	
	@DataProvider
	public Object[][] getData(){
		return new Object[][] {
			{"http://www.amazon.com"},
			{"https://www.snapdeal.com/"}
		};
	}
	
	@Test(dataProvider = "getData")
	public void webScrapeTest(String appurl) {
		driver.get(appurl);
		String url=driver.getCurrentUrl();
		String title=driver.getTitle();
		Document metadata=new Document();
		metadata.append("url", url);
		metadata.append("title", title);
		
		List<WebElement> linklist=driver.findElements(By.tagName("a"));
		List<String> linkhref=new ArrayList<String>();
		for (WebElement webElement : linklist) {
			linkhref.add(webElement.getAttribute("href"));
		}
		metadata.append("linkcount",linklist.size());
		metadata.append("linkattribute",linkhref);
		linklist=driver.findElements(By.tagName("img"));
		linkhref.clear();
		for (WebElement webElement : linklist) {
			linkhref.add(webElement.getAttribute("src"));
		}
		metadata.append("imagecount",linklist.size());
		metadata.append("imgscr",linkhref);
		
		
		List<Document> docsList =new ArrayList<Document>();
		docsList.add(metadata);
		webCollection.insertMany(docsList);
		
	}

}
