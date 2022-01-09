package com.matrimony.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;

@Component
public class ResponseBuilder<T> implements Serializable {

    private boolean success;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer totalResults;
    private T result;


    public ResponseBuilder<T> returnSuccess(T body){
        ResponseBuilder responseBuilder = new ResponseBuilder();
        responseBuilder.setSuccess(true);
        if(body instanceof List){
            responseBuilder.setTotalResults(((List) body).size());
        }
        responseBuilder.setResult(body);

        return responseBuilder;
    }

    public ResponseBuilder<T> returnFailure(T body){
        ResponseBuilder responseBuilder = new ResponseBuilder();
        responseBuilder.setResult(body);
        responseBuilder.setSuccess(false);

        return responseBuilder;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Integer getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(Integer totalResults) {
        this.totalResults = totalResults;
    }
}
