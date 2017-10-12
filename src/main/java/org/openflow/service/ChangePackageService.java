package org.openflow.service;

import java.nio.ByteBuffer;

import org.openflow.protocol.OFMatch;

public interface ChangePackageService {
    /**
     * 改变匹配域内的VLAN
     * @param match
     * @return
     */
    OFMatch changeVlan(OFMatch match);
    /**
     * 将匹配域写进ByteBuffer
     * @param match
     * @return
     */
    ByteBuffer writeToBuf(OFMatch match);
    /**
     * 
     * @param match
     * @param packet_in
     * @return
     */
    byte[] write(OFMatch match , byte[] packet_in);
}
