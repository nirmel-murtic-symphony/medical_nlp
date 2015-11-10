package edu.pitt.medical_nlp.utility;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;

import net.didion.jwnl.JWNL;
import net.didion.jwnl.JWNLException;
import net.didion.jwnl.data.IndexWord;
import net.didion.jwnl.data.IndexWordSet;
import net.didion.jwnl.data.POS;
import net.didion.jwnl.dictionary.Dictionary;
import net.didion.jwnl.dictionary.MorphologicalProcessor;

public class WordNetUtility {
	static HashMap<String, String> cache = new HashMap<>();

	public static String getStem(String raw_word) {
		String nword = "";
		String[] words = raw_word.split(" ");
		for (String word : words) {
			nword += getSingleStem(word) + " ";
		}
		return nword.substring(0, nword.length() - 1);
	}

	public static String getSingleStem(String word) {
		if (cache.containsKey(word)) {
			return cache.get(word);
		}
		
		try {
			String nword = null;
			MorphologicalProcessor mop = Dictionary.getInstance().getMorphologicalProcessor();
			IndexWord w = mop.lookupBaseForm(POS.NOUN, word);
			if (w != null) {
				nword = w.getLemma();
				cache.put(word, nword);
				return nword;
			}
			w = mop.lookupBaseForm(POS.VERB, word);
			if (w != null) {
				nword = w.getLemma();
				cache.put(word, nword);
				return nword;
			}
			w = mop.lookupBaseForm(POS.ADJECTIVE, word);
			if (w != null) {
				nword = w.getLemma();
				cache.put(word, nword);
				return nword;
			}
			w = mop.lookupBaseForm(POS.ADVERB, word);
			if (w != null) {
				nword = w.getLemma();
				cache.put(word, nword);
				return nword;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return word;
	}

}