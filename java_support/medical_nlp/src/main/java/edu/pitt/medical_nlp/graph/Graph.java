package edu.pitt.medical_nlp.graph;

import java.util.ArrayList;
import java.util.HashMap;

import edu.pitt.medical_nlp.utility.DependencyType;

public class Graph {
	HashMap<Integer, WordNode> nodes = new HashMap<>();
	ArrayList<Edge> edges = new ArrayList<>();

	public Edge createEdge(WordNode node_adj, WordNode node_n, DependencyType type) {
		Edge edge = new Edge(node_adj, node_n, type);
		if (!nodes.containsKey(node_adj)) {
			nodes.put(node_adj.idx, node_adj);
		}
		if (!nodes.containsKey(node_n)) {
			nodes.put(node_n.idx, node_n);
		}
		edges.add(edge);
		node_adj.links.add(edge);
		node_n.links.add(edge);
		return edge;
	}

	@Deprecated
	public WordNode createNode(int idx, String lemma) {
		if (nodes.containsKey(idx)) {
			return nodes.get(idx);
		} else {
			WordNode node = new WordNode(idx, lemma);
			nodes.put(idx, node);
			return node;
		}
	}

	@Deprecated
	public Edge createEdge(String lemma_adj, String lemma_n, int idx_adj, int idx_n, DependencyType type) {
		WordNode node_adj = createNode(idx_adj, lemma_adj);
		WordNode node_n = createNode(idx_n, lemma_n);

		Edge edge = new Edge(node_adj, node_n, type);
		edges.add(edge);
		node_adj.links.add(edge);
		node_n.links.add(edge);
		return edge;
	}

	public ArrayList<String> generateFeatures() {
		ArrayList<String> features = new ArrayList<>();
		DependencyType[] modifys = { DependencyType.AdjectiveModifer, DependencyType.NominalSubject,
				DependencyType.Negative };

		for (DependencyType dependencyType : modifys) {
			for (Edge edge : edges) {
				if (edge.type == dependencyType) {
					String feature = edge.node_adj.lemma + "_" + edge.node_n.lemma;
					features.add(feature);

					for (Edge edge_loop : edge.node_n.links) {
						if (edge_loop.type == DependencyType.Compound) {
							String feature_loop = edge.node_adj.lemma + "_" + edge_loop.getOtherNode(edge.node_n).lemma;
							features.add(feature_loop);
						}
					}

				}
			}
		}
		return features;
	}
}
