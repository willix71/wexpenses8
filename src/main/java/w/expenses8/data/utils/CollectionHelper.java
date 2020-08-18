package w.expenses8.data.utils;

import java.util.Collection;
import java.util.stream.Stream;

public class CollectionHelper {

	public static boolean isEmpty(Collection<?> c) {
		return c==null || c.isEmpty();
	}
	
	public static <T> Stream<T> stream(Collection<T> c) {
		return c==null || c.isEmpty()?Stream.empty():c.stream();
	}
}
