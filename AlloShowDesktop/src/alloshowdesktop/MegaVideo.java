/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package alloshowdesktop;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author chrissou
 */
public class MegaVideo {

    private Random generator = new Random();
    private String baseURL = "/tmp/alloShowDesktop";
    private String saveURL = baseURL;


    public String downloadFromMV(String url){

        System.out.println("URL = "+url);

        try {
            String finalLink = "";
            String un = null;
            String k1 = null;
            String k2 = null;
            String s = null;
            URL pageu = new URL(url);
            Process p = Runtime.getRuntime().exec("wget " + pageu.toString() + " -O pagecontent");
            Thread.currentThread();
            Thread.sleep(1000);
            String pagecontent = null;

            
            // Open the file that is the first
            // command line parameter
            FileInputStream fstream = new FileInputStream("pagecontent");
            // Get the object of DataInputStream
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader bru = new BufferedReader(new InputStreamReader(in));
            String strLine;
            boolean avilable = true;
            // Read File Line By Line
            while ((strLine = bru.readLine()) != null) {
                // Print the content on the console
                if (strLine.contains("flash")) {
                    pagecontent += strLine;
                }
                if(strLine.contains("This video is unavailable.")){
                    avilable = false;
                }
            }
            // Close the input stream
            in.close();

            if (avilable && pagecontent.contains("flashvars.s = ")
                    && pagecontent.contains("flashvars.k1 = ")
                    && pagecontent.contains("flashvars.k2 = ")
                    && pagecontent.contains("flashvars.un = ")){

                String[] s_ = pagecontent.split("flashvars.s = \"");
                s_ = s_[1].split("\""); // s[0] contient la valeur
                String[] un_ = pagecontent.toString().split("flashvars.un = \"");
                un_ = un_[1].split("\""); // un[0] contient la valeur
                String[] k1_ = pagecontent.toString().split("flashvars.k1 = \"");
                k1_ = k1_[1].split("\""); // k1[0] contient la valeur
                String[] k2_ = pagecontent.toString().split("flashvars.k2 = \"");
                k2_ = k2_[1].split("\""); // k2[0] contient la valeur

                URL Vurl = new URL("http://www" + s_[0] + ".megavideo.com/files/" + decrypt(un_[0], Integer.parseInt(k1_[0]), Integer.parseInt(k2_[0])) + "/");


                return Vurl.toString();
            }else{
                System.out.println("video unaviable");
                return "video unaviable";
            }

        } catch (InterruptedException ex) {
            Logger.getLogger(MegaVideo.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MegaVideo.class.getName()).log(Level.SEVERE, null, ex);
        }

        return "You should never see this";
    }

    public String downloadFile(String fileURL){//NON USED
        saveURL = baseURL + generator.nextInt();
      try
      {
          URL url  = new URL(fileURL);
          System.out.println("Opening connection to " + fileURL + "...");
          URLConnection urlC = url.openConnection();
          // Copy resource to local file, use remote file
          // if no local file name specified
          InputStream is = url.openStream();
          // Print info about resource
          System.out.print("Copying resource (type: " +
                           urlC.getContentType());
          Date date=new Date(urlC.getLastModified());
          System.out.println(", modified on: " +
             date.toLocaleString() + ")...");
          System.out.flush();

          FileOutputStream fos= new FileOutputStream(this.saveURL);
          
          int oneChar, count=0;
          while ((oneChar=is.read()) != -1)
          {
             fos.write(oneChar);
             count++;
          }
          is.close();
          fos.close();
          System.out.println(count + " byte(s) copied");
      }
      catch (MalformedURLException e)
      { System.err.println(e.toString()); }
      catch (IOException e)
      { System.err.println(e.toString()); }

        return saveURL;
    }


    public String alloSevenToMV(String url){
        String code = null;
        try
        {
            URL link = new URL(url);
            URLConnection conn = link.openConnection();
            conn.setDoOutput(true);

            System.out.println(url.toString());

            // Get the response
            StringBuffer answer = new StringBuffer();
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                answer.append(line);
            }
            reader.close();

            if(answer.toString().contains("mv_player.swf")){
                String[] rep = answer.toString().split("mv_player.swf\\?v=");
                rep = rep[1].split("\"");
                code = rep[0];
                return "http://www.megavideo.com/?v="+code;
            }else if(answer.toString().contains("http://www.megaupload.com/")){
                String[] rep = answer.toString().split("http://www.megaupload.com/");
                rep = rep[1].split("\"");
                code = rep[0];
                return "http://www.megavideo.com/"+code;
            }else if(answer.toString().contains("http://www.megavideo.com/")){
                String[] rep = answer.toString().split("http://www.megavideo.com/");
                rep = rep[1].split("\"");
                code = rep[0];
                return "http://www.megavideo.com/"+code;
            }

            

        }catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return "You should never see this";
    }


    public static String encodeHTML(String s)
    {
        StringBuffer out = new StringBuffer();
        for(int i=0; i<s.length(); i++)
        {
            char c = s.charAt(i);
            if(c > 127 || c=='"' || c=='<' || c=='>')
            {
               out.append("&#"+(int)c+";");
            }
            else
            {
                out.append(c);
            }
        }
        return out.toString();
    }


	private static String decrypt(String input, int k1, int k2) {
		List<Integer> req1 = new ArrayList<Integer>();
		int req3 = 0;
		while (req3 < input.length()) {
			char c = input.charAt(req3);
			switch (c) {
			case '0':
				req1.add(0);
				req1.add(0);
				req1.add(0);
				req1.add(0);
				break;
			case '1':
				req1.add(0);
				req1.add(0);
				req1.add(0);
				req1.add(1);
				break;
			case '2':
				req1.add(0);
				req1.add(0);
				req1.add(1);
				req1.add(0);
				break;
			case '3':
				req1.add(0);
				req1.add(0);
				req1.add(1);
				req1.add(1);
				break;
			case '4':
				req1.add(0);
				req1.add(1);
				req1.add(0);
				req1.add(0);
				break;
			case '5':
				req1.add(0);
				req1.add(1);
				req1.add(0);
				req1.add(1);
				break;
			case '6':
				req1.add(0);
				req1.add(1);
				req1.add(1);
				req1.add(0);
				break;
			case '7':
				req1.add(0);
				req1.add(1);
				req1.add(1);
				req1.add(1);
				break;
			case '8':
				req1.add(1);
				req1.add(0);
				req1.add(0);
				req1.add(0);
				break;
			case '9':
				req1.add(1);
				req1.add(0);
				req1.add(0);
				req1.add(1);
				break;
			case 'a':
				req1.add(1);
				req1.add(0);
				req1.add(1);
				req1.add(0);
				break;
			case 'b':
				req1.add(1);
				req1.add(0);
				req1.add(1);
				req1.add(1);
				break;
			case 'c':
				req1.add(1);
				req1.add(1);
				req1.add(0);
				req1.add(0);
				break;
			case 'd':
				req1.add(1);
				req1.add(1);
				req1.add(0);
				req1.add(1);
				break;
			case 'e':
				req1.add(1);
				req1.add(1);
				req1.add(1);
				req1.add(0);
				break;
			case 'f':
				req1.add(1);
				req1.add(1);
				req1.add(1);
				req1.add(1);
				break;
			}
			req3++;
		}

		List<Integer> req6 = new ArrayList<Integer>();
		req3 = 0;
		while (req3 < 384) {
			k1 = (k1 * 11 + 77213) % 81371;
			k2 = (k2 * 17 + 92717) % 192811;
			req6.add((k1 + k2) % 128);
			req3++;
		}
		req3 = 256;
		while (req3 >= 0) {
			int req5 = req6.get(req3);
			int req4 = req3 % 128;
			int req8 = req1.get(req5);
			req1.set(req5, req1.get(req4));
			req1.set(req4, req8);
			--req3;
		}
		req3 = 0;
		while (req3 < 128) {
			req1.set(req3, req1.get(req3) ^ (req6.get(req3 + 256) & 1));
			++req3;
		}

		String out = "";
		req3 = 0;
		while (req3 < req1.size()) {
			int tmp = (req1.get(req3) * 8);
			tmp += (req1.get(req3 + 1) * 4);
			tmp += (req1.get(req3 + 2) * 2);
			tmp += (req1.get(req3 + 3));
			switch (tmp) {
			case 0:
				out = out + "0";
				break;
			case 1:
				out = out + "1";
				break;
			case 2:
				out = out + "2";
				break;
			case 3:
				out = out + "3";
				break;
			case 4:
				out = out + "4";
				break;
			case 5:
				out = out + "5";
				break;
			case 6:
				out = out + "6";
				break;
			case 7:
				out = out + "7";
				break;
			case 8:
				out = out + "8";
				break;
			case 9:
				out = out + "9";
				break;
			case 10:
				out = out + "a";
				break;
			case 11:
				out = out + "b";
				break;
			case 12:
				out = out + "c";
				break;
			case 13:
				out = out + "d";
				break;
			case 14:
				out = out + "e";
				break;
			case 15:
				out = out + "f";
				break;
			}
			req3 += 4;
		}
		return out;
	}


}
