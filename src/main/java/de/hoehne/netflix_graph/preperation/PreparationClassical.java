package de.hoehne.netflix_graph.preperation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;

import de.hoehne.netflix_graph.Article;
import de.hoehne.netflix_graph.classical.KeyProviderClassic;

public class PreparationClassical {

	public static void main(String[] args) throws Exception {
		long start = System.currentTimeMillis();
		// Preparation.download();
		loadKeyProviderClassic();
		System.out.printf("It took %d to initiate %s\n", (System.currentTimeMillis() - start),
				KeyProviderClassic.me.toString());

		start = System.currentTimeMillis();
		KeyProviderClassic.persistDB(new File("./src/main/resources"));
		System.out.printf("It took %d to persist it\n", (System.currentTimeMillis() - start));

	}

	public static void readTar(List<String> myQueue) throws Exception {
		TarArchiveInputStream tarInput = new TarArchiveInputStream(
				new GzipCompressorInputStream(new FileInputStream("./src/main/resources/solr.tar.gz")));
		TarArchiveEntry currentEntry = tarInput.getNextTarEntry();
		BufferedReader br = null;
		while (currentEntry != null) {
			br = new BufferedReader(new InputStreamReader(tarInput));
			String solr = IOUtils.toString(br);
			myQueue.add(solr);
			currentEntry = tarInput.getNextTarEntry();
		}
		tarInput.close();
	}

	public static void loadKeyProviderClassic() throws Exception {
		List<String> solrs = new ArrayList<String>();
		readTar(solrs);
		solrs.stream().parallel()//
				.map(solr -> getArticle(solr))//
				.forEachOrdered(article -> {

					// System.out.printf("%d) %s\n", count.incrementAndGet(),
					// article);
					KeyProviderClassic.me.add(article);
				});
		;

	}

	public static void download() throws Exception {

		Files.lines(Paths.get("./src/main/resources/solr_files.txt"))//
				.parallel()//
				.map(line -> {
					try {

						try {
							return getResource().path(line).request(MediaType.APPLICATION_XML_TYPE).get(String.class);

						} catch (Exception e) {
							return null;
						}
					} catch (Exception e) {
						e.printStackTrace();
						throw new RuntimeException(e);
					}
				}).forEach(line -> {
					if (line != null) {
						final String fileName = System.currentTimeMillis() + "_" + new Random().nextInt() + ".xml";
						try {
							final File file = new File(new File("/home/johannes/tmp/"), fileName);
							FileUtils.writeStringToFile(file, line);
							System.out.println(file);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				});
		;
	}

	private static WebTarget getResource() {

		HttpAuthenticationFeature feature = HttpAuthenticationFeature.basicBuilder().nonPreemptive()
				.credentials("**", "**").build();

		ClientConfig clientConfig = new ClientConfig();
		clientConfig.register(feature);

		Client client = ClientBuilder.newClient(clientConfig);

		return client.target("http://iappl4.mgi.de:11100/artcache/all/mcc/v1/articlecache/");
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
