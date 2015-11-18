package edu.pitt.medical_nlp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import edu.pitt.medical_nlp.graph.Graph;
import edu.pitt.medical_nlp.graph.WordNode;
import edu.pitt.medical_nlp.utility.DependencyType;
import edu.pitt.medical_nlp.utility.Helper;
import edu.pitt.medical_nlp.utility.Module;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.parser.nndep.DependencyParser;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructure.Extras;
import edu.stanford.nlp.trees.TypedDependency;

public class PostProcess extends Process {

	public PostProcess() {
		super();
	}

	public void postProcessDocs(String filename) {
		try {
			super.processDocs();
			postProcessSingleDocs();
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(filename)));

			for (int i = 0; i < _docs_processed.size(); i++) {
				List<List<WordNode>> record = _docs_processed.get(i);
				for (List<WordNode> sentence : record) {
					for (HasWord word : sentence) {
						writer.write(word.word());
						writer.write(" ");
					}
				}
				if (Config.ADD_RELATION) {
					for (String feature : added_record_features.get(i)) {
						writer.write(feature);
						writer.write(" ");
					}
				}
				writer.write("\n");
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			System.out.println("Done!");
		}
	}

	public void postProcessSingleDocs() {
		MaxentTagger tagger = Module.getInst().getTagger();
		DependencyParser parser = Module.getInst().getDependencyParser();
		for (int i = 0; i < _docs_processed.size(); i++) {
			List<List<WordNode>> record = _docs_processed.get(i);
			Graph graph = new Graph();
			for (List<WordNode> sentence : record) {
				List<TaggedWord> tagged = tagger.tagSentence(sentence);
				GrammaticalStructure gs = parser.predict(tagged);
				for (TypedDependency typed_dependence : gs.typedDependenciesCCprocessed(Extras.MAXIMAL)) {
					int idx_gov = typed_dependence.gov().index() - 1;
					int idx_dep = typed_dependence.dep().index() - 1;
					if (idx_dep >= 0 && idx_gov >= 0 && idx_dep < sentence.size() && idx_gov < sentence.size()) {
						WordNode node_gov = sentence.get(idx_gov);
						WordNode node_dep = sentence.get(idx_dep);
						String lemma_gov = typed_dependence.gov().word(), lemma_dep = typed_dependence.dep().word();
						if (!node_gov.word().equals(lemma_gov) || !node_dep.word().equals(lemma_dep)) {
							// intermediate checking !
							System.err.println(sentence);
						}
						DependencyType dependency_type = Helper
								.mapRelationTypes(typed_dependence.reln().getShortName());
						switch (dependency_type) {
						case AdjectiveModifer:
							graph.createEdge(node_dep, node_gov, dependency_type);
							// graph.createEdge(lemma_dep, lemma_gov, idx_dep,
							// idx_gov, dependency_type);
							break;
						case Negative:
							graph.createEdge(node_dep, node_gov, dependency_type);
							// graph.createEdge(lemma_dep, lemma_gov, idx_dep,
							// idx_gov, dependency_type);
							break;
						case NominalSubject:
							graph.createEdge(node_gov, node_dep, dependency_type);
							// graph.createEdge(lemma_gov, lemma_dep, idx_gov,
							// idx_dep, dependency_type);
							break;
						case Compound:
							graph.createEdge(node_dep, node_gov, dependency_type);
							// graph.createEdge(lemma_dep, lemma_gov, idx_dep,
							// idx_gov, dependency_type);
							break;
						default:
							break;
						}
					}
				}
			}
			added_record_features.get(i).addAll(graph.generateFeatures());
		}
	}

}
