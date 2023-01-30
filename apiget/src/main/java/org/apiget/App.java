package org.apiget;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

public class App 
{
    private String APIKEY;
    private String APISECRET;
    private String APIURL;
    private String JWTTOKEN;
    // main method
    // read .env file
    // generate JWT token
    // call API
    // print response
    // some debug info
    public static void main(String[] args) throws IOException {

        App Obj = new App();
        Obj.readEnv(Obj);
        Obj.JWTTOKEN = Obj.generateJWT(Obj);
        System.out.println(Obj.JWTTOKEN);
        URL url;
        try {
            url = new URL(Obj.APIURL + "?a=" + Obj.GenerateRandomNumber() + "&b=" + Obj.GenerateRandomNumber());
            System.out.println(url);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Authorization", "Bearer " + Obj.JWTTOKEN);
            con.setRequestMethod("GET");
            con.setDoOutput(true);
            con.setDoInput(true);
            int status = con.getResponseCode();
            System.out.println(status);
            InputStream inputStream = con.getInputStream();
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            for (int length; (length = inputStream.read(buffer)) != -1; ) {
                result.write(buffer, 0, length);
            }
            System.out.println(result.toString("UTF-8"));
            con.disconnect();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
    // read .env file
    public void readEnv(App Obj) {
         try {
             Dotenv dotenv = Dotenv.load();
                Obj.APIKEY = dotenv.get("APIKEY");
                Obj.APISECRET = dotenv.get("TOKEN");
                Obj.APIURL = dotenv.get("API");

         } catch (Exception e) {
             System.out.println(e);
         }
    }
    // Generate JWT token
    public String generateJWT(App Obj) {
        String token = "";
        SecretKey key = Keys.hmacShaKeyFor(Obj.APISECRET.getBytes(StandardCharsets.UTF_8));
        try {
            token = Jwts.builder()
            .setIssuer(Obj.APIKEY)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + 1800000))
            .signWith(key)
            .compact();

        } catch (Exception e) {
            System.out.println(e);
        }
        return token;
    }
    // Generate random number between 1 and 100
    public String GenerateRandomNumber() {
        int min = 1;
        int max = 100;
        int random_int = (int)Math.floor(Math.random() * (max - min + 1) + min);
        return Integer.toString(random_int);
    }
}
