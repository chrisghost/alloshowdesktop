/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package alloshowtv;

import alloshowdesktop.Show;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

/**
 *
 * @author chrissou
 */
public class Search {
    
    public Search() {
    }

    public List<Show> searchTerm(String s){
        //Build parameter string
        String data = "titre="+s;
        List<Show> list = null;
        try {
            int errorCode = 0;
            StringBuffer answer = new StringBuffer();
            do{
                System.out.println("Searching...");
                // Send the request
                URL url = new URL("http://www.alloshowtv.com/series/result.php");
                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);
                OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());

                //write parameters
                writer.write(data);
                writer.flush();

                // Get the response
                answer = new StringBuffer();
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    answer.append(line);
                }
                writer.close();
                reader.close();
                errorCode = answer.toString().indexOf("/home/alloshow/public_html/series/connexion.php");
            }while(errorCode != -1);
            //Output the response
            //System.out.println(answer.toString());

            String[] res = answer.toString().split("<div class=\"credits Style62\">");
            res = res[1].split("table");
            res = res[1].split("<tr");

            for(String element : res){
                String[] t = element.split("\"");
                list.add(populateShow(t[3]));
            }

        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return list;

    }

    private Show populateShow(String string) {
        Show serie = new Show();
        try{
            URL url = new URL("http://www.alloshowtv.com/series/"+string);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);

            // Get the response
            StringBuffer answer = new StringBuffer();
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                answer.append(line);
            }
            reader.close();

            String[] res = answer.toString().split("name=\"aaaa\"/>");

            //IMAGE
            String[] img = res[0].split("src=\"");
            img = img[img.length-1].split("\"");
            serie.setImage(img[0]);
            //IMAGE

            //TITLE
            String[] title = res[1].split("class=\"b\"");
            title = title[1].split(">");
            title = title[1].split("<");
            serie.setName(title[0]);
            //TITLE

        }catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return serie;
    }

}
