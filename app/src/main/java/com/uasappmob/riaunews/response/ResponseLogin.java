package com.uasappmob.riaunews.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.uasappmob.riaunews.model.User;

public class ResponseLogin {

    @SerializedName("status")
    @Expose
    private Boolean status;

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("data")
    @Expose
    private User user;

    public Boolean getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public User getUser() {
        return user;
    }
}

