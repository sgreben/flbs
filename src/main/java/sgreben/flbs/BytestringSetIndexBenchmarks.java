package sgreben.flbs;

/**

# Run complete. Total time: 00:01:21

Benchmark                                             Mode  Cnt    Score   Error  Units
BytestringSetIndexBenchmarks.Union1024Words_DeepFSA  thrpt   20  109.828 ± 1.206  ops/s
BytestringSetIndexBenchmarks.Union1024Words_FlatFSA  thrpt   20  231.924 ± 0.807  ops/s


# Run complete. Total time: 00:01:21

Benchmark                                             Mode  Cnt    Score   Error  Units
BytestringSetIndexBenchmarks.Union1024Words_DeepFSA  thrpt   20  123.632 ± 0.641  ops/s
BytestringSetIndexBenchmarks.Union1024Words_FlatFSA  thrpt   20  240.107 ± 3.867  ops/s

# Run complete. Total time: 00:02:43

Benchmark                                                      Mode  Cnt    Score   Error  Units
BytestringSetIndexBenchmarks.Union1024Words_DeepFSA           thrpt   20  155.123 ± 1.783  ops/s
BytestringSetIndexBenchmarks.Union1024Words_FlatFSA           thrpt   20  248.958 ± 2.010  ops/s
BytestringSetIndexBenchmarks.UnionSingleton1024Words_DeepFSA  thrpt   20  187.807 ± 1.395  ops/s
BytestringSetIndexBenchmarks.UnionSingleton1024Words_FlatFSA  thrpt   20  285.303 ± 2.639  ops/s

*/

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

public class BytestringSetIndexBenchmarks {
	static byte[] word1 = new byte[]{0x1,0x2,0x3,0xF,0xE,0xD};
	static byte[] word2 = new byte[]{0x1,0x2,0x3,0xF,0xF,0xF};
	static byte[] word3 = new byte[]{0xF,0xF,0xF,0xF,0xF,0xF};
	static byte[][] words = new byte[1024][];
	
	public BytestringSetIndexBenchmarks() {
		for(int i = 0; i < 1024; ++i) {
			words[i] = new byte[6];
			for(int j = 0; j < 6; ++j) {
				words[i][j] = (byte)((1024-i) ^ word1[j] ^ 0x3 + word2[j] ^ 0x4 + ((byte)((i % 256) - 128)));
			}
		}
	}
	
	@State(Scope.Benchmark)
	public static class BenchmarkState {
		BytestringSetIndex flatFsa;
		BytestringSetIndex deepFsa;
		
		@Setup
		public void Setup() {
			FlatIndexedStateTable flatTable = new FlatIndexedStateTable(); 
			flatFsa = new FlatFsaBytestringSetIndex(flatTable);
			IndexedStateTable table = new IndexedStateTable();
			deepFsa = new FsaBytestringSetIndex(table);
		}
	}
		
	private void Union1024Words(BytestringSetIndex bsi) {
		int L = bsi.emptySet();
		for(int i = 0; i < 1024; ++i) {
			L = bsi.union(L, bsi.singleton(words[i]));
		}
	}
	
	private void UnionSingleton1024Words(BytestringSetIndex bsi) {
		int L = bsi.emptySet();
		for(int i = 0; i < 1024; ++i) {
			L = bsi.unionSingleton(L, words[i]);
		}
	}
	
	@Benchmark
	public void Union1024Words_FlatFSA(BenchmarkState state) {
		Union1024Words(state.flatFsa);
	}
	
	@Benchmark
	public void Union1024Words_DeepFSA(BenchmarkState state) {
		Union1024Words(state.deepFsa);
	}
	
	@Benchmark
	public void UnionSingleton1024Words_FlatFSA(BenchmarkState state) {
		UnionSingleton1024Words(state.flatFsa);
	}
	
	@Benchmark
	public void UnionSingleton1024Words_DeepFSA(BenchmarkState state) {
		UnionSingleton1024Words(state.deepFsa);
	}

}