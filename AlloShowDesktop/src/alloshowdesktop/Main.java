/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package alloshowdesktop;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;


/**
 *
 * @author chrissou
 */
public class Main {

    private alloshowtv.Search alloshowtv = new alloshowtv.Search();
    private alloseven.Search alloseven;// = new alloseven.Search();
    private ASDgui gui ;
    private List<Show> currentShows = new ArrayList<Show>();
    private List<Show> allShows = new ArrayList<Show>();
    private Hashtable<String, String> icons = new Hashtable<String, String>();
    private SearchOserver sObs = new SearchOserver(this.gui, this);

    private String lastSearchTerm = "";//utile quand on tape du texte pour savoir si on vient de rajouter ou d enlever une lettre/texte
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.println("AlloShowDesktop starts...");

        ASDgui.main(args);
    }

    public void setGui(ASDgui gui) {
        this.gui = gui;
        this.sObs.setGui(gui);
    }

    public List<Show> getCurrentShows() {
        List<Show> ret = new ArrayList<Show>();
        for (Iterator<Show> it = allShows.iterator(); it.hasNext();) {
            Show show = it.next();
            if(show.isCurrent()){
                ret.add(show);
            }
        }
        return ret;
    }

    public List<Show> getAllShows() {
        return allShows;
    }
    

    public Show getShowByTitle(String title){
        Show s = null;

        for(Show e : this.allShows){
            if (e.getName().equalsIgnoreCase(title))
                s = e;
        }

        return s;
    }

    public void updateCurrentShows(String st){

        if(st.length() > this.lastSearchTerm.length()){//on a rajoute un lettre
            for (Iterator<Show> it = this.getCurrentShows().iterator(); it.hasNext();) {
                Show show = it.next();
                if(!show.getName().toLowerCase().contains(st.toLowerCase())){
                    show.setCurrent(false);
                }
            }
        }else{//on a enleve une lettre
           for (Iterator<Show> it = allShows.iterator(); it.hasNext();) {
                Show show = it.next();
                if(show.getName().toLowerCase().contains(st.toLowerCase())){
                    show.setCurrent(true);
                }
            }
        }
        this.lastSearchTerm = st;
    }

    public void search(String s){
//        this.currentShows = alloshowtv.searchTerm(s);
        sObs.newSearch(new alloseven.Search(s));
        
    }

    public void finishSearch(List<Show> result){
        this.currentShows = result;
        
        for (Iterator<Show> it = currentShows.iterator(); it.hasNext();) {
            Show show = it.next();
            if(this.allShows != null){
                if(!this.allShowsContains(show)){
                    this.allShows.add(show);
                    System.out.println("adding "+ show.getName() +" to allShows");
                }
            }else{
                this.allShows = this.currentShows;
                System.out.println("ALLSHOWS ARE OVERWRITTEN!");
            }
        }
        this.saveShows();
        this.updateDbInfosLabel();
        this.gui.updateTree();
    }

    public Boolean allShowsContains(Show s){
        for (Iterator<Show> it = allShows.iterator(); it.hasNext();) {
            Show show = it.next();
            if (s.getName().equalsIgnoreCase(show.getName()))
                return true;
        }
        return false;
    }

    public void populateIcons(){
        this.icons.put("AS", "images/as.png");
        this.icons.put("MV", "images/mv.png");
    }

    public String getIcon(String k){
        return this.icons.get(k);
    }

    public void saveShows(){
        try {

            // ouverture d'un flux de sortie vers le fichier "personne.serial"
            FileOutputStream fos = new FileOutputStream("shows.serial");

            // création d'un "flux objet" avec le flux fichier
            ObjectOutputStream oos= new ObjectOutputStream(fos);
            try {
                // sérialisation : écriture de l'objet dans le flux de sortie
                oos.writeObject(this.getAllShows());
                // on vide le tampon
                oos.flush();
                System.out.println("Serialization Ok");
            } finally {
                    //fermeture des flux
                    try {
                            oos.close();
                    } finally {
                            fos.close();
                    }
            }
        } catch(IOException ioe) {
         ioe.printStackTrace();
        }
    }

    public void loadShows() {
            try {
                    // ouverture d'un flux d'entrée depuis le fichier "personne.serial"
                    FileInputStream fis = new FileInputStream("shows.serial");
                    // création d'un "flux objet" avec le flux fichier
                    ObjectInputStream ois= new ObjectInputStream(fis);
                    try {
                            // désérialisation : lecture de l'objet depuis le flux d'entrée
                            this.allShows = (List<Show>) ois.readObject();
                            for (Iterator<Show> it = allShows.iterator(); it.hasNext();) {
                                it.next().setCurrent(true);
                            }
                    } finally {
                            // on ferme les flux
                            try {
                                    ois.close();
                            } finally {
                                    fis.close();
                            }
                    }
            } catch(IOException ioe) {
                    System.out.println("No file to load");
            } catch(ClassNotFoundException cnfe) {
                    cnfe.printStackTrace();
            }
            if(this.allShows != null) {
                    System.out.println("Deserialization Ok");
                    this.gui.updateTree();
                    this.updateDbInfosLabel();
            }
    }


    public void updateDbInfosLabel(){
        String info = "Database infos : ";
        File f=new File("shows.serial");
        info = info+this.allShows.size()+" shows; ";
        info = info+f.length()/1000+" KB ";

        this.gui.setDbInfosLabel(info);
    }
}
