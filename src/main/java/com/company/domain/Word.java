package com.company.domain;

import lombok.*;

import java.util.List;

@Getter
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor


public class Word {

    private String id;
    private String userId;
    private String word;
    private String  translate;
    private String description;
    private List<String> examples;
}
