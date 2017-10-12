package org.openflow.protocol;

import org.openflow.util.U16;

/**
 * Represents an OFPT_BARRIER_REQUEST message
 */
public class OFBarrierRequest extends OFMessage {
    public OFBarrierRequest() {
        super();
        this.type = OFType.BARRIER_REQUEST;
        this.length = U16.t(OFMessage.MINIMUM_LENGTH);
    }
}
