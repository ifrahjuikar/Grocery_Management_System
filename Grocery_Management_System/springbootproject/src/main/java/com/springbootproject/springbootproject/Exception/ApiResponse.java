package com.springbootproject.springbootproject.Exception;

public class ApiResponse {
    // Fields representing the status, error code, message, and data of the response
    private String status; // Status of the response (e.g., "Success" or "Error")
    private int statusCode; // Numeric code representing the error
    private String statusMessage; // Description of the status or error message
    private Object data; // Any additional data to be included in the response

    // Constructor to initialize the fields
    public ApiResponse(String status, int statusCode, String statusMessage, Object data) {
        this.status = status;
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
        this.data = data;
    }

    // Getter for status
    public String getStatus() {
        return status;
    }

    // Setter for status
    public void setStatus(String status) {
        this.status = status;
    }

    // Getter for errorCode
    public int getStatusCode() {
        return statusCode;
    }

    // Setter for errorCode
    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    // Getter for statusMessage
    public String getStatusMessage() {
        return statusMessage;
    }

    // Setter for statusMessage
    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    // Getter for data
    public Object getData() {
        return data;
    }

    // Setter for data
    public void setData(Object data) {
        this.data = data;
    }
}
