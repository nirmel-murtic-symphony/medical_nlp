package edu.pitt.medical_nlp.feature;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Stat {
	public static void summary() {
		HashMap<String, Integer> count = new HashMap<>();
		try {
			BufferedReader reader = new BufferedReader(
					new FileReader(new File("ADD_SYNONYMfalse_ADD_PHRASEtrue_ADD_TYPEfalse_ADD_RELATIONtrue.txt")));
			String line = null;
			while (null != (line = reader.readLine())) {
				for (String feature : line.split(" ")) {
					if (feature.indexOf("_") != -1) {
						if (!count.containsKey(feature)) {
							count.put(feature, 0);
						}
						count.put(feature, 1 + count.get(feature));
					}
				}
			}
			reader.close();

			ArrayList<Map.Entry<String, Integer>> list = new ArrayList<>();
			list.addAll(count.entrySet());
			Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {

				@Override
				public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
					return o2.getValue() - o1.getValue();
				}
			});
			for (Entry<String, Integer> entry : list) {
				System.out.println(entry.getKey() + ":" + entry.getValue());
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		summary();
	}
}