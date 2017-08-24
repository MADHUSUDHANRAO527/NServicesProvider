package mobile.app.nservicesprovider.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by madhu on 27/07/17.
 */

public class LoginBaseModel {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("mobile")
    @Expose
    private String mobile;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("icon")
    @Expose
    private String icon;

    public String getJobsCompleted() {
        return jobsCompleted;
    }

    public void setJobsCompleted(String jobsCompleted) {
        this.jobsCompleted = jobsCompleted;
    }

    @SerializedName("job_completed")
    @Expose
    private String jobsCompleted;
    @SerializedName("services")
    @Expose
    private List<SpServicesModel> services = null;
    @SerializedName("rating")
    @Expose
    private double rating;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public List<SpServicesModel> getServices() {
        return services;
    }

    public void setServices(List<SpServicesModel> services) {
        this.services = services;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

}