package de.hoehne.netflix_graph;

import java.io.File;

import de.hoehne.netflix_graph.classical.KeyProviderClassic;

public class TryOut {

	public static void main(String[] args) throws Exception {
		final String gtin = "4046802210250";
		final String subSys = "789437";

		// netflix graph
		System.out.println("Netflix Graph approach");
		long start = System.nanoTime();
		KeyProvider.restoreDB(new File("./src/main/resources/"));
		System.out.printf("It took %d10 ns to restore %s\n", System.nanoTime() - start, KeyProvider.info());

		start = System.nanoTime();
		String artNo = KeyProvider.getArticleForGtin(gtin);
		// 136426
		System.out.printf("It took %d10 ns to get %s as article number for gtin %s\n", (System.nanoTime() - start),
				artNo, gtin, artNo);

		start = System.nanoTime();
		artNo = KeyProvider.getArticleForSubSys(subSys);
		System.out.printf("It took %d10 ns to get %s as article number for subsys %s\n", (System.nanoTime() - start),
				artNo, subSys, artNo);

		// classical
		System.out.println("Classical approach");
		start = System.nanoTime();
		KeyProviderClassic.restoreDB(new File("./src/main/resources/"));
		System.out.printf("It took %d10 ns to restore %s\n", System.nanoTime() - start,
				KeyProviderClassic.me.toString());

		start = System.nanoTime();
		artNo = KeyProviderClassic.getArticleForGtin(gtin);
		// 136426
		System.out.printf("It took %d10 ns to get %s as article number for gtin %s\n", (System.nanoTime() - start),
				artNo, gtin, artNo);

		start = System.nanoTime();
		artNo = KeyProviderClassic.getArticleForSubSys(subSys);
		System.out.printf("It took %d10 ns to get %s as article number for subsys %s\n", (System.nanoTime() - start),
				artNo, subSys, artNo);

	}
}
