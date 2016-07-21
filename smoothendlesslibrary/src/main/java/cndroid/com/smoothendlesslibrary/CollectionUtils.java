package cndroid.com.smoothendlesslibrary;


import java.util.Collection;
import java.util.Collections;

public class CollectionUtils {
    public static boolean isEmpty(Collection collection) {
        return collection == null || collection.isEmpty();
    }

    public static <T> boolean addAll(Collection<? super T> c, T... a) {
        return a != null && Collections.addAll(c, a);
    }

    public static <T> boolean addAll(Collection<? super T> c, Collection<T> a) {
        return a != null && c.addAll(a);
    }

    public static <T> int getSize(Collection c) {
        return c == null ? 0 : c.size();
    }
}
