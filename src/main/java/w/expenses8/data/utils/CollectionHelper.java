package w.expenses8.data.utils;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public class CollectionHelper {

	public static boolean isEmpty(Collection<?> c) {
		return c==null || c.isEmpty();
	}
	
	public static <T> Stream<T> stream(Collection<T> c) {
		return c==null || c.isEmpty()?Stream.empty():c.stream();
	}
	
	public static <T> T first(Collection<T> c) {
		return c==null?null:atIndex(c,0);
	}
	
	public static <T> T last(Collection<T> c) {
		return c==null?null:atIndex(c,c.size()-1);
	}
	
	public static <T> T atIndex(Collection<T> c, int index) {
		if (c==null || index>=c.size()) return null;
		if (c instanceof List) return ((List<T>) c).get(index);
		return c.stream().skip(index).findFirst().get();
	}
}
