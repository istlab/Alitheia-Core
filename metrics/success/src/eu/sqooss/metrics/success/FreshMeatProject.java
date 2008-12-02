package eu.sqooss.metrics.success;
import java.util.Calendar;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.net.*;
import javax.xml.stream.*;

public class FreshMeatProject {

    private String projectName;
    private int subscriptions;
    private float popularity;
    private float vitality;
    private float rating;
    private int daysSinceLaunch;
    private boolean successful;

    public FreshMeatProject(String projName) {
        this.projectName = projName;
    }

    public FreshMeatProject(String projName, int subscriptions, float popularity, float vitality, float rating, int days) {
        this(projName);
        this.subscriptions = subscriptions;
        this.popularity = popularity;
        this.vitality = vitality;
        this.rating = rating;
        this.daysSinceLaunch = days;
    }

    public static FreshMeatProject DownloadProjectInfo(String projectName) throws Exception{
        String requestURL = "http://freshmeat.net/projects-xml/" + projectName + "/" + projectName + ".xml";
        XMLStreamReader xmlReader = null;
        try {
            URL url = new URL(requestURL);
           
            xmlReader = XMLInputFactory.newInstance().createXMLStreamReader(url.openConnection().getInputStream());
            
            FreshMeatProject project = new FreshMeatProject(projectName);
            while (xmlReader.hasNext()) {
                int event = xmlReader.getEventType();
                switch (event) {
                    case XMLStreamConstants.START_ELEMENT:
                        String element = xmlReader.getLocalName();
                        if (element.equals("vitality_score")) {
                        	
                            project.setVitalityScore(Float.parseFloat(xmlReader.getElementText()));
                        }
                        if (element.equals("popularity_score")) {
                        	
                            project.setPopularityScore(Float.parseFloat(xmlReader.getElementText()));
                        }
                        if (element.equals("rating")) {
                        	
                            project.setRating(Float.parseFloat(xmlReader.getElementText()));
                        }
                        if (element.equals("subscriptions")) {
                        	
                            project.setSubscriptions(Integer.parseInt(xmlReader.getElementText()));
                        }
                        if (element.equals("date_added")) {
                        	
                            String date = xmlReader.getElementText();
                            //System.out.println("date "+date);
                        //TODO: Date will have be like 2003-11-10 13:23:25
                        //Extract date, compute diff and update DaysSinceLaunch
                        String [] d=date.split(" ");
                        //String [] dd=d[0].slit("-");
                        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        				Date dt = new Date();
        				String ddt=dateFormat.format(dt);
        				Date dt2=new Date();
        				dt2=dateFormat.parse(d[0]);
        				Calendar cal1 =Calendar.getInstance();
				        Calendar cal2 = Calendar.getInstance(); 
				        cal1.setTime(dt);          
        				long ldate1 = dt.getTime();
        				cal2.setTime(dt2);
        				long ldate2 = dt2.getTime() ;
        				int hr1   = (int)(ldate1/3600000); //60*60*1000
        				int hr2   = (int)(ldate2/3600000);
        				int days1 = hr1/24;
        				int days2 = hr2/24;
 						project.setDaysSinceLaunch(-(days2 - days1));

				        

                        }
                }
                xmlReader.next();
            }
            project.setSuccessful(false);
            return project;
        } catch (Exception e) {
            throw e;
        } finally {
            try {
                if (xmlReader != null) {
                    xmlReader.close();
                    
                }
                
            } catch (Exception ex) {
                throw ex;
            }
        }


        //return null;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public int getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(int subscriptions) {
        this.subscriptions = subscriptions;
    }

    public float getPopularityScore() {
        return popularity;
    }

    public void setPopularityScore(float popularity) {
        this.popularity = popularity;
    }

    public float getVitalityScore() {
        return vitality;
    }

    public void setVitalityScore(float vitality) {
        this.vitality = vitality;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public int getDaysSinceLaunch() {
        return daysSinceLaunch;
    }

    public void setDaysSinceLaunch(int daysSinceLaunch) {
        this.daysSinceLaunch = daysSinceLaunch;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }
}