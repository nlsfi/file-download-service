package fi.nls.fileservice.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class LazyQueryResultList<E> implements List<E> {

    private static final String NOT_IMPLEMENTED = "Not implemented. Use iterator().";
    private Iterator<E> iterator;

    public LazyQueryResultList(Iterator<E> iterator) {
        this.iterator = iterator;
    }

    @Override
    public boolean add(E e) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public void add(int index, E element) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public boolean contains(Object o) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public E get(int index) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public int indexOf(Object o) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public Iterator<E> iterator() {
        return iterator;
    }

    @Override
    public int lastIndexOf(Object o) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public ListIterator<E> listIterator() {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public E remove(int index) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public E set(int index, E element) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public int size() {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public Object[] toArray() {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public <T> T[] toArray(T[] a) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }
}
