package org.openflow.protocol.action;

import org.openflow.protocol.OFMatch;

/**
 *
 */
public class OFActionNetworkLayerDestination extends OFActionNetworkLayerAddress {
    public OFActionNetworkLayerDestination() {
        super();
        super.setType(OFActionType.SET_NW_DST);
        super.setLength((short) OFActionNetworkLayerAddress.MINIMUM_LENGTH);
    }

    @Override
    public String toString() {
        return "OFActionNetworkLayerDestination [networkAddress="
                + OFMatch.ipToString(networkAddress) + "]";
    }
}
