package org.openflow.service;

import org.openflow.protocol.OFPortStatus;

public class PortStatusServiceImp implements PortStatusService{

    @Override
    public String getReason(OFPortStatus m) {
        // TODO Auto-generated method stub
        String reply = null;
        byte reason = m .getReason();
        switch(reason){
        case (byte)0:
            reply = "端口增加";
            break;
        case (byte)1:
            reply = "端口删除";
            break;
        case (byte)2:
            reply = "端口更改";
            break;
        }
        return reply;
    }


}
