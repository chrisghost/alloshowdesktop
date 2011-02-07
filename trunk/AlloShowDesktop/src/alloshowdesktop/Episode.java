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
    private String direct_url;

    private List<URL> sources;

    public Episode(int number, String specialCode, String host, String website, String lang, String durl) {
        this.number=number;
        this.specialCode = specialCode;
        this.host=host;
        this.website=website;
        this.lang=lang;
        this.direct_url=durl;
    }

    public String getDirect_url() {
        return direct_url;
    }

    public void setDirect_url(String direct_url) {
        this.direct_url = direct_url;
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
         String url = "";
        if(this.host.equals("alloseven")){
            String[] exp = specialCode.split("\\(");
            exp = exp[1].split(",");
            url = "http://www.alloseven.com/embed.php?s="+exp[0]+"&e="+exp[1]+"&k=series";
            exp = exp[2].split("'");
            url = url + "&v="+exp[1];
        }else if(this.host.equals("alloshowtv")){
            url = "http://alloshowtv.com/series/"+this.specialCode;
        }
        return url;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    

}
