package alloshowdesktop;

import java.io.Serializable;
import java.net.URL;
import java.util.List;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



/**
 *
 * @author chrissou
 */
public class Episode implements Serializable {

    private int number;
    private String specialCode;
    private String host;
    private String website;
    private String lang;

    private List<URL> sources;

    public Episode(int n, String sc, String h, String w, String l) {
        number=n;
        specialCode = sc;
        host=h;
        website=w;
        lang=l;
    }

    public void setSpecialCode(String specialCode) {
        this.specialCode = specialCode;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    
    public String getFakeName(){
        return "Episode "+number+" ("+lang+")";
    }

    public String getUrl(){
        String[] exp = specialCode.split("\\(");
        exp = exp[1].split(",");
        String url = "http://www.alloseven.com/embed.php?s="+exp[0]+"&e="+exp[1]+"&k=series";
        exp = exp[2].split("'");
        url = url + "&v="+exp[1];
        return url;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    

}
