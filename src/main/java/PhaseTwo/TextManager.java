package PhaseTwo;

import PhaseOne.GUI;
import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.api.StreamSpeechRecognizer;
import edu.cmu.sphinx.result.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

/**
 * Created by AmirHossein on 10/8/15.
 */


public class TextManager {

    ArrayList<SubtitleEntry> Subs;

    public static long CurrentFrame;
    public static boolean SubtitlesAreReady = false;

    public TextManager(){


        Subs = new ArrayList<SubtitleEntry>();
        Configuration configuration = new Configuration();


        configuration.setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us");
        configuration.setDictionaryPath("resource:/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict");
        configuration.setLanguageModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us.lm.bin");


        try {

            StreamSpeechRecognizer recognizer = new StreamSpeechRecognizer(configuration);
            recognizer.startRecognition(new FileInputStream("output.wav"));
            SpeechResult result ;

            String temp = new String("");
            int counter = 1;
            long d1 = 0,d2 = 0;
            boolean b = false;

            while ((result = recognizer.getResult()) != null) {
            //    System.out.print(result.getHypothesis() + "          ");

                for (WordResult r : result.getWords()) {

                    CurrentFrame = r.getTimeFrame().getEnd();
                    if(!r.getWord().toString().equals("<sil>")) {
                        temp = new StringBuilder().append(temp).append(" ").append(r.getWord().toString()).toString();
                        if(b || d1 == 0) {
                            d1 = r.getTimeFrame().getStart();
                            b = false;
                        }
                        d2 = r.getTimeFrame().getEnd();
                    }
                    else{
                        if(!b && !temp.equals("")) {
                            Subs.add(new SubtitleEntry(counter++, temp, d1, d2));
                            temp = new String("");
                        }
                        b = true;

                    }

                }
            }
            CurrentFrame = GUI.FileDuarion;
            if(!temp.equals(""))
                Subs.add(new SubtitleEntry(counter++, temp, d1, d2));

            this.SubtitlesAreReady = true;

            FileOutputStream fos = new FileOutputStream(new StringBuilder().append(GUI.sourceUrl.substring(0,GUI.sourceUrl.length()-4)).append(".srt").toString());
            PrintStream Ps = new PrintStream(fos);

            for(SubtitleEntry SE : Subs){
                Ps.println(SE.getID());
                Ps.println(SE.getTime());
                Ps.println(SE.getText());
                Ps.println();
            }
            recognizer.stopRecognition();
            fos.close();


        } catch (Exception e) {

        }
    }

    public ArrayList<SubtitleEntry> getSubs(){
        return this.Subs;
    }
}