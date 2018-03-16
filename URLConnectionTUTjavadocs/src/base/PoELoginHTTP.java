package base;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

/** https://www.mkyong.com/java/how-to-automate-login-a-website-java-example/
 * */

/** The procedure:
 *  Send a HTTP GET request to the login site.
 *  Use JSoup library to extract all visible and hidden form's data, replace with username and password. (TODO: UPGRADE TO USE COOKIE?)
 *  Send a HTTP POST request back to login form, along with contructed parameters.
 *  After user authenticated, send another HTTP GET request to the account page.
 * */

public class PoELoginHTTP {

    private List<String> cookies;
    private HttpsURLConnection conn;

    private final String USER_AGENT = "Mozilla/5.0";

    public static void main(String[] args) throws Exception{

        String loginURL = "https://www.pathofexile.com/login";
        String poeAccountURL = "https://www.pathofexile.com/my-account"; //TODO: This info can be found in the HTTP get response?!

        PoELoginHTTP http = new PoELoginHTTP();

        //Make sure cookies is turned on
        CookieHandler.setDefault(new CookieManager());

        //1. Send a GET request, so that you can extract the form's data.
        String page = http.GetPageContent(loginURL);
        //String postParams = http.getFormParams(page, "username@hotmail.com", "password"); //TODO: might need to be my inf
        String postParams = http.getFakedFormParams(page, "flintfedt-trash@hotmail.com", "1937mikkel"); //TODO: might need to be my inf

        System.out.println("PARAMS:");
        System.out.println(postParams); //TODO: TEMP
        //2. Construct above post's content and then send a POST request for authentication
        http.sendPost(loginURL, postParams);

        //TODO: THIS IS WHERE TO CONTINUE! THE SENDPOST NEEDS IMPLEMENTAION

    }

    private String GetPageContent(String url) throws Exception {

        URL urlObj = new URL(url);
        conn = (HttpsURLConnection) urlObj.openConnection();

        //Default is GET
        conn.setRequestMethod("GET");

        conn.setUseCaches(false);

        //Act like a browser (ALL THIS DATA GET BE FOUND IN THE CHROME -> INSPECT -> NETWORK)
        conn.setRequestProperty("User-Agent", USER_AGENT);
        conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        conn.setRequestProperty("Accept-Language", "en-US,en;q=0.9,da;q=0.8,nb;q=0.7,sv;q=0.6,zh-CN;q=0.5,zh;q=0.4");
        if(cookies != null){
            for(String cookie : this.cookies){
                conn.addRequestProperty("Cookie", cookie.split(";", 1)[0]);
            }
        }
        int responseCode = conn.getResponseCode();
        System.out.println("\nSending GET request to url : " + url);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null){
            response.append(inputLine);
        }
        in.close();

        //Get the response cookies
        setCookies(conn.getHeaderFields().get("Set-Cookie"));

        return response.toString();
    }

    //Not working //Half is fixed
    public String getFormParams(String html, String username, String password) throws UnsupportedEncodingException{

        System.out.println("Extracting form's data...");

        Document doc = Jsoup.parse(html);

        //PoE form id //TODO: Also found with the help from chrome -> inspect
        Element loginform = doc.getElementById("login");
        Elements inputElements = loginform.getElementsByTag("input");

        System.out.println("inputElements: ");//TODO:TEMP
        System.out.println(inputElements.toString());//TODO:TEMP
        System.out.println("DONE");

        List<String> paramList = new ArrayList<String>();
        for(Element inputElement : inputElements){
            String key = inputElement.attr("login_email"); //TODO: CHECKED
            String value = inputElement.attr("login_password"); //TODO: CHECKED

            System.out.println("--------------------");
            System.out.print(key); //TEMP
            System.out.println(" " + value); //TEMP
            System.out.println("--------------------");

            if(key.equals("login_email"))
                value = username;
            else if(key.equals("login_password"))
                value = password;
            paramList.add(key + "=" + URLEncoder.encode(value, "UTF-8"));
        }

        //Build parameters list
        StringBuilder result = new StringBuilder();
        for(String param : paramList){
            if(result.length() == 0){
                result.append(param);
            } else{
                result.append("&" + param);
            }
        }

        return result.toString();
    }

    public String getFakedFormParams(String html, String username, String password) throws UnsupportedEncodingException{

        System.out.println("Faking login data...");

        List<String> paramList = new ArrayList<String>();

        paramList.add("login_email" + "=" + URLEncoder.encode(username, "UTF-8"));
        paramList.add("login_password" + "=" + URLEncoder.encode(password, "UTF-8"));
        //TODO MORE ELEMENTS TO BE ADDED?
        "login_email=flintfedt-trash%40hotmail.com&login_password=1937mikkel&remember_me=0&hash=0c9eaf305aa57274d24443a704d152c3&login=Login"

        //Build parameters list
        StringBuilder result = new StringBuilder();
        for(String param : paramList){
            if(result.length() == 0){
                result.append(param);
            } else{
                result.append("&" + param);
            }
        }

        return result.toString();
    }

    private void sendPost(String url, String postParams) throws Exception{
        //TODO: IMPLEMENTATION
    }

    public List<String> getCookies(){
        return cookies;
    }

    public void setCookies(List<String> cookies){
        this.cookies = cookies;
    }
}
