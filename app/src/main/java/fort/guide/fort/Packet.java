package fort.guide.fort;

import java.util.Date;

/**
 * Created by dj on 22/3/19.
 */

public class Packet {

    double latitude;
    double longitude;
    String audio;
    String obstacleName;
    String fortName;
    String imageUrl;
    Date modifiedOn;
    Date createdOn;

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }

    public String getObstacleName() {
        return obstacleName;
    }

    public void setObstacleName(String obstacleName) {
        this.obstacleName = obstacleName;
    }

    public String getFortName() {
        return fortName;
    }

    public void setFortName(String fortName) {
        this.fortName = fortName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Date getModifiedOn() {
        return modifiedOn;
    }

    public void setModifiedOn(Date modifiedOn) {
        this.modifiedOn = modifiedOn;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }
}
