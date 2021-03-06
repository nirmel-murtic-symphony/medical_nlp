package edu.pitt.medical_nlp.graph;

import java.util.ArrayList;

import edu.stanford.nlp.ling.HasWord;

public class WordNode implements HasWord {

	public int idx;
	public String lemma;
	public String core ;
	public String type;
	public String snomed_id;
	public ArrayList<Edge> links = new ArrayList<>();
	
	//features 
	//pos 0 : ignore
	public int features = 0;

	public WordNode(int idx, String lemma) {
		this.idx = idx;
		this.lemma = lemma;
	}

	public WordNode(String type, String snomed_id,int idx, String lemma,String core) {
		this.lemma = lemma;
		this.snomed_id = snomed_id;
		this.type = type;
		this.idx = idx;
		this.core = core;
	}

	@Override
	public String word() {
		return lemma;
	}

	@Override
	public void setWord(String word) {
		this.lemma = word;
	}

	private static final long serialVersionUID = -4139502897206203482L;
}
