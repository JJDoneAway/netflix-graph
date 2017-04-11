package de.hoehne.netflix_graph.classical;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import de.hoehne.netflix_graph.Article;

public class KeyProviderClassic implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3715902314344686334L;

	private KeyProviderClassic() {
	}

	public static KeyProviderClassic me = new KeyProviderClassic();

	public Map<String, String> gtinToBundle = new HashMap<String, String>();
	public Map<String, String> bundleToGtin = new HashMap<String, String>();

	public Map<String, String> bundleToSubsys = new HashMap<String, String>();
	public Map<String, String> susysToBundle = new HashMap<String, String>();

	public Map<String, String> bundleToVariant = new HashMap<String, String>();
	public MultiMap variantToBundle = new MultiMap();

	public Map<String, String> variantToArticle = new HashMap<String, String>();
	public MultiMap articleToVarinant = new MultiMap();

	/**
	 * adding a new article key set
	 * 
	 * @param data.article
	 * @param data.variant
	 * @param data.bundle
	 * @param data.gtin
	 * @param subsys
	 */
	public void add(Article art) {
		if (art == null) {
			return;
		}
		articleToVarinant.putNode(art.article, art.variant);
		variantToArticle.put(art.variant, art.article);

		variantToBundle.putNode(art.variant, art.bundle);
		bundleToVariant.put(art.bundle, art.variant);

		if (art.gtin != null && !art.gtin.trim().equals("")) {
			bundleToGtin.put(art.bundle, art.gtin);
			gtinToBundle.put(art.gtin, art.bundle);
		}

		if (art.subsysNo != null && !art.subsysNo.trim().equals("")) {
			bundleToSubsys.put(art.bundle, art.subsysNo);
			susysToBundle.put(art.subsysNo, art.bundle);
		}

	}

	public static String getArticleForGtin(String gtin) {
		return me.variantToArticle.get(me.bundleToVariant.get(me.gtinToBundle.get(gtin)));
	}

	public static String getArticleForSubSys(String subSys) {
		return me.variantToArticle.get(me.bundleToVariant.get(me.susysToBundle.get(subSys)));
	}

	public static void persistDB(File location) throws Exception {
		FileOutputStream fos = new FileOutputStream(new File(location, "KeyProviderClassic.ser"));
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(me);
		fos.close();
	}

	public static void restoreDB(File location) throws Exception {
		FileInputStream fis = new FileInputStream(new File(location, "KeyProviderClassic.ser"));
		ObjectInputStream oos = new ObjectInputStream(fis);
		me = (KeyProviderClassic) oos.readObject();
		fis.close();
	}

	@Override
	public String toString() {
		return String.format("%s articles, %s variants, %s bundles, %s gtins, %s subsys", articleToVarinant.size(),
				variantToBundle.size(), bundleToVariant.size(), gtinToBundle.size(), susysToBundle.size());
	}


}
