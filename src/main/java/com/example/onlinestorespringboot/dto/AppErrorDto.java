package com.example.onlinestorespringboot.dto;

import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


@Data
public class AppErrorDto {

    private String message;

    private String timestamp;

    public AppErrorDto(String message){
        this.message=message;
        this.timestamp=formatTimestamp();
    }

    private String formatTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", new Locale("ru", "RU"));
        sdf.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"));
        return sdf.format(new Date());
    }
}

