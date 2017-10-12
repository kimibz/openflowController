/**
 * 
 */
package org.openflow.protocol.factory;

/**
 *
 */
public interface OFMessageFactoryAware {

       /**
        * Sets the message factory for this object
        * 
        * @param factory
        */
       void setMessageFactory(OFMessageFactory factory);
}
