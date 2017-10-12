package org.openflow.protocol.factory;

/**
 * Objects implementing this interface are expected to be instantiated with an
 * instance of an OFActionFactory
 */
public interface OFActionFactoryAware {
    /**
     * Sets the OFActionFactory
     * @param actionFactory
     */
    public void setActionFactory(OFActionFactory actionFactory);
}
