/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package alloshowdesktop;


import java.awt.*;
import java.io.*;
import javax.swing.*;
import javax.media.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class MediaPlayer extends JFrame {

    public MediaPlayer() {

        //final FeedReader reader = new FeedReader(new URL("http://www.oreillynet.com/onjava/blog/atom.xml"));
        //reader.read();

        final JFrame frame = new JFrame("Player");
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setSize( 500, 400 );

        Runnable executeScript = new Runnable() {

            public void run() {
                ClassLoader loader =
                        Thread.currentThread().getContextClassLoader();
                ScriptEngineManager manager = new ScriptEngineManager(loader);
                ScriptEngine engine = manager.getEngineByExtension("fx");

                Bindings bindings = engine.createBindings();
                //bindings.put("media:com.oreilly.onjava.feedticker.FeedReader", "http://sun.edgeboss.net/download/sun/media/1460825906/1460825906_11810873001_09c01923-00.flv");
                bindings.put("MY_CONTAINER:javax.swing.JComponent", frame.getContentPane());

                ScriptContext context = new SimpleScriptContext();
                // Bug workaround
                context.setBindings(bindings, ScriptContext.GLOBAL_SCOPE);
                context.setBindings(bindings, ScriptContext.ENGINE_SCOPE);

                engine.setContext(context);
                String script =
                        "";
                try {
                    engine.eval(script);
                } catch( ScriptException se ) {
                    System.out.println( "Blah" );
                }

                frame.setVisible(true);
            }

        };

        SwingUtilities.invokeLater( executeScript );
    }

}


/*
   private Player player;
   private File file;
   private String floc;

   public MediaPlayer(String file)
   {
      super( "Media Player" );

      this.file = new File(file);

      this.floc = file;

      System.out.println(this.file);

      setSize( 300, 300 );
      createPlayer();
   }

   private void createPlayer()
   {
      if ( file == null )
         return;

      removePreviousPlayer();

      try {
         // create a new player and add listener
         javax.media.bean.playerbean.MediaPlayer MediaPlayer1 = new javax.media.bean.playerbean.MediaPlayer();
         MediaPlayer1.setMediaLocation(new java.lang.String("file:///home/chrissou/Programmation/AlloShowDesktop/file.flv"));

         player = Manager.createPlayer( new URL(this.floc) );
         player.addControllerListener( new EventHandler() );
         player.start();  // start player

         MediaPlayer1.start();
      }
      catch ( Exception e ){
         JOptionPane.showMessageDialog( this,
            "Invalid file or location", "Error loading file",
            JOptionPane.ERROR_MESSAGE );
      }
   }

   private void removePreviousPlayer()
   {
      if ( player == null )
         return;

      player.close();

      Component visual = player.getVisualComponent();
      Component control = player.getControlPanelComponent();

      Container c = getContentPane();

      if ( visual != null )
         c.remove( visual );

      if ( control != null )
         c.remove( control );
   }

   // inner class to handler events from media player
   private class EventHandler implements ControllerListener {
      public void controllerUpdate( ControllerEvent e ) {
         if ( e instanceof RealizeCompleteEvent ) {
            Container c = getContentPane();

            // load Visual and Control components if they exist
            Component visualComponent =
               player.getVisualComponent();

            if ( visualComponent != null )
               c.add( visualComponent, BorderLayout.CENTER );

            Component controlsComponent =
               player.getControlPanelComponent();

            if ( controlsComponent != null )
               c.add( controlsComponent, BorderLayout.SOUTH );

            c.doLayout();
         }
      }
   }

}
*/