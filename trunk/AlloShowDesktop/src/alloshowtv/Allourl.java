/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package alloshowtv;

import alloshowdesktop.ASDgui;
import alloshowdesktop.Episode;
import alloshowdesktop.Main;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author chrissou
 */
public class Allourl implements Runnable {

    private List<Episode> list = new ArrayList<Episode>();
    private Main main = null;

    public Allourl(List<Episode> list, Main main) {
        this.main = main;
        this.list = list;
    }



    public String getLink(String u){
        String first_page = getPageContent(u, null);
        String url = first_page.split("iframe src=\"")[1].split("\"")[0];
        String second_page = getPageContent("http://www.allourl.com/"+url , null);
        String k = second_page.split("var k=\"")[1].split("\"")[0];
        
        try {
            System.out.print("Wait...");
            Thread.sleep(6000);
            System.out.println("ok");
        } catch (InterruptedException ex) {
            Logger.getLogger(Allourl.class.getName()).log(Level.SEVERE, null, ex);
        }

        String securpage = "";
        if(u.contains("?w="))
            securpage = "secur.php";
        else if(u.contains("?u="))
            securpage = "allosecure.php";
        
        String res = getPageContent("http://www.allourl.com/"+securpage,"k="+k,
                "application/x-www-form-urlencoded",
                "Mozilla/5.0 (X11; U; Linux x86_64; fr; rv:1.9.2.8) Gecko/20100723 Ubuntu/10.04 (lucid) Firefox/3.6.8",
                "http://www.allourl.com/"+url);

        System.out.println("http://www.allourl.com/"+url);
        System.out.println(res);

        String link = "";

        if(!res.contains("Error") && !res.contains("Link Expired"))
            link = res.split("href=\"")[1].split("\"")[0];
        
        return link;
    }


    public String getPageContent(String u, String data){
        return this.getPageContent(u, data, "", "", "");
    }

    public String getPageContent(String u, String data, String header, String user_agent, String referer){
        try{
            if(data == null){
                data = "";
            }

            System.setProperty("http.agent", "");

            URL url = new URL(u);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", header);
            conn.setRequestProperty("User-Agent", user_agent);
            conn.setRequestProperty("Referer", referer);
            conn.setRequestProperty("Cookie", "79b2_unique_user=1");

            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.flush();
            
            // Get the response
            StringBuffer answer = new StringBuffer();
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                answer.append(line);
            }
            wr.close();
            reader.close();
            return answer.toString();
        }catch(MalformedURLException e){
            
        }catch(IOException e){
            
        }
        return "";
    }

    public void run() {
        for(int i = 0;i<this.list.size();i++){
            this.main.setAlloUrlLinksLabel(i, true);
            this.list.get(i).setDirect_url(this.getLink(this.list.get(i).getDirect_url()));
        }
    }
    public void launch() {
        Thread thread = new Thread(this);
        thread.start();
    }
}
