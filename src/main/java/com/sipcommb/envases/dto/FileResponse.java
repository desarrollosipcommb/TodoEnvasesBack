package com.sipcommb.envases.dto;

public class FileResponse {
    private String fileName;
    private String message;

    public FileResponse() {
    }

    public FileResponse(String fileName, String message) {
        this.fileName = fileName;
        this.message = message;
    }

    public String getFileName() {
        return fileName;
    }

    public String getMessage() {
        return message;
    }
}
