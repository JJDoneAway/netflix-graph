package de.hoehne.netflix_graph.classical;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * very simple multi map implementation which is just a example but far away
 * from being a good implementation
 * 
 * @author johannes
 *
 */
public class MultiMap extends HashMap<String, Set<String>> {

	private static final long serialVersionUID = 1L;

	public void putNode(String sourceNode, String targetNode) {

		if (!this.containsKey(sourceNode)) {
			this.put(sourceNode, new HashSet<String>());
		}

		this.get(sourceNode).add(targetNode);

	}
}
