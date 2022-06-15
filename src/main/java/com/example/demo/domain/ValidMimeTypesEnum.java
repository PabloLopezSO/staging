package com.example.demo.domain;

public enum ValidMimeTypesEnum {
    
    MIME_APPLICATION_PDF("application/pdf"), 
    MIME_IMAGE_JPEG("image/jpeg"), 
    MIME_TEXT_PLAIN("text/plain");

    private final String fileType;

    private ValidMimeTypesEnum(String fileType) {
        this.fileType = fileType;
    }
 
    public static String getEnumByString(String code) {
        for(ValidMimeTypesEnum e : ValidMimeTypesEnum.values()){
            if(e.fileType.equalsIgnoreCase(code)) return e.name();
        }
        return null;
    }
}
