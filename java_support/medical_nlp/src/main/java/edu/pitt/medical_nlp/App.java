package edu.pitt.medical_nlp;

import edu.pitt.medical_nlp.utility.Module;

public class App {
	public static void main(String[] args) {
		// Process p = new Process();
		// p.extractAspect();
		// p.printAspects(0);
		Module.getInst();
		PostProcess postProcess = new PostProcess();
		postProcess.postProcessDocs("ndata_add_features_add_reverse_phrase_stem_text_v4.txt");
	}
}
