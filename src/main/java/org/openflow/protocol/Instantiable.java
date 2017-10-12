package org.openflow.protocol;

/**
 *
 *
 */
public interface Instantiable<E> {

    /**
     * @return new instance
     */
    public E instantiate();
}
