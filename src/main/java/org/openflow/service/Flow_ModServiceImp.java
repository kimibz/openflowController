package org.openflow.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.openflow.protocol.OFFlowRemoved;
import org.openflow.protocol.OFFlowRemoved.OFFlowRemovedReason;
import org.openflow.protocol.OFMatch;
import org.openflow.util.ReadTxT;

public class Flow_ModServiceImp implements Flow_ModService{
    OFMatch match = new OFMatch();

    @Override
    public String getFlowRemovedReason(OFFlowRemoved remove) {
        // TODO Auto-generated method stub
        OFFlowRemovedReason reason = remove.getReason();
        String str = null;
        switch(reason){
            case OFPRR_DELETE: str ="流表删除";break;
            case OFPRR_HARD_TIMEOUT: str ="绝对时间导致的超时";break;
            case OFPRR_IDLE_TIMEOUT: str = "规定时间内没有收到匹配数据包"; break ;
        }
        return str;
    }

    @Override
    public List<OFMatch> getMatchList(){
        // TODO Auto-generated method stub\
        String path = "c://example//match.txt";
        List<OFMatch> OFMatchList = new ArrayList<OFMatch>();
        List<String> match_string = new ArrayList<String>();
        try {
            match_string = ReadTxT.readF1(path);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        for(int i=0 ; i<match_string.size() ; i++){
            OFMatch match = new OFMatch();
            match.fromString(match_string.get(i));
            if(!match.toString().equals(""))
            OFMatchList.add(match);
        }
        return OFMatchList;
    }
}
