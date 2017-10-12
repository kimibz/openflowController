package org.openflow.service;

import java.util.Map;

import org.openflow.protocol.OFFeaturesReply;

public interface Feature_ReplyService {
    /**
     * 将端口信息存入MAP
     * @param m
     * @return
     */
    Map<String,String> getPortName (OFFeaturesReply m);
}
