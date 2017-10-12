package org.openflow.service;

public interface ErrorService {
    /**
     * 读取error的文字信息
     * @param ErrorType
     * @return
     */
    String getErrorTypeMsg(short ErrorType);
}
