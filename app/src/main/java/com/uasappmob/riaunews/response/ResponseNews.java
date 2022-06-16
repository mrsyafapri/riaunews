package com.uasappmob.riaunews.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResponseNews<T> {
    @SerializedName("status")
    @Expose
    private Boolean status;

    @SerializedName("result")
    @Expose
    private T result;

    public Boolean getStatus() {
        return status;
    }

    public T getResult() {
        return result;
    }
}
