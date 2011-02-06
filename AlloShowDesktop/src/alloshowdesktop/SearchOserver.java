/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package alloshowdesktop;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 *
 * @author chrissou
 */
public class SearchOserver implements Observer{
    private ASDgui gui;
    private Main main;
    private List<Show> result;

    public SearchOserver(ASDgui gui, Main main) {
        this.gui = gui;
        this.main = main;
    }

    public List<Show> newSearch(alloshowtv.Search s){
        s.addObserver(this);
        s.notifyObservers();
        
        System.out.println(s.countObservers()+" observers");
        s.search();
        return s.getResult();
    }

    public void update(Observable o, Object o1) {
        System.out.println("NOTIFIED");

        Search s = (Search) o;
        String m = "";
       
        if (s.getState().equals("starting"))
            m = "Starting...";
        else if(s.getState().equals("sending request"))
            m = "Connecting...";
        else if(s.getState().equals("listing"))
            m = "Listing results...";
        else if(s.getState().contains("adding")){
            String title = s.getState().replace("adding ", "");
            m = "Processing "+title;
        }
        else if(s.getState().contains("finished")){
            m = "Search finished";
            this.result = s.getResult();
            this.main.finishSearch(result);
        }
        else
            m="...";
        
        this.gui.setSearchInfosLabel(m);
        
    }

    public void setGui(ASDgui gui) {
        this.gui = gui;
    }

    public boolean finished(){
        return this.result.size() > 0;
    }

    public void setMain(Main main) {
        this.main = main;
    }

}
