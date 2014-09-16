package fi.nls.fileservice.util;

import java.util.Iterator;

import javax.jcr.query.RowIterator;

/**
 * Abstract iterator that wraps javax.jcr.query.RowIterator. This is used as a
 * support class for <code>LazyQueryResultList</code>s.
 * 
 * @param <E>
 */
public abstract class AbstractRowIteratorWrapper<E> implements Iterator<E> {

    protected RowIterator rowIterator;

    /**
     * Constructs a new AbstractRowIteratorWrapper
     * 
     * @param rowIterator
     *            javax.jcr.query.RowIterator that is wrapped
     */
    public AbstractRowIteratorWrapper(RowIterator rowIterator) {
        this.rowIterator = rowIterator;
    }

    @Override
    public boolean hasNext() {
        return rowIterator.hasNext();
    }

    @Override
    public void remove() {
        rowIterator.remove();
    }

}
