package fi.nls.fileservice.util;

import java.util.Collection;
import java.util.Iterator;

public class CollectionUtils<T> {

    /*
     * public static <T> Collection<T> filter(Collection<T> target,
     * IPredicate<T> predicate) { Collection<T> result = new ArrayList<T>(); for
     * (T element : target) { if (predicate.apply(element)) {
     * result.add(element); } } return result; }
     */

    public static <T> void filter(Collection<T> target, IPredicate<T> predicate) {
        Iterator<T> iter = target.iterator();
        while (iter.hasNext()) {
            T element = iter.next();
            if (!predicate.apply(element)) {
                iter.remove();
            }
        }
    }

}
