/**
 *
 */
package org.openflow.io;

import java.util.List;
import org.openflow.protocol.OFMessage;

/**
 * Interface for writing OFMessages to a buffered stream
 *
 */
public interface OFMessageOutStream {
    /**
     * 讲一个OF Msg写入Stream
     * @param m An OF Message
     */
    public void write(OFMessage m) throws java.io.IOException;

    /**
     * 讲一个OF Msg写入Stream
     * @param l A list of OF Messages
     */
    public void write(List<OFMessage> l) throws java.io.IOException;

    /**将buffer好的数据传到stream里，需要多次flush（），直到needFlush()返回false值
     */
    public void flush() throws java.io.IOException;

    /**
     * 是否有数据需要flush()?
     * @return 
     */
    public boolean needsFlush();
}
