package org.openflow.util;

import java.util.ArrayList;
import java.util.List;

import org.openflow.protocol.OFMatch;
import org.openflow.service.Flow_ModServiceImp;

public class TS {

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        Flow_ModServiceImp service = new Flow_ModServiceImp();
        List<OFMatch> list = new ArrayList<OFMatch>();
        list = service.getMatchList();
        for(int i=0 ; i<list.size() ; i++){
            System.out.println(list.get(i).toString());
        }
    }

}
