package com.company.domain;

import lombok.Data;

import java.util.List;

@Data
public class DefinitionsItem{
	private List<Object> synonyms;
	private List<Object> antonyms;
	private String definition="";
	private String example="";
}