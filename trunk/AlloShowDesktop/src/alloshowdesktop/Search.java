/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package alloshowdesktop;

import java.util.List;
import java.util.Observable;

/**
 *
 * @author chrissou
 */
public class Search extends Observable {

    protected String state;
    protected List<Show> result;
    private Integer nb_sofar = 0;

    public void incResNb(){
        this.nb_sofar++;
    }
    public Integer getNbSofar() {
        return nb_sofar;
    }

    public String getState(){
        return state;
    }

    public List<Show> getResult() {
        return result;
    }

    public void setResult(List<Show> result) {
        this.result = result;
    }

    public void state(String st){
        this.state = st;
        this.setChanged();
        this.notifyObservers();
    }

}
