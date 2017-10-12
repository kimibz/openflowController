package org.openflow.service;

import org.openflow.protocol.OFPortStatus;

public interface PortStatusService {
    /**
     * 回复端口变化的原因
     * @param m
     * @return
     */
    String getReason(OFPortStatus m);
}
