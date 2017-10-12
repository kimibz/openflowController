package org.openflow.protocol;

import org.openflow.util.U16;

/**
 * Represents an OFPT_BARRIER_REPLY message
 */
public class OFBarrierReply extends OFMessage {
    public OFBarrierReply() {
        super();
        this.type = OFType.BARRIER_REPLY;
        this.length = U16.t(OFMessage.MINIMUM_LENGTH);
    }
}
