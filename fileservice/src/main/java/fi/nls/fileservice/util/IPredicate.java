package fi.nls.fileservice.util;

public interface IPredicate<T> {

    boolean apply(T type);
}
