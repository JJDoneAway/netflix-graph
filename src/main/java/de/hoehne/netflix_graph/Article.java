package de.hoehne.netflix_graph;

public class Article {
	public String article;
	public String variant;
	public String bundle;
	public String gtin;
	public String subsysNo;

	@Override
	public String toString() {
		return String.format("ArticleNo %s -> VarinatNo %s -> BundleNo %s -> [GTIN %s, SubSysNo %s]", article, variant,
				bundle, gtin, subsysNo);
	}

}