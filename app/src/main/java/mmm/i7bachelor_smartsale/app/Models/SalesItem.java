package mmm.i7bachelor_smartsale.app.Models;

import android.location.Location;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.GeoPoint;

import java.io.Serializable;

public class SalesItem implements Serializable {

    private String description;
    private double price;
    private String user;
    private String image;
    private Location location;
    private String title;
    public String path;

    public SalesItem()
    {
    }

    public SalesItem(String title, String desc, float price, String user, String image, Location location,
                     String path){
        this.title = title;
        this.description = desc;
        this.price = price;
        this.user = user;
        this.image = image;
        this.location = location;
        this.path = path;
    }

    /*
    public static SalesItem fromSnapshot(DocumentSnapshot d) {
        SalesItem s = new SalesItem(d.get("title").toString(), d.get("description").toString(),
                Float.parseFloat(d.get("price").toString()),
                d.get("user").toString(), d.get("image").toString(),
                createLocationPoint(d.getGeoPoint("location")),
                d.getReference().getPath());
        return s;
    }

     */

    public static Location createLocationPoint(GeoPoint geoPoint)
    {
        Location newLocation = new Location("");
        newLocation.setLongitude(geoPoint.getLongitude());
        newLocation.setLatitude(geoPoint.getLatitude());
        return newLocation;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setPath(String path) { this.path = path;}

    public String getPath(){return this.path;}

}
