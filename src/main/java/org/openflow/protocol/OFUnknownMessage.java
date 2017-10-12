package org.openflow.protocol;

import java.nio.ByteBuffer;

/**
 * 如果从openflow stream里面读取的消息是个未知type，直接跳过data,返回unknown
 */
public class OFUnknownMessage extends OFMessage {

    @Override
    public void readFrom(ByteBuffer data) {
        super.readFrom(data);
        // 未知数据
        if (super.length > MINIMUM_LENGTH) {
            data.position(data.position() + (super.length - MINIMUM_LENGTH));
        }
    }

    @Override
    public void writeTo(ByteBuffer data) {
        throw new RuntimeException("This message cannot be written to a stream");
    }
}
