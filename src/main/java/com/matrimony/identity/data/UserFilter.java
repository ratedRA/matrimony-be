package com.matrimony.identity.data;

import io.swagger.models.auth.In;

import java.io.Serializable;

public class UserFilter implements Serializable {
    public static final Integer DEFAULT_MAX_AGE = Integer.MAX_VALUE;
    public static final Integer DEFAULT_MIN_AGE = Integer.MIN_VALUE;
    private Integer minAge;
    private Integer maxAge;

    private Gender gender;

    private MaritalStatus maritalStatus;

    // handles - country, tags, socialIds
    private String searchTerm;

    private Integer pageStart;
    private Integer pageSize;

    public Integer getMinAge() {
        return minAge;
    }

    public void setMinAge(Integer minAge) {
        this.minAge = minAge;
    }

    public Integer getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(Integer maxAge) {
        this.maxAge = maxAge;
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

    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getPageStart() {
        return pageStart;
    }

    public void setPageStart(Integer pageStart) {
        this.pageStart = pageStart;
    }
}
