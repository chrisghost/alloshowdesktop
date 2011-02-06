/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package alloseven;

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
import java.util.Arrays;
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
        String s = this.search;

        System.out.println("Searching for "+s);

        List<Show> list = new ArrayList<Show>();
        try {
//            int errorCode = 0;
            StringBuffer answer = new StringBuffer();
//            do{
                //System.out.println("Searching...");
                // Send the request

                this.state = "sending request";
                this.setChanged();
                this.notifyObservers();

                URL url = new URL("http://www.alloseven.com/?q="+s+"&k=series");
                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);
                OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());

                //write parameters
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
//                errorCode = answer.toString().indexOf("/home/alloshow/public_html/series/connexion.php");
//            }while(errorCode != -1);
            //Output the response
            //System.out.println(answer.toString());

            String[] res = answer.toString().split("<span class=\"titre\">");
//            res = res[1].split("<b>");
//            res = res[1].split("<tr");

            List<String> lt = new ArrayList<String>(Arrays.asList(res));
            lt.remove(0);
            res = lt.toArray(new String[0]);

            this.state = "listing";this.setChanged();this.notifyObservers();

            for(String element : res){
                String[] t = element.split("<a href=\"");
                t = t[1].split("\">");
                list.add(populateShow(t[0]));
            }

        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        //System.out.println("... search finished! Found "+list.size()+" show(s).");

        return list;

    }

    private Show populateShow(String string) {
        Show serie = new Show();
        try{
            URL url = new URL("http://www.alloseven.com"+string);
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

            String[] res = answer.toString().split("<div class=\"thumb\">");

            //IMAGE
            String[] img = res[1].split("src=\"");
            img = img[1].split("\"");
            serie.setImage(img[0]);
            //IMAGE

            //TITLE
            String[] title = res[1].split("<span class=\"titre\">");
            title = title[1].split("</span>");
            serie.setName(title[0]);
            //TITLE

            this.state = "adding "+title[0];this.setChanged();this.notifyObservers();

            //SEASONS
            String[] seasons = answer.toString().split("id=\"theS\"");
            seasons = seasons[1].split("</select>");
            seasons = seasons[0].split("value='");

            //remove first element
            List<String> lt = new ArrayList<String>(Arrays.asList(seasons));
            lt.remove(0);
            seasons = lt.toArray(new String[0]);
            //remove first element
            
            for(String cur : seasons){
                String[] number = cur.split("'");
                int n = Integer.parseInt(number[0]);
                String[] name = cur.split("</option>");

                Season newSeason = new Season(name[0], n);

                String[] episodes = answer.toString().split("id='sais_"+Integer.toString(n)+"'");
                episodes = episodes[1].split("</div></div>");
                episodes = episodes[0].split("onclick=\"");

                //remove first element
                List<String> lt2 = new ArrayList<String>(Arrays.asList(episodes));
                lt2.remove(0);
                episodes = lt2.toArray(new String[0]);
                //remove first element
                for(String curEpisode : episodes){
                    String[] specialCode = curEpisode.split("\"");
                    String[] EpNumber = curEpisode.split(">");
                    EpNumber = EpNumber[1].split("<");
                    int EpN = Integer.parseInt(EpNumber[0]);

                    try{
                        //generate url from specialCode
                        String[] exp = specialCode[0].split("\\(");
                        exp = exp[1].split(",");
                        String Turl = "http://www.alloseven.com/embed.php?s="+exp[0]+"&e="+exp[1]+"&k=series";
                        exp = exp[2].split("'");
                        Turl = Turl + "&v="+exp[1];



                        URL epUrl = new URL(Turl);
                        URLConnection epConn = epUrl.openConnection();
                        epConn.setDoOutput(true);

                        // Get the response
                        StringBuffer epAnswer = new StringBuffer();
                        BufferedReader epReader = new BufferedReader(new InputStreamReader(epConn.getInputStream()));
                        String epLine;
                        while ((epLine = epReader.readLine()) != null) {
                            epAnswer.append(epLine);
                        }
                        epReader.close();


                        String[] episodesList = epAnswer.toString().split("<option ");
                        //remove first element
                        List<String> lt3 = new ArrayList<String>(Arrays.asList(episodesList));
                        lt3.remove(0);
                        episodesList = lt3.toArray(new String[0]);
                        for(String curEpisodeInList : episodesList){
                            String lang = "N/A";


                            String[] temp = curEpisodeInList.split("value=\'");
                            temp = temp[1].split("'");
                            int num = Integer.parseInt(temp[0]);
                            temp = temp[1].split(">");
                            temp = temp[1].split("<");
                            if(temp[0].contains("MegaVideo")){// Dispo sur MV
                                temp[0] = temp[0].replace("MegaVideo", "");
                                if(temp[0].length() > 2){// Langue Disponible
                                    temp[0] = temp[0].replace("/MU", "");
                                    temp[0] = temp[0].replace("[", "");
                                    temp[0] = temp[0].replace("]", "");
                                    lang = temp[0];
                                }
                                newSeason.addEpisode(new Episode(EpN, specialCode[0]+num, "MV", "AS", lang));
                            }
                        }

                        
                    }catch (MalformedURLException ex) {
                        ex.printStackTrace();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }

                serie.addSeason(newSeason);
            }
            //SEASONS

            serie.loadIcon();

        }catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return serie;
    }

    public void run(){
        this.state = "starting";this.setChanged();this.notifyObservers();
        this.search = this.search.replace(' ', '+');
        this.result = searchTerm();
        System.out.println(this.result.toString());
        this.state = "finished";this.setChanged();this.notifyObservers();
    }

    public void search() {
        Thread thread = new Thread(this);
        thread.start();
    }

}
