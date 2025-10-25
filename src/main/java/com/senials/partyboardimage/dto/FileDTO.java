package com.senials.partyboardimage.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class FileDTO {

    private String originFileName;

    private String savedFileName;

    private String filePath;

    /* AllArgsConstructor */
    public FileDTO(String originFileName, String savedFileName, String filePath) {
        this.originFileName = originFileName;
        this.savedFileName = savedFileName;
        this.filePath = filePath;
    }
}
