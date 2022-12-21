package com.company.domain;

import lombok.Data;

import java.util.List;

@Data
public class MeaningsItem{
	private String partOfSpeech;
	private List<DefinitionsItem> definitions;
}