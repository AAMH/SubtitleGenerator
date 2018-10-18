package PhaseOne;

import com.xuggle.mediatool.*;
import com.xuggle.mediatool.event.*;
import com.xuggle.xuggler.*;

import javax.swing.*;
import java.awt.*;
import java.io.File;


/**
 * Created by AmirHossein on 10/8/15.
 */


public class MediaManager {


    IMediaReader mediaReader;
    final int mySampleRate = 44100;
    final int myChannels = 2;
    final int myBitRate = 16;
    String CurrentPath;


    JFrame welcomeFrame;

    public MediaManager() {


        File f = new File("");
        CurrentPath =  f.getAbsolutePath();
        f.delete();

        welcomeFrame = new JFrame("STEP ONE");
        welcomeFrame.setSize(300, 100);
        welcomeFrame.setLocation(GUI.SCREEN_WIDTH / 2 - 150, GUI.SCREEN_HEIGHT / 2 - 50);
        welcomeFrame.setLayout(null);

        JLabel label = new JLabel("Please Wait ..");
        label.setSize(300,50);
        label.setLocation(25, 5);
        label.setFont(new Font("Courier New", Font.BOLD,25));
        welcomeFrame.add(label);

    }


    public void seperate2(String from, final String to) {

        mediaReader = ToolFactory.makeReader(from);

        mediaReader.addListener(new MediaToolAdapter() {

            private IContainer container;
            private IMediaWriter mediaWriter;
            IStreamCoder streamCoder;

            @Override
            public void onOpenCoder(IOpenCoderEvent event) {
                container = event.getSource().getContainer();
                mediaWriter = null;
            }

            @Override
            public void onAudioSamples(IAudioSamplesEvent event) {
                if (container != null) {
                    if (mediaWriter == null) {
                        mediaWriter = ToolFactory.makeWriter(to);

                        mediaWriter.addListener(new MediaListenerAdapter() {

                            @Override
                            public void onAddStream(IAddStreamEvent event) {
                                streamCoder = event.getSource().getContainer().getStream(event.getStreamIndex()).getStreamCoder();
                                streamCoder.setFlag(IStreamCoder.Flags.FLAG_QSCALE, false);
                                streamCoder.setBitRate(myBitRate);
                                streamCoder.setChannels(myChannels);
                                streamCoder.setSampleRate(mySampleRate);
                                streamCoder.setBitRateTolerance(0);
                            }
                        });

                        mediaWriter.addAudioStream(0, 0, myChannels, mySampleRate);
                    }


                    mediaWriter.encodeAudio(0, event.getAudioSamples());
                }
            }

            @Override
            public void onClose(ICloseEvent event) {
                if (mediaWriter != null) {
                    mediaWriter.close();
                }
            }
        });

        while (mediaReader.readPacket() == null)
            ;
    }


    public boolean Seperate(String fileName){

        try {

            welcomeFrame.setVisible(true);

            ProcessBuilder pb = new ProcessBuilder("ffmpeg","-y", "-i", fileName,
                    "-vn", "-ar", "16000", "-ac", "1" , "-f" ,"wav","output.wav");
            pb.redirectErrorStream(true).redirectOutput(ProcessBuilder.Redirect.INHERIT);
            Process ffmpeg = pb.start();
            ffmpeg.waitFor();

            welcomeFrame.setVisible(false);

        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;

    }

}