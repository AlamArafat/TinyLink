package com.example.UrlShortner;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Random;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class UrlShortner {
	@GetMapping("/hi")
	public String gotoMainWeb() throws Exception {
		return "Hello";
		
	}
	
	// ---------------------------URL shortner---------------

		@GetMapping("/api/shorturl")
		public String shortlongUrl(String longUrl, String customUrl) throws Exception {
			String newUrl="";
			String checkUrl="";
			Class.forName("com.mysql.cj.jdbc.Driver");  // load Driver
				
			Connection connect = DriverManager.getConnection("jdbc:mysql://localhost:3306/project", "root" , "aalam"); // connection established

			if(customUrl == null || customUrl.isEmpty()) {
				while(true) {
				  newUrl = createNewUrl(6);
				  PreparedStatement stmt = connect.prepareStatement("SELECT * FROM URLS WHERE shortUrl = ?"); // If shortUrl already given in database then we will get Error.
				  stmt.setString(1, newUrl);
				  ResultSet rs = stmt.executeQuery();
				  if(rs.next()) {
					String chk = rs.getString("shortUrl");
					if(chk.equals(newUrl)) {
						continue;
					}
				  }else {
						PreparedStatement stmt1 = connect.prepareStatement("INSERT INTO URLS (longUrl, shortUrl) VALUES ( ? , ? )"); // If shortUrl already given in database then we will get Error.
						stmt1.setString(1, longUrl);
						stmt1.setString(2, newUrl);
						int i = stmt1.executeUpdate();
						if(i > 0) {
							return "Your new short URL is: "+ newUrl;
						}
					}
			   }
			}else {
				PreparedStatement stmt = connect.prepareStatement("SELECT * FROM URLS WHERE shortUrl = ?");
				stmt.setString(1, customUrl);
				ResultSet rs = stmt.executeQuery();
				
				while(rs.next()) {
					checkUrl = rs.getString("shortUrl");
					System.out.println("checking Url..."+ checkUrl);
					if(checkUrl.equals(customUrl)) {
						return "This String is Already used!";
					}		
				}
				if(checkUrl!=customUrl) {
					PreparedStatement stmt1 = connect.prepareStatement("insert into urls values (? , ?)");
					stmt1.setString(1, longUrl);
					stmt1.setString(2, customUrl);
					int i = stmt1.executeUpdate();
					if(i > 0)
						return "Your new Short URL is: "+ customUrl;
				}
			}
			return null;
		}

			private String createNewUrl(int targetStringLength) {

				int leftLimit = 48; // numeral '0'
			    int rightLimit = 122; // letter 'z'
			    Random random = new Random();

			    String generatedString = random.ints(leftLimit, rightLimit + 1)
			      .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
			      .limit(targetStringLength)
			      .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
			      .toString();

			    System.out.println("GeneratedString: "+ generatedString);
				return generatedString;
			}
	
	
	@GetMapping("/{url}")
	public ModelAndView gotoMainWeb(@PathVariable("url") String url) throws Exception {
		Class.forName("com.mysql.cj.jdbc.Driver");  // load Driver
			
		Connection connect = DriverManager.getConnection("jdbc:mysql://localhost:3306/project", "root" , "aalam"); // connection established
		PreparedStatement stmt = connect.prepareStatement("SELECT * FROM URLS WHERE shortUrl = ?"); // If shortUrl already given in database then we will get Error.
		  stmt.setString(1, url);
		  ResultSet rs = stmt.executeQuery();
		  
		  while(rs.next()) {
			  String pwd = rs.getString("shortUrl");
			    System.out.println("ShortURL + CHK: "+ pwd + " "+ url);
				if(pwd.equals(url)) {
					String longUrl = rs.getString("longUrl");
					return new ModelAndView("redirect:"+longUrl);
				}
			  }
			
		return null;
	}
}
