package PhaseOne;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

import javax.swing.*;

import PhaseTwo.SubtitleEntry;
import PhaseTwo.TextManager;
import com.xuggle.mediatool.*;
import com.xuggle.mediatool.event.IVideoPictureEvent;
import com.xuggle.xuggler.*;

/**
 * Created by AmirHossein on 10/23/15.
 */
public class GUI implements Runnable{


    private static final Integer WIDTH = 500;
    private static final Integer HEIGHT = 500;

    public static int SCREEN_WIDTH = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
    public static int SCREEN_HEIGHT = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();

    JFrame frame;
    JFrame progressFrame;
    JProgressBar processedData;
    JLabel[] FileInfo = new JLabel[5];
    JButton[] buttons = new JButton[3];

    static MyVideoPanel VideoPanel;
    public static String sourceUrl;

    MediaManager MM;
    TextManager TM;

    public static long FileDuarion;
    private static long ElapsedTime;

    public static ArrayList<SubtitleEntry> Subs;


    public void run(){

        while(true){

            ElapsedTime+=50;

            this.FileInfo[2].setText(new StringBuilder().append("Processed :   ").append(Long.toString(TextManager.CurrentFrame / 1000)).append("/").append(Long.toString(FileDuarion / 1000)).append(" Seconds ").toString());
            this.FileInfo[3].setText(new StringBuilder().append("Elapsed Time :   ").append(getAlphTime(ElapsedTime)).toString());

            processedData.setValue ((int)TextManager.CurrentFrame);
            processedData.repaint();
            progressFrame.repaint();
            if(processedData.getValue() >= processedData.getMaximum()) {
                System.out.println("finished!");
                this.FileInfo[4].setText("Done!");
                this.buttons[2].setText("Exit");
                buttons[0].setEnabled(true);
                buttons[1].setEnabled(true);

          //      StartPlay();
                break;
            }
            try{
                Thread.sleep(50);
            }
            catch(InterruptedException ie){

            }
        }


    }


    public GUI(){


        frame = new JFrame();
        frame.setLocation(SCREEN_WIDTH / 2 - 500, SCREEN_HEIGHT / 2 - 350);
        frame.setSize(1000, 700);
        frame.setLayout(null);

        VideoPanel = new MyVideoPanel();
        VideoPanel.setSize(new Dimension(WIDTH, HEIGHT));
        VideoPanel.setLocation(100, 100);


        frame.add(VideoPanel);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);



        progressFrame = new JFrame("STEP TWO");
        progressFrame.setSize(500, 300);
        progressFrame.setLocation(SCREEN_WIDTH / 2 - 250, SCREEN_HEIGHT / 2 - 150);
        progressFrame.setLayout(null);

        processedData = new JProgressBar(0,(int)FileDuarion);
        processedData.setSize(480, 50);
        processedData.setLocation(5, 5);
        processedData.setStringPainted(true);
        processedData.setForeground(Color.GREEN);
        progressFrame.add(processedData);


        FileInfo[0] = new JLabel();
        FileInfo[0].setSize(480, 30);
        FileInfo[0].setLocation(10, 70);

        FileInfo[1] = new JLabel();
        FileInfo[1].setSize(480, 30);
        FileInfo[1].setLocation(10, 100);

        FileInfo[2] = new JLabel();
        FileInfo[2].setSize(480, 30);
        FileInfo[2].setLocation(10, 130);

        FileInfo[3] = new JLabel();
        FileInfo[3].setSize(480, 30);
        FileInfo[3].setLocation(10, 160);

        FileInfo[4] = new JLabel();
        FileInfo[4].setSize(100, 30);
        FileInfo[4].setLocation(10, 200);


        progressFrame.add(FileInfo[0]);
        progressFrame.add(FileInfo[1]);
        progressFrame.add(FileInfo[2]);
        progressFrame.add(FileInfo[3]);
    //    progressFrame.add(FileInfo[4]);

        buttons[0] = new JButton("Play with this App");
        buttons[0].setSize(150, 40);
        buttons[0].setLocation(10, 200);
        buttons[0].setEnabled(false);
        buttons[0].addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                Thread t = new Thread(new Runnable() {
                    public void run() {

                        StartPlay();

                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {

                                progressFrame.repaint();
                                progressFrame.setVisible(false);

                            }
                        });
                    }
                });
                t.start();

            }
        });

        buttons[1] = new JButton("Play with VLC");
        buttons[1].setSize(150, 40);
        buttons[1].setLocation(170, 200);
        buttons[1].setEnabled(false);
        buttons[1].addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    ProcessBuilder pb = new ProcessBuilder("open","/Applications/VLC.app/", sourceUrl);
                    pb.start();
                //    System.exit(0);

                }catch(Exception ex){}

            }
        });

        buttons[2] = new JButton("Cancel");
        buttons[2].setSize(150, 40);
        buttons[2].setLocation(330, 200);
        buttons[2].addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                    System.exit(0);
            }
        });

        progressFrame.add(buttons[0]);
        progressFrame.add(buttons[1]);
        progressFrame.add(buttons[2]);


        JFileChooser fc=new JFileChooser();
        int returnVal=fc.showOpenDialog(frame);
        if (returnVal == JFileChooser.APPROVE_OPTION) {

            sourceUrl = fc.getSelectedFile().getAbsolutePath();
            System.out.println(sourceUrl);

            if(new File(new StringBuilder().append(GUI.sourceUrl.substring(0,GUI.sourceUrl.length()-4)).append(".srt").toString()).exists()) {
                int dialogButton = JOptionPane.YES_NO_OPTION;
                int dialogResult = JOptionPane.showConfirmDialog(null, "A subtitle file has been found for this video, Would you like to make it again ?", "Warning", dialogButton);
                if (dialogResult == JOptionPane.YES_OPTION)
                    InitiateProcess();
                else{
                    StartPlay();
                }

            } else
                InitiateProcess();

        }
        else {
            System.out.println("asdadasd");
        }

    }

    private void InitiateProcess(){

        MM = new MediaManager();
        MM.Seperate(sourceUrl);

        IContainer Container = IContainer.make();
        Container.open(sourceUrl,IContainer.Type.READ, null);
        FileDuarion= Container.getDuration() / 1000;
        System.out.println("dura " + FileDuarion);
        processedData.setMaximum((int) FileDuarion);
        Container.close();


        String temp = new String("");
        StringTokenizer st = new StringTokenizer(sourceUrl,"/");
        while(st.hasMoreTokens())
            temp = st.nextToken();
        this.FileInfo[0].setText(new StringBuilder().append("File Name :   ").append(temp).toString());
        this.FileInfo[1].setText(new StringBuilder("File Duration :   ").append(getAlphTime(FileDuarion)).toString());


        progressFrame.setVisible(true);
        (new Thread(this)).start();
        TM = new TextManager();

    }

    private String getAlphTime(long l){

        int t = (int)(l / 1000);
        int ms =  (int)(l % 1000);

        int h = (t / 3600);
        int m = ((t % 3600) / 60);
        int s = ((t % 3600) % 60);

        String st = new String("");

        if(h > 0)
            st = new StringBuilder(st).append(Integer.toString(h)).append(" hours ").toString();
        if(m > 0)
            st = new StringBuilder(st).append(Integer.toString(m)).append(" minutes ").toString();
        if(s > 0)
            st = new StringBuilder(st).append(Integer.toString(s)).append(" seconds ").toString();
        return st;
    }

    public void StartPlay(){

        LoadSubs();

        IMediaReader reader = ToolFactory.makeReader(sourceUrl);
        reader.addListener(new MyViewer(true));
        System.out.println("aaaaaa");
        while (reader.readPacket() == null)
            ;

    }

    private void LoadSubs(){

        if(TextManager.SubtitlesAreReady){
            this.Subs = TM.getSubs();
        }
        else{
            try {
                this.Subs = new ArrayList<SubtitleEntry>();
                String s1 = " ",s2 = " ",s3 = " ";
                StringTokenizer st;
                FileInputStream fis = new FileInputStream(new StringBuilder().append(GUI.sourceUrl.substring(0, GUI.sourceUrl.length() - 4)).append(".srt").toString());
                Scanner sc = new Scanner(fis);
                while(sc.hasNext()){
                    s1 = sc.nextLine();
                    s2 = sc.nextLine();
                    s3 = sc.nextLine();
                    sc.nextLine();
                    st = new StringTokenizer(s2,"-> ");
                    this.Subs.add(new SubtitleEntry(Integer.parseInt(s1),s3.substring(0,s3.length()-1),st.nextToken(),st.nextToken()));

                }
                fis.close();

            }catch(Exception e){

            }
        }
    }

    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel("com.jtattoo.plaf.noire.NoireLookAndFeel");
            GUI videoPlayer = new GUI();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public static void play(String sourceUrl) {

        final IMediaReader reader = ToolFactory.makeReader(sourceUrl);
        reader.setBufferedImageTypeToGenerate(BufferedImage.TYPE_3BYTE_BGR);


        MediaListenerAdapter adapter = new MediaListenerAdapter() {

            @Override
            public void onVideoPicture(IVideoPictureEvent event) {
                VideoPanel.setImage((BufferedImage) event.getImage());
                try {
                    Thread.sleep((int) reader.getContainer().getStream(event.getStreamIndex()).getFrameRate().getDouble());
                } catch (InterruptedException ex) {

                    System.out.println(ex.getStackTrace());
                }
            }
        };

        reader.addListener(adapter);

        while(reader.readPacket() == null);

    }


    @SuppressWarnings("serial")
    private class MyVideoPanel extends JPanel {

        Image image;

        public void setImage(final Image image) {
            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    MyVideoPanel.this.image = image;
                    repaint();
                }
            });
        }

        @Override
        public synchronized void paint(Graphics g) {
            if (image != null) {
                g.drawImage(image, 0, 0, VideoPanel.getWidth(), VideoPanel.getHeight() ,null);
            }
        }

//        @Override
//        public void processEvent(AWTEvent e){
//            if(e.getID() == AWTEvent.RESERVED_ID_MAX + 1){
//                play(sourceUrl);
//            }
//            super.processEvent(e);
//        }
    }

}