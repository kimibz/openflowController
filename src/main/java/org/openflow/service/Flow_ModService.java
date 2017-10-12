package org.openflow.service;

import java.util.List;

import org.openflow.protocol.OFFlowRemoved;
import org.openflow.protocol.OFMatch;

public interface Flow_ModService {
    /**
     * 读取flow_removed的文字信息
     * @param remove
     * @return
     */
    String getFlowRemovedReason(OFFlowRemoved remove);
    /**
     * 读取txt文件的匹配域信息
     * @return
     */
    List<OFMatch> getMatchList();
}
