package org.openflow.protocol.action;

import org.openflow.util.U16;

/**
 *
 * */
public class OFActionTransportLayerSource extends OFActionTransportLayer {
    public OFActionTransportLayerSource() {
        super();
        super.setType(OFActionType.SET_TP_SRC);
        super.setLength((short) OFActionTransportLayer.MINIMUM_LENGTH);
    }

    @Override
    public String toString() {
        return "OFActionTransportLayerSource [transportPort="
                + U16.f(transportPort) + "]";
    }
}
