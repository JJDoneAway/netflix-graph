package de.hoehne.netflix_graph.classical;

import java.io.File;

public class TryOutClassical {

	public static void main(String[] args) throws Exception {

		long start = System.nanoTime();
		KeyProviderClassic.restoreDB(new File("./src/main/resources/"));
		System.out.printf("It took %dns to restore %s\n", System.nanoTime() - start,
				KeyProviderClassic.me.toString());

		start = System.nanoTime();
		final String gtin = "4046802210250";
		String artNo = KeyProviderClassic.getArticleForGtin(gtin); 
		// 136426
		System.out.printf("It took %dns to get %s as article number for gtin %s\n", (System.nanoTime() - start), artNo , gtin, artNo);
		
		
		start = System.nanoTime();
		final String subSys = "789437";
		artNo = KeyProviderClassic.getArticleForSubSys(subSys);
		System.out.printf("It took %dns to get %s as article number for subsys %s\n", (System.nanoTime() - start), artNo , subSys, artNo);

	}
}
