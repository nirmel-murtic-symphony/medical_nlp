package edu.pitt.medical_nlp.text;

import edu.pitt.medical_nlp.utility.Config;
import edu.pitt.medical_nlp.utility.Module;

public class App {
	public static void main(String[] args) {
		// Process p = new Process();
		// p.extractAspect();
		// p.printAspects(0);
		Module.getInst();
		PostProcess postProcess = new PostProcess();
		String filename = "ADD_SYNONYM" + Config.ADD_SYNONYM + "_ADD_PHRASE" + Config.ADD_PHRASE + "_ADD_TYPE"
				+ Config.ADD_TYPE + "_ADD_RELATION" + Config.ADD_RELATION + ".txt";
		postProcess.postProcessDocs(filename);
	}
}