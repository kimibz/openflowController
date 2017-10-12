package org.openflow.protocol.action;

import java.nio.ByteBuffer;

import org.openflow.util.U16;

/**
 * The base class for all OpenFlow Actions.
 *
 */
public class OFAction implements Cloneable {
    /**
     *注意，这个头的真正的最小长度是8，包括一个pad到64
     *位对齐，但是因为这个基类用于解复用
     *incoming action，只需要读取前4个字节。 所有
     *扩展这个类的动作负责读/写
     *前8个字节，包括pad如果需要。
     */
    public static int MINIMUM_LENGTH = 4;
    public static int OFFSET_LENGTH = 2;
    public static int OFFSET_TYPE = 0;

    protected OFActionType type;
    protected short length;

    /**
     * Get the length of this message
     *
     * @return
     */
    public short getLength() {
        return length;
    }

    /**
     * Get the length of this message, unsigned
     *
     * @return
     */
    public int getLengthU() {
        return U16.f(length);
    }

    /**
     * Set the length of this message
     *
     * @param length
     */
    public OFAction setLength(short length) {
        this.length = length;
        return this;
    }

    /**
     * Get the type of this message
     *
     * @return OFActionType enum
     */
    public OFActionType getType() {
        return this.type;
    }

    /**
     * Set the type of this message
     *
     * @param type
     */
    public OFAction setType(OFActionType type) {
        this.type = type;
        return this;
    }

    /**
     * Returns a summary of the message
     * @return "ofmsg=v=$version;t=$type:l=$len:xid=$xid"
     */
    public String toString() {
        return "ofaction" +
            ";t=" + this.getType() +
            ";l=" + this.getLength();
    }
    
    /**
     * Given the output from toString(), 
     * create a new OFAction
     * @param val
     * @return
     */
    public static OFAction fromString(String val) {
        String tokens[] = val.split(";");
        if (!tokens[0].equals("ofaction"))
            throw new IllegalArgumentException("expected 'ofaction' but got '" + 
                    tokens[0] + "'");
        String type_tokens[] = tokens[1].split("="); 
        String len_tokens[] = tokens[2].split("=");
        OFAction action = new OFAction();
        action.setLength(Short.valueOf(len_tokens[1]));
        action.setType(OFActionType.valueOf(type_tokens[1]));
        return action;
    }

    public void readFrom(ByteBuffer data) {
        this.type = OFActionType.valueOf(data.getShort());
        this.length = data.getShort();
        // Note missing PAD, see MINIMUM_LENGTH comment for details
    }

    public void writeTo(ByteBuffer data) {
        data.putShort(type.getTypeValue());
        data.putShort(length);
        // Note missing PAD, see MINIMUM_LENGTH comment for details
    }

    @Override
    public int hashCode() {
        final int prime = 347;
        int result = 1;
        result = prime * result + length;
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof OFAction)) {
            return false;
        }
        OFAction other = (OFAction) obj;
        if (length != other.length) {
            return false;
        }
        if (type == null) {
            if (other.type != null) {
                return false;
            }
        } else if (!type.equals(other.type)) {
            return false;
        }
        return true;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#clone()
     */
    @Override
    public OFAction clone() throws CloneNotSupportedException {
        return (OFAction) super.clone();
    }
    
}
