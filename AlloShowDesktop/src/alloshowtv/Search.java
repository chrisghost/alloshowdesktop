/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package alloshowtv;

import alloshowdesktop.Episode;
import alloshowdesktop.Season;
import alloshowdesktop.Show;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author chrissou
 */
public class Search extends alloshowdesktop.Search implements Runnable {
    
    private String search;

    public Search(String s) {
        this.search = s;
        //search();
    }


    public List<Show> searchTerm(){
        //Build parameter string
        String data = "titre="+this.search+"&rechee01=Recherche";
        List<Show> list = new ArrayList<Show>();
        try {
            int errorCode = 0;
            StringBuffer answer = new StringBuffer();
            do{
                System.out.println("Searching...");
                this.state("sending request");

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

            //for(String element : res){

            this.state("listing");

            for(int i=1;i<res.length;i++){
                String[] t = res[i].split("href");
                t = t[1].split("\"");

                list.add(populateShow(t[1]));
            }

        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return list;

    }

    @SuppressWarnings("empty-statement")
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

            String res = answer.toString();

            //TITLE
            String[] title = res.split("<table width=\"100%\" border=\"0\" align=\"center\">");
            title = title[1].split("</a>");
            title = title[0].split(">");
            serie.setName(title[title.length-1]);
            //TITLE

            this.state("adding "+title[title.length-1]);

            String[] seasons = res.split("Saison ");

            System.out.println("length : "+seasons.length);

            
            for(int i=1;i<seasons.length;i++){
                String[] n = seasons[i].split("<");
                int season_n = Integer.parseInt(n[0]);

                Season new_season = new Season("Saison "+season_n, season_n);

                System.out.println("Saison "+season_n);


                String[] episodes = seasons[i].split("<tr");
                for(int j=1;j<episodes.length-1;j++){
                    String cur = episodes[j];
                    String[] link = cur.split("href=");
                    link = link[1].split(" ");
                    //lien = link[0]


                    String[] e_num = cur.split("pisode ");
                    int num = 0;
                    try{
                        e_num = e_num[1].split("<");
                        e_num = e_num[0].split(" ");

                        num = Integer.parseInt(e_num[0]);
                    }catch(NumberFormatException e){
                        System.out.println("Can't read integer from "+e_num[0]);
                    }catch(ArrayIndexOutOfBoundsException e){
                        System.out.println("Out of bounds");
                    }

                    String[] host = cur.split("width=\"19%\" class=\"");

                    host = host[1].split("\"");
                    //host = host[0]

                    String[] lang = cur.split("width=\"14%\"");
                    lang = lang[1].split("<br>");
                    String lang_final = "";
                    
                    if(lang[0].substring(lang[0].length()-4, lang[0].length()).equalsIgnoreCase("V.F.")){
                        lang_final = "V.F.";
                    }else if(lang[0].substring(lang[0].length()-6, lang[0].length()).equalsIgnoreCase("V.O.")){
                        lang_final = "V.O.";
                    }else if(lang[0].substring(lang[0].length()-6, lang[0].length()).equalsIgnoreCase("VOSTFR")){
                       lang_final = "VOSTFR";
                    }else{
                        lang_final = "-";
                    }

                    String e_page = getPageContent("http://www.alloshowtv.com/series/"+link[0], null);
                    //System.out.println("DL page : http://www.alloshowtv.com/series/"+link[0]);
                    String[] te_link = e_page.split("Style67\"><a href=\"");
                    String e_link = "";

                    if(te_link.length > 1)
                        e_link = te_link[1].split("\"")[0];

                    this.state("adding "+title[title.length-1]);

                    /*if(e_link.length() > 23){
                        if(e_link.substring(0, 22).equalsIgnoreCase("http://209.212.147.251")){
                            this.state("allourl");
                            Allourl allo_url = new Allourl();
                            e_link = allo_url.getLink(e_link);
                        }
                    }*/

                    System.out.println(e_link);

                    this.incResNb();

                    new_season.addEpisode(new Episode(num, link[0], host[0], "alloshowtv", lang_final, e_link));
                }
                serie.addSeason(new_season);
            }

            serie.loadIcon();

        }catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return serie;
    }

    public void run(){
        this.state("starting");
        this.search = this.search.replace(' ', '+');
        this.result = searchTerm();

        this.state("finished");
    }

    public void search() {
        Thread thread = new Thread(this);
        thread.start();
    }

    public String getPageContent(String u, String data){
        try{
            if(data == null){
                data = "";
            }

            URL url = new URL(u);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
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
            System.out.println("Mauvaise URL: "+u);
        }catch(IOException e){
            System.out.println("IOException : "+u);
        }
        return "";
    }

}
