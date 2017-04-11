package de.hoehne.netflix_graph;

import static com.netflix.nfgraph.spec.NFPropertySpec.COMPACT;
import static com.netflix.nfgraph.spec.NFPropertySpec.HASH;
import static com.netflix.nfgraph.spec.NFPropertySpec.MULTIPLE;
import static com.netflix.nfgraph.spec.NFPropertySpec.SINGLE;

import com.netflix.nfgraph.OrdinalIterator;
import com.netflix.nfgraph.build.NFBuildGraph;
import com.netflix.nfgraph.compressed.NFCompressedGraph;
import com.netflix.nfgraph.spec.NFGraphSpec;
import com.netflix.nfgraph.spec.NFNodeSpec;
import com.netflix.nfgraph.spec.NFPropertySpec;
import com.netflix.nfgraph.util.OrdinalMap;

public class FirstTest {
	public static void main(String[] args) {

		NFGraphSpec movieSchema = new NFGraphSpec(
				new NFNodeSpec("Actor", new NFPropertySpec("starredIn", "Movie", MULTIPLE | COMPACT),
						new NFPropertySpec("costarredWith", "Actor", MULTIPLE | HASH),
						new NFPropertySpec("premieredIn", "Movie", SINGLE)),
				new NFNodeSpec("Movie", new NFPropertySpec("rated", "Rating", SINGLE),
						new NFPropertySpec("starring", "Actor", MULTIPLE | HASH)),
				new NFNodeSpec("Rating"));

		NFBuildGraph buildGraph = new NFBuildGraph(movieSchema);

		OrdinalMap<String> movieOrdinals = new OrdinalMap<String>();
		OrdinalMap<String> actorOrdinals = new OrdinalMap<String>();
		OrdinalMap<String> ratingOrdinals = new OrdinalMap<String>();

		int matrixOrdinal = movieOrdinals.add("The Matrix");
		int keanuOrdinal = actorOrdinals.add("Keanu Reeves");
		int laurenceOrdinal = actorOrdinals.add("Laurence Fishburn");
		int ratedROrdinal = ratingOrdinals.add("R");

		buildGraph.addConnection("Actor", keanuOrdinal, "starredIn", matrixOrdinal);
		buildGraph.addConnection("Actor", laurenceOrdinal, "starredIn", matrixOrdinal);
		
		buildGraph.addConnection("Movie", matrixOrdinal, "starring", keanuOrdinal);
		buildGraph.addConnection("Movie", matrixOrdinal, "starring", laurenceOrdinal);

		buildGraph.addConnection("Movie", matrixOrdinal, "rated", ratedROrdinal);
		
		NFCompressedGraph compressedGraph = buildGraph.compress();
		final OrdinalIterator iter = compressedGraph.getConnectionIterator("Movie", matrixOrdinal, "starring");
		
		int currentOrdinal = iter.nextOrdinal();

		while(currentOrdinal != OrdinalIterator.NO_MORE_ORDINALS) {
			System.out.println(actorOrdinals.get(currentOrdinal) + " starred in The Matrix!");
			currentOrdinal = iter.nextOrdinal();
		}	
		
	}

}
