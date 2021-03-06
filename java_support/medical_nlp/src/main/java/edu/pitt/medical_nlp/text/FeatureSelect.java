package edu.pitt.medical_nlp.text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class FeatureSelect {
	public HashMap<String, ArrayList<Integer>> code_map = new HashMap<>();
	public ArrayList<HashSet<String>> texts = new ArrayList<>();

	public FeatureSelect() {
		init("ADD_SYNONYMfalse_ADD_PHRASEtrue_ADD_TYPEfalse_ADD_RELATIONtrue.txt", "codes.txt");
	}

	void init(String path_data, String path_code) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(path_data)));
			String line = null;
			while (null != (line = reader.readLine())) {
				HashSet<String> features = new HashSet<>(Arrays.asList(line.split(" ")));
				texts.add(features);
			}
			reader.close();

			reader = new BufferedReader(new FileReader(new File(path_code)));
			line = null;
			int idx_line = 0;
			while (null != (line = reader.readLine())) {
				String[] codes = line.split(" ");
				for (String code : codes) {
					if (!code_map.containsKey(code)) {
						code_map.put(code, new ArrayList<>());
					}
					code_map.get(code).add(idx_line);
				}
				++idx_line;

			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void select(double threshold) {
		for (String code : code_map.keySet()) {
			boolean good_code = false;
			// build helper for each code
			HashMap<String, SelectWrap> obj = new HashMap<>();
			// loop over all features within code
			ArrayList<Integer> idx_contains = code_map.get(code);
			for (Integer idx_contain : idx_contains) {
				for (String feature : texts.get(idx_contain)) {
					if (!obj.containsKey(feature)) {
						obj.put(feature, new SelectWrap(feature));
					}
					obj.get(feature).idx_contain.add(idx_contain);
				}
			}
			// loop over texts without code
			for (int i = 0; i < 978; i++) {
				if (!idx_contains.contains(i)) {
					for (String feature : texts.get(i)) {
						if (obj.containsKey(feature)) {
							obj.get(feature).idx_exclude.add(i);
						}
					}
				}
			}
			// do the computation
			for (String feature : obj.keySet()) {
				double pos = obj.get(feature).idx_contain.size(), neg = obj.get(feature).idx_exclude.size();
				if (pos / neg > threshold) {
					System.out.println(code + "\t" + feature + "\t" + pos + "\t" + neg);
					good_code = true;
				}
			}
			if (!good_code) {
				//System.out.println(code);
			}
		}
	}
}

class SelectWrap {
	public String feature;
	public ArrayList<Integer> idx_contain = new ArrayList<>(), idx_exclude = new ArrayList<>();

	public SelectWrap(String feature) {
		this.feature = feature;
	}
}
