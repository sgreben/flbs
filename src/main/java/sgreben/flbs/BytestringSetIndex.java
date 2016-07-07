package sgreben.flbs;

import java.util.Iterator;
import java.math.BigInteger;

interface BytestringSetIndex {
	
	/** Return the empty set
	*/
	int emptySet();
	
	/** Return the set containing the empty bytestring.
	*/
	int singletonEmpty();
	
	/** Find or build a singleton set containing the given bytestring
	 */
	int singleton(byte[] data);

	/** Returns the union of a set with a single byte string. 
		Returns the same state as union(L, singleton(data)), but avoids constructing
		the intermediate set singleton(data).
	 */
	int unionSingleton(int L, byte[] data);
	
	/** Find or build the intersection of the two given sets of same-length bytestrings.
	 */
	int intersection(int L, int R);
	
	/** Find or build the union of the two given sets  of same-length bytestrings.
	 */
	int union(int L, int R);
	
	/** Find or build the fixed-length complement of the given set.
	 */
	int complement(int L);
	
	/** True iff the set L contains the given bytestring
	 */
	boolean contains(int L, byte[] data);
	/** Computes the length of the bytestrings in the given set. 
	 */
	int length(int L);
	
	/** Returns a bytestring iterator for the given set. 
	 *  The byte[] returned by next() is overwritten by the next call to next().
	 */
	Iterator<byte[]> iterate(int L);
	
	/** Computes the size of the given set.
	 */
	BigInteger size(int L);
	
	/** Find or build the set of suffixes of L starting with the given byte[].
	 */
	int suffix(int L, byte[] data);
	
	/** Find or build the set of bytestrings from L prefixed by the given byte[].
	 */
	int prefix(int L, byte[] data);
	
	/** Find or build the set of bytestrings from L prefixed by [length] copies of the given [symbol].
	 */
	int prefixPadding(int L, int length, byte symbol);
	
	/** Find or build the set of bytestrings from L prefixed by any byte[] of the given length.
	 */
	int prefixPadding(int L, int length);
}
