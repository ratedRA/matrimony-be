package com.matrimony.identity.data;

import java.util.Date;

public class UserOtpDetail {
    private String lastSentOtp;
    private Date lastOtpSentDate;

    public UserOtpDetail(String lastSentOtp, Date lastOtpSentDate) {
        this.lastSentOtp = lastSentOtp;
        this.lastOtpSentDate = lastOtpSentDate;
    }

    public String getLastSentOtp() {
        return lastSentOtp;
    }

    public void setLastSentOtp(String lastSentOtp) {
        this.lastSentOtp = lastSentOtp;
    }

    public Date getLastOtpSentDate() {
        return lastOtpSentDate;
    }

    public void setLastOtpSentDate(Date lastOtpSentDate) {
        this.lastOtpSentDate = lastOtpSentDate;
    }
}
