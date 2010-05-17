package alloshowdesktop;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author chrissou
 */
public class Season implements Serializable {

    private List<Episode> episodes = new ArrayList<Episode>();
    private String name;
    private int number;

    public Season(String name, int number) {
        this.name = name;
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    public void addEpisode(Episode e){
        this.episodes.add(e);
    }

    public List<Episode> getEpisodes() {
        return episodes;
    }
    
}
