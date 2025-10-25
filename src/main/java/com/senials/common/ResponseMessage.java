package com.senials.common;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class ResponseMessage {

    private int status;
    private String message;
    private Map<String, Object> results;

    public ResponseMessage(int status, String message, Map<String, Object> results) {
        this.status = status;
        this.message = message;
        this.results = results;
    }
}
