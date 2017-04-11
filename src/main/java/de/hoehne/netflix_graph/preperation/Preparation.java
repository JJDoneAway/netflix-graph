package de.hoehne.netflix_graph.preperation;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.io.FileUtils;

import de.hoehne.netflix_graph.Article;
import de.hoehne.netflix_graph.KeyProvider;

public class Preparation {

	public static void main(String[] args) throws Exception {
		long start = System.currentTimeMillis();
		// Preparation.download();
		loadKeyProvider();
		System.out.printf("It took %d to initiate %s\n", (System.currentTimeMillis() - start),
				KeyProvider.info());

		start = System.currentTimeMillis();
		KeyProvider.persistDB(new File("./src/main/resources"));
		System.out.printf("It took %d to persist it\n", (System.currentTimeMillis() - start));
		
		start = System.nanoTime();
		final String gtin = "4046802210250";
		String artNo = KeyProvider.getArticleForGtin(gtin); 
		// 136426
		System.out.printf("It took %dns to get %s as article number for gtin %s\n", (System.nanoTime() - start), artNo , gtin, artNo);


	}

	public static void loadKeyProvider() {
		AtomicInteger count = new AtomicInteger(0);
		Arrays//
				.asList(new File("/home/johannes/tmp")//
						.listFiles())
				.stream().parallel()//
				.map(file -> {
					try {
						return FileUtils.readFileToString(file);
					} catch (IOException e) {
						e.printStackTrace();
						return null;
					}
				})//
				.map(solr -> getArticle(solr))//
				.forEachOrdered(article -> {

					System.out.printf("%d) %s\n", count.incrementAndGet(), article);
					KeyProvider.add(article);
				});
		;

	}


	public static Article getArticle(String solr) {

		Article art = new Article();

		String[] id = solr.split("\"id\">")[1].split("<")[0].split("_");

		art.article = id[1];
		art.variant = art.article + "_" + id[2];
		art.bundle = art.variant + "_" + id[3];
		art.gtin = solr.contains("gtin") ? solr.split("\"gtin\">")[1].split("<")[0] : null;
		art.subsysNo = solr.contains("subsysNo") ? solr.split("\"subsysNo\">")[1].split("<")[0] : null;

		return art;

	}

}
