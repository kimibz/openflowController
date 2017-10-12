/**
 *
 */
package org.openflow.io;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.List;

import org.openflow.protocol.OFMessage;
import org.openflow.protocol.factory.OFMessageFactory;

/**
 * 在NIO SocketChannel异步的openflow MSG 编组和解码
 */
public class OFMessageAsyncStream implements OFMessageInStream,
        OFMessageOutStream {
    static public int defaultBufferSize = 65536;

    protected ByteBuffer inBuf, outBuf;
    protected OFMessageFactory messageFactory;
    protected SocketChannel sock;
    protected int partialReadCount = 0;

    public OFMessageAsyncStream(SocketChannel sock,
            OFMessageFactory messageFactory) throws IOException {
        inBuf = ByteBuffer
                .allocateDirect(OFMessageAsyncStream.defaultBufferSize);
        outBuf = ByteBuffer
                .allocateDirect(OFMessageAsyncStream.defaultBufferSize);
        this.sock = sock;
        this.messageFactory = messageFactory;
        this.sock.configureBlocking(false);
    }

    @Override
    public List<OFMessage> read() throws IOException {
        return this.read(0);
    }

    @Override
    public List<OFMessage> read(int limit) throws IOException {
        List<OFMessage> l;
        int read = sock.read(inBuf);
        if (read == -1)
            return null;
        inBuf.flip();
        l = messageFactory.parseMessages(inBuf, limit);
        if (inBuf.hasRemaining())
            inBuf.compact();
        else
            inBuf.clear();
        return l;
    }

    protected void appendMessageToOutBuf(OFMessage m) throws IOException {
        int msglen = m.getLengthU();
        if (outBuf.remaining() < msglen) {
            throw new IOException(
                    "Message length exceeds buffer capacity: " + msglen);
        }
        m.writeTo(outBuf);
    }

    /**
     * Buffer一个输出的openflow message
     */
    @Override
    public void write(OFMessage m) throws IOException {
        appendMessageToOutBuf(m);
    }

    /**
     * Buffer一个OpenFlow messages的list
     */
    @Override
    public void write(List<OFMessage> l) throws IOException {
        for (OFMessage m : l) {
            appendMessageToOutBuf(m);
        }
    }

    /**输出已经buffered的outgoing数据。一直输出直到needsFlush() returns false。每个flush()
     * 对应一个SocketChannel.write()，一个flush()对应一个select() event
     */
    public void flush() throws IOException {
        outBuf.flip(); // swap pointers; lim = pos; pos = 0;
        sock.write(outBuf); // write data starting at pos up to lim
        outBuf.compact();
    }

    /**
     * 判断outgoing的数据里还有需要flush()的？
     */
    public boolean needsFlush() {
        return outBuf.position() > 0;
    }

    /**
     * @return the messageFactory
     */
    public OFMessageFactory getMessageFactory() {
        return messageFactory;
    }

    /**
     * @param messageFactory
     *            the messageFactory to set
     */
    public void setMessageFactory(OFMessageFactory messageFactory) {
        this.messageFactory = messageFactory;
    }
}
