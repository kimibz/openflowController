package org.openflow.protocol.action;

import org.openflow.util.HexString;

/**
 *
 */
public class OFActionDataLayerSource extends OFActionDataLayer {
    public OFActionDataLayerSource() {
        super();
        super.setType(OFActionType.SET_DL_SRC);
        super.setLength((short) OFActionDataLayer.MINIMUM_LENGTH);
    }

    @Override
    public String toString() {
        return "OFActionDataLayerSource [dataLayerAddress="
                + HexString.toHexString(dataLayerAddress) + "]";
    }
}
