package PhaseTwo;


/**
 * Created by AmirHossein on 10/18/15.
 */
public class SubtitleEntry {

    private int ID;
    private String Text;
    private String StartTime;
    private String EndTime;

    public SubtitleEntry(int id, String text, long starttime, long endtime){

        this.ID = id;
        this.Text = new String(text.substring(1));
        this.StartTime = new String(msToTime(starttime));
        this.EndTime = new String(msToTime(endtime));

    }

    public SubtitleEntry(int id, String text, String starttime, String endtime){

        this.ID = id;
        this.Text = new String(text);
        this.StartTime = new String(starttime);
        this.EndTime = new String(endtime);

    }

    public String getID(){
        return Integer.toString(this .ID);
    }

    public String getTime(){
        return new StringBuilder().append(this.StartTime)
                .append(" --> ")
                .append(this.EndTime)
                .toString();
    }

    public String getText(){
        return new StringBuilder().append(this.Text).append(".").toString();
    }

    private String msToTime(long duration) {

        int t = (int)(duration / 1000);
        int ms =  (int)(duration % 1000);

        int h = (int) (t / 3600);
        int m = (int) ((t % 3600) / 60);
        int s = (int) ((t % 3600) % 60);

        String s1, s2, s3;

        if(h >= 10)
            s1 = Integer.toString(h);
        else
            s1 = new StringBuilder().append("0").append(Integer.toString(h)).toString();

        if(m >= 10)
            s2 = Integer.toString(m);
        else
            s2 = new StringBuilder().append("0").append(Integer.toString(m)).toString();

        if(s >= 10)
            s3 = Integer.toString(s);
        else
            s3 = new StringBuilder().append("0").append(Integer.toString(s)).toString();

        String str = new StringBuilder().append(s1).append(":")
                .append(s2).append(":")
                .append(s3).append(",")
                .append(Integer.toString(ms)).toString();

        return  str;

    }

    public String getStartTime(){
        return this.StartTime;
    }

    public String getEndTime(){
        return this.EndTime;
    }
}
