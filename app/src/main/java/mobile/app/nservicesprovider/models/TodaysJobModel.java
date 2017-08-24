package mobile.app.nservicesprovider.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Madhu on 27/07/17.
 */

public class TodaysJobModel {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("consumer_id")
    @Expose
    private String consumerId;
    @SerializedName("location")
    @Expose
    private String location;
    @SerializedName("booking_time")
    @Expose
    private String bookingTime;
    @SerializedName("service_id")
    @Expose
    private String serviceId;
    @SerializedName("consumer_lat")
    @Expose
    private String consumerLat;
    @SerializedName("consumer_lng")
    @Expose
    private String consumerLng;
    @SerializedName("service_req_date")
    @Expose
    private String serviceReqDate;
    @SerializedName("req_details")
    @Expose
    private String reqDetails;
    @SerializedName("req_notes")
    @Expose
    private Object reqNotes;
    @SerializedName("service_provider_id")
    @Expose
    private String serviceProviderId;
    @SerializedName("price_type")
    @Expose
    private String priceType;
    @SerializedName("price")
    @Expose
    private String price;
    @SerializedName("amount")
    @Expose
    private String amount;
    @SerializedName("status")
    @Expose
    private String jobStatus;
    @SerializedName("start_time")
    @Expose
    private Object startTime;
    @SerializedName("complete_time")
    @Expose
    private Object completeTime;
    @SerializedName("review_status")
    @Expose
    private String reviewStatus;
    @SerializedName("firstname")
    @Expose
    private String firstname;
    @SerializedName("lastname")
    @Expose
    private String lastname;
    @SerializedName("mobile")
    @Expose
    private String mobile;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("service")
    @Expose
    private String service;

    @SerializedName("display_status")
    @Expose
    private String displayStatus;

    public String getDisplayStatus() {
        return displayStatus;
    }

    public void setDisplayStatus(String displayStatus) {
        this.displayStatus = displayStatus;
    }


    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
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

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getConsumerId() {
        return consumerId;
    }

    public void setConsumerId(String consumerId) {
        this.consumerId = consumerId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getBookingTime() {
        return bookingTime;
    }

    public void setBookingTime(String bookingTime) {
        this.bookingTime = bookingTime;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getConsumerLat() {
        return consumerLat;
    }

    public void setConsumerLat(String consumerLat) {
        this.consumerLat = consumerLat;
    }

    public String getConsumerLng() {
        return consumerLng;
    }

    public void setConsumerLng(String consumerLng) {
        this.consumerLng = consumerLng;
    }

    public String getServiceReqDate() {
        return serviceReqDate;
    }

    public void setServiceReqDate(String serviceReqDate) {
        this.serviceReqDate = serviceReqDate;
    }

    public String getReqDetails() {
        return reqDetails;
    }

    public void setReqDetails(String reqDetails) {
        this.reqDetails = reqDetails;
    }

    public Object getReqNotes() {
        return reqNotes;
    }

    public void setReqNotes(Object reqNotes) {
        this.reqNotes = reqNotes;
    }

    public String getServiceProviderId() {
        return serviceProviderId;
    }

    public void setServiceProviderId(String serviceProviderId) {
        this.serviceProviderId = serviceProviderId;
    }

    public String getPriceType() {
        return priceType;
    }

    public void setPriceType(String priceType) {
        this.priceType = priceType;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(String status) {
        this.jobStatus = status;
    }

    public Object getStartTime() {
        return startTime;
    }

    public void setStartTime(Object startTime) {
        this.startTime = startTime;
    }

    public Object getCompleteTime() {
        return completeTime;
    }

    public void setCompleteTime(Object completeTime) {
        this.completeTime = completeTime;
    }

    public String getReviewStatus() {
        return reviewStatus;
    }

    public void setReviewStatus(String reviewStatus) {
        this.reviewStatus = reviewStatus;
    }

}
