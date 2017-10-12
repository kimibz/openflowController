package org.openflow.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openflow.protocol.OFFeaturesReply;
import org.openflow.protocol.OFPhysicalPort;
import org.openflow.util.macToString;

public class Feature_ReplyServiceImp implements Feature_ReplyService{

    @Override
    public Map<String, String> getPortName(OFFeaturesReply m) {
        // TODO Auto-generated method stub
        Map<String,String> map = new HashMap<String,String>();
        List<OFPhysicalPort> ports = m.getPorts();
        for(int i=0; i<ports.size() ; i++){
            map.put(ports.get(i).getName(), 
                    macToString.toString(ports.get(i).getHardwareAddress()));
        }
        
        return map;
    }

}
