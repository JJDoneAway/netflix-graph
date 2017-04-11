package de.hoehne.netflix_graph.classical;

import java.io.File;
import java.io.FileInputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import de.hoehne.netflix_graph.Article;
import de.hoehne.netflix_graph.preperation.PreparationClassical;

public class TestKeyProvider {

	@Test
	public void getAllKeyValues() throws Exception {
		String solr = IOUtils.toString(new FileInputStream(new File("./src/test/resources/solr.xml")));
		System.out.println(solr);

		Article art = PreparationClassical.getArticle(solr);
		System.out.println(art);

		Assert.assertEquals("571255", art.article);
		Assert.assertEquals("571255_1", art.variant);
		Assert.assertEquals("571255_1_2", art.bundle);
		Assert.assertEquals("4002627811898", art.gtin);
		Assert.assertEquals("308683", art.subsysNo);

	}

	@Test
	public void testClassicalVersion() throws Exception {
		String solr = IOUtils.toString(new FileInputStream(new File("./src/test/resources/solr.xml")));
		System.out.println(solr);

		Article art = PreparationClassical.getArticle(solr);
		KeyProviderClassic.me.add(art);

		final String artNo = KeyProviderClassic.getArticleForGtin("4002627811898");
		System.out.printf("The Article Number of GTIN %s is %s\n", "4002627811898", artNo);
		Assert.assertEquals("571255", artNo);

	}

	@Test
	public void testClassicalVersionPersistece() throws Exception {
		try {
			String solr = IOUtils.toString(new FileInputStream(new File("./src/test/resources/solr.xml")));

			Article art = PreparationClassical.getArticle(solr);
			KeyProviderClassic.me.add(art);

			KeyProviderClassic.persistDB(new File("./src/test/resources/"));
			

			KeyProviderClassic.me.gtinToBundle.clear();
			KeyProviderClassic.me.bundleToGtin.clear();

			KeyProviderClassic.me.bundleToSubsys.clear();
			KeyProviderClassic.me.susysToBundle.clear();

			KeyProviderClassic.me.bundleToVariant.clear();
			KeyProviderClassic.me.variantToBundle.clear();

			KeyProviderClassic.me.variantToArticle.clear();
			KeyProviderClassic.me.articleToVarinant.clear();

			String artNo = KeyProviderClassic.getArticleForGtin("4002627811898");
			System.out.printf("The Article Number of GTIN %s is %s\n", "4002627811898", artNo);
			Assert.assertNull( artNo);
			
			
			KeyProviderClassic.restoreDB(new File("./src/test/resources/"));

			artNo = KeyProviderClassic.getArticleForGtin("4002627811898");
			System.out.printf("The Article Number of GTIN %s is %s\n", "4002627811898", artNo);
			Assert.assertEquals("571255", artNo);

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

}
