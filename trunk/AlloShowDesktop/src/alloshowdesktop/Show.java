package alloshowdesktop;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 *
 * @author chrissou
 */
public class Show implements Serializable {

    static private final long serialVersionUID = 3L;

    private List<alloshowdesktop.Season> seasons = new ArrayList<Season>();
    private String name;
    private String image;
    private ImageIcon icon;
    private boolean current = true;

    public Show() {
    }

    public String getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public List<Season> getSeasons() {
        return seasons;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ImageIcon getIcon() {
        return icon;
    }

    public void addSeason(Season s){
        this.seasons.add(s);
    }

    public Season getSeason(int n){
        Season good = null;
        for(Season s : seasons){
            if (s.getNumber() == n)
                good = s;
        }
        
        return good;
    }

    public void loadIcon(){
        BufferedImage original = null;
        try {
            System.out.println(this.image);
            original = ImageIO.read(new URL(this.image));

            BufferedImage bi = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
            bi.getGraphics().drawImage(original, 0, 0, 100, 100, null);
            this.icon = new ImageIcon(bi);

        } catch (MalformedURLException ex) {
            //Logger.getLogger(ASDgui.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("URL "+ this.image +" seems to be wrong");
        }catch (IOException ex) {
            System.out.println("Image "+ this.image +" cannot be loaded");
            //Logger.getLogger(ASDgui.class.getName()).log(Level.SEVERE, null, ex);
            this.icon = new javax.swing.ImageIcon(getClass().getResource("/alloshowdesktop/no_image.gif"));
        } catch (NullPointerException ex){
            System.out.println("Error loading image " + this.image);
        }
    }

    public boolean isCurrent() {
        return current;
    }

    public void setCurrent(boolean current) {
        this.current = current;
    }

    

}
