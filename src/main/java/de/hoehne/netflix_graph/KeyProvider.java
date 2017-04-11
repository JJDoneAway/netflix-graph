package de.hoehne.netflix_graph;

import static com.netflix.nfgraph.spec.NFPropertySpec.COMPACT;
import static com.netflix.nfgraph.spec.NFPropertySpec.MULTIPLE;
import static com.netflix.nfgraph.spec.NFPropertySpec.SINGLE;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;

import com.netflix.nfgraph.OrdinalIterator;
import com.netflix.nfgraph.build.NFBuildGraph;
import com.netflix.nfgraph.compressed.NFCompressedGraph;
import com.netflix.nfgraph.spec.NFGraphSpec;
import com.netflix.nfgraph.spec.NFNodeSpec;
import com.netflix.nfgraph.spec.NFPropertySpec;
import com.netflix.nfgraph.util.OrdinalMap;

public class KeyProvider implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3715902314344686334L;

	public static NFGraphSpec mmsKeySchema = new NFGraphSpec(
			new NFNodeSpec("Article", new NFPropertySpec("has_variant", "Variant", MULTIPLE | COMPACT)),
			new NFNodeSpec("Variant", //
					new NFPropertySpec("has_article", "Article", SINGLE),
					new NFPropertySpec("has_bundle", "Bundle", MULTIPLE | COMPACT)),
			new NFNodeSpec("Bundle", //
					new NFPropertySpec("has_variant", "Variant", SINGLE), //
					new NFPropertySpec("has_gtin", "GTIN", SINGLE), //
					new NFPropertySpec("has_subsys", "SubSys", SINGLE)),
			new NFNodeSpec("GTIN", new NFPropertySpec("has_bundle", "Bundle", SINGLE)),
			new NFNodeSpec("SubSys", new NFPropertySpec("has_bundle", "Bundle", SINGLE)));

	public static NFBuildGraph buildGraph = new NFBuildGraph(mmsKeySchema);

	public static OrdinalMap<String> articles = new OrdinalMap<String>();
	public static OrdinalMap<String> variants = new OrdinalMap<String>();
	public static OrdinalMap<String> bundles = new OrdinalMap<String>();
	public static OrdinalMap<String> gtins = new OrdinalMap<String>();
	public static OrdinalMap<String> subsys = new OrdinalMap<String>();

	public static NFCompressedGraph compressedGraph;

	private KeyProvider() {
	}

	public static void add(Article art) {
		if (art == null) {
			return;
		}

		buildGraph.addConnection("Article", articles.add(art.article), "has_variant", variants.add(art.variant));
		buildGraph.addConnection("Variant", variants.add(art.variant), "has_article", articles.add(art.article));

		buildGraph.addConnection("Variant", variants.add(art.variant), "has_bundle", bundles.add(art.bundle));
		buildGraph.addConnection("Bundle", bundles.add(art.bundle), "has_variant", variants.add(art.variant));

		if (art.gtin != null && !art.gtin.trim().equals("")) {
			buildGraph.addConnection("Bundle", bundles.add(art.bundle), "has_gtin", gtins.add(art.gtin));
			buildGraph.addConnection("GTIN", gtins.add(art.gtin), "has_bundle", bundles.add(art.bundle));
		}

		if (art.subsysNo != null && !art.subsysNo.trim().equals("")) {
			buildGraph.addConnection("Bundle", bundles.add(art.bundle), "has_subsys", subsys.add(art.subsysNo));
			buildGraph.addConnection("SubSys", subsys.add(art.subsysNo), "has_bundle", bundles.add(art.bundle));
		}

	}

	public static String getArticleForGtin(String gtin) {
		OrdinalIterator iter = compressedGraph.getConnectionIterator("GTIN", gtins.get(gtin), "has_bundle");
		iter = compressedGraph.getConnectionIterator("Bundle", iter.nextOrdinal(), "has_variant");
		iter = compressedGraph.getConnectionIterator("Variant", iter.nextOrdinal(), "has_article");

		return articles.get(iter.nextOrdinal());
	}

	public static String getArticleForSubSys(String subSys) {
		OrdinalIterator iter = compressedGraph.getConnectionIterator("SubSys", subsys.get(subSys), "has_bundle");
		iter = compressedGraph.getConnectionIterator("Bundle", iter.nextOrdinal(), "has_variant");
		iter = compressedGraph.getConnectionIterator("Variant", iter.nextOrdinal(), "has_article");

		return articles.get(iter.nextOrdinal());
	}

	public static void persistDB(File location) throws Exception {
		compressedGraph = buildGraph.compress();

		FileOutputStream fos = new FileOutputStream(new File(location, "KeyProvider.ser"));
		DataOutputStream dos = new DataOutputStream(fos);
		serializeNodeDetails(articles, dos);
		serializeNodeDetails(variants, dos);
		serializeNodeDetails(bundles, dos);
		serializeNodeDetails(gtins, dos);
		serializeNodeDetails(subsys, dos);
		compressedGraph.writeTo(dos);
		fos.close();
	}

	private static void serializeNodeDetails(OrdinalMap<String> nodeMap, DataOutputStream out) throws Exception {
		out.writeInt(nodeMap.size());
		for (String node : nodeMap) {
			out.writeUTF(node);
		}
	}

	public static void restoreDB(File location) throws Exception {
		FileInputStream fis = new FileInputStream(new File(location, "KeyProvider.ser"));
		DataInputStream is = new DataInputStream(fis);
		articles = deserializeNodeDetails(is);
		variants = deserializeNodeDetails(is);
		bundles = deserializeNodeDetails(is);
		gtins = deserializeNodeDetails(is);
		subsys = deserializeNodeDetails(is);
		compressedGraph = NFCompressedGraph.readFrom(is);
		fis.close();
	}

	private static OrdinalMap<String> deserializeNodeDetails(DataInputStream is) throws IOException {
		int size = is.readInt();
		OrdinalMap<String> map = new OrdinalMap<String>(size);
		for (int i = 0; i < size; i++) {
			map.add(is.readUTF());
		}
		return map;
	}

	public static String info() {
		return String.format("%s articles, %s variants, %s bundles, %s gtins, %s subsys", articles.size(),
				variants.size(), bundles.size(), gtins.size(), subsys.size());
	}

}
