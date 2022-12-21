package com.company.domain;

import lombok.Data;

import java.util.List;

@Data
public class Translate {

	private String phonetic;
	private String origin;
	private List<PhoneticsItem> phonetics;
	private String word;
	private List<MeaningsItem> meanings;

}