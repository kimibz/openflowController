package org.openflow.protocol;

import org.openflow.util.U16;


/**
 * Represents an ofp_hello message
 *
 */
public class OFHello extends OFMessage {
    public static int MINIMUM_LENGTH = 8;

    public OFHello() {
        super();
        this.type = OFType.HELLO;
        this.length = U16.t(MINIMUM_LENGTH);
    }
}
