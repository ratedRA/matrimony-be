package com.matrimony.identity.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.matrimony.identity.data.Gender;
import com.matrimony.identity.data.MaritalStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Document(value = "user")
public class MatrimonyUser implements Serializable {

    @Id
    private String id;

    // credentials
    // private String userName -> should be same as phone number;
    @JsonIgnore
    private String password;
    private String authenticationToken;

    // personal info
    private String firstName;
    private String middleName;
    private String lastName;
    private Integer age;
    private Gender gender;
    private MaritalStatus maritalStatus;
    private String bio;
    private List<String> tags;

    // contact info
    @Indexed(unique = true)
    private String phoneNumber;
    private String countryCode;
    private String country;
    private String State;
    private String District;

    // site info
    private Boolean verified = Boolean.FALSE;
    private String lastSentOtp;
    private Date lastSentOtpDate;

    // social info
    private String instagramId;
    private String facebookId;
    private String pinterestId;
    private String snapchatId;

    // registration info
    private Date createdDate;
    private Date modifiedDate;

    public MatrimonyUser(String phoneNumber, String countryCode) {
        this.phoneNumber = phoneNumber;
        this.countryCode = countryCode;
    }

    public String getPassword() {
        return password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return id;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getState() {
        return State;
    }

    public void setState(String state) {
        State = state;
    }

    public String getDistrict() {
        return District;
    }

    public void setDistrict(String district) {
        District = district;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public String getLastSentOtp() {
        return lastSentOtp;
    }

    public void setLastSentOtp(String lastSentOtp) {
        this.lastSentOtp = lastSentOtp;
    }

    public Date getLastSentOtpDate() {
        return lastSentOtpDate;
    }

    public void setLastSentOtpDate(Date lastSentOtpDate) {
        this.lastSentOtpDate = lastSentOtpDate;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public String getAuthenticationToken() {
        return authenticationToken;
    }

    public void setAuthenticationToken(String authenticationToken) {
        this.authenticationToken = authenticationToken;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public MaritalStatus getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(MaritalStatus maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getInstagramId() {
        return instagramId;
    }

    public void setInstagramId(String instagramId) {
        this.instagramId = instagramId;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

    public String getPinterestId() {
        return pinterestId;
    }

    public void setPinterestId(String pinterestId) {
        this.pinterestId = pinterestId;
    }

    public String getSnapchatId() {
        return snapchatId;
    }

    public void setSnapchatId(String snapchatId) {
        this.snapchatId = snapchatId;
    }
}
