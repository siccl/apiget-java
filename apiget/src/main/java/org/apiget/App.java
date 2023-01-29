package org.apiget;

import org.apache.commons.codec.digest.DigestUtils;
import io.github.cdimascio.dotenv.Dotenv;
import java.util.Date;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class App 
{
    // internal properties
    private String APIKEY;
    private String APISECRET;
    private String APIURL;
    private String JWTTOKEN;

    public static void main(String[] args) throws IOException {

        App Obj = new App();
        Obj.readEnv(Obj);
        // generate JWT token
        Obj.JWTTOKEN = Obj.generateJWT(Obj);
        System.out.println(Obj.JWTTOKEN);

        URL url;
        try {

            url = new URL(Obj.APIURL + "?a=" + Obj.GenerateRandomNumber() + "&b=" + Obj.GenerateRandomNumber());
            // print url
            System.out.println(url);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Authorization", "Bearer " + Obj.JWTTOKEN);
            con.setRequestMethod("GET");
            con.setDoOutput(true);
            con.setDoInput(true);
            // read response
            int status = con.getResponseCode();
            System.out.println(status);
            InputStream inputStream = con.getInputStream();
            //System.out.println(con.getResponseMessage());
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            for (int length; (length = inputStream.read(buffer)) != -1; ) {
                result.write(buffer, 0, length);
            }
            // StandardCharsets.UTF_8.name() > JDK 7
            System.out.println(result.toString("UTF-8"));
            con.disconnect();

        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static String sha256hex(String input) {
        return DigestUtils.sha256Hex(input);
    }

    // read .env file
    public void readEnv(App Obj) {
         try {
             Dotenv dotenv = Dotenv.load();
                // read and set TOKEN/APISECRET
                Obj.APIKEY = dotenv.get("APIKEY");
                //Obj.APISECRET = dotenv.get("TOKEN");
                Obj.APISECRET = dotenv.get("TOKEN");
                //get("APISECRET");
                Obj.APIURL = dotenv.get("API");

         } catch (Exception e) {
             System.out.println(e);
         }
    }
    // Generate JWT token
    public String generateJWT(App Obj) {
        // JWT token
        String token = "";
        //SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(Obj.APISECRET));
        SecretKey key = Keys.hmacShaKeyFor(Obj.APISECRET.getBytes(StandardCharsets.UTF_8));
        try {

            token = Jwts.builder()
            .setIssuer(Obj.APIKEY)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + 1800000))
            .signWith(key)
            .compact();

        } catch (Exception e) {
            // show complete error trace
            System.out.println(e);

        }
        return token;
    }
    // generate GenerateRandomNumber method
    public String GenerateRandomNumber() {
        int min = 50; // Minimum value of range
        int max = 100; // Maximum value of range
        int random_int = (int)Math.floor(Math.random() * (max - min + 1) + min);
        return Integer.toString(random_int);
    }
}
