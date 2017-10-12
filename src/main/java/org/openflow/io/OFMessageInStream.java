/**
 *
 */
package org.openflow.io;

import java.util.List;

import org.openflow.protocol.OFMessage;
import org.openflow.protocol.factory.OFMessageFactory;

/**
 * Interface for reading OFMessages from a buffered stream
 * 
 */
public interface OFMessageInStream {
    /**
     * 从Stream里面读取OF Msg
     * 
     * @return a list of OF Messages, empty代表没有完整消息，null代表stream被关闭
     */
    public List<OFMessage> read() throws java.io.IOException;

    /**
     * 从Stream里面读取OF Msg
     * 
     * @param limit 读取messages的最大个数，0代表所有MSG都被buffer了
     * @return a list of OF Messages, empty代表没有完整消息，null代表stream被关闭
     * 
     */
    public List<OFMessage> read(int limit) throws java.io.IOException;

    /**
     * 设置OFMessageFactory用来建立消息，并用在这个Stream里
     * 
     * @param factory
     */
    public void setMessageFactory(OFMessageFactory factory);

    /**
     * 返回OFMessageFactory用来建立消息，并用在这个Stream里
     * 
     * @return
     */
    public OFMessageFactory getMessageFactory();
}
