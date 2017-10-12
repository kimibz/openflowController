package org.openflow.service;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openflow.protocol.OFMatch;
import org.openflow.util.ReadTxT;
import org.openflow.util.U16;

public class ChangePackageServiceImp implements ChangePackageService {

    @Override
    public OFMatch changeVlan(OFMatch match) {
        // TODO Auto-generated method stub
        List<String> list = new ArrayList<String>();
        String filePath ="c://example//localconfig.txt";
//      String word ="abs"+"\r\n";
//      writeF1(word,filePath);
        try {
            list=ReadTxT.readF1(filePath);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
//        Iterator<String> it1 = list.iterator();
        //遍历list
//        while(it1.hasNext()){
//            System.out.println(it1.next());
//        }
        Map<String,String> map = new HashMap<String,String>();
        for(int i=0 ; i< list.size() ; i++){
            ReadTxT.splitToMap(list.get(i),map);
        }
        OFMatch res = new OFMatch();
        res.setDataLayerDestination(match.getDataLayerDestination());
        res.setDataLayerSource(match.getDataLayerSource());
        res.setDataLayerType(match.getDataLayerType());
        res.setDataLayerVirtualLanPriorityCodePoint(match.getDataLayerVirtualLanPriorityCodePoint());
        res.setInputPort(match.getInputPort());
        res.setNetworkSource(match.getNetworkSource());
        res.setNetworkDestination(match.getNetworkDestination());
        res.setNetworkProtocol(match.getNetworkProtocol());
        res.setTransportDestination(match.getTransportDestination());
        res.setTransportSource(match.getTransportSource());
        res.setNetworkTypeOfService(match.getNetworkTypeOfService());
        String vlan_s = Integer.toHexString(U16.f(match.getDataLayerVirtualLan()));//16进制数     
        res.setWildcards(match.getWildcards());
        String find_vlan = find(vlan_s,map);
        res.setDataLayerVirtualLan(U16.t(Integer.valueOf(find_vlan)));
        
        return res;
    }
    protected String find(String findNO,Map<String,String> map){
        String findValue = null;
        String text = "未找到对应数据";
        for (String key : map.keySet()) {  
            if(key.equals(findNO)){
                findValue = map.get(key);
            }
            else if(map.get(key).equals(findNO)){
                findValue = key;
            }
        }
        if(findValue==null){
            findValue=text;
        }
        return findValue;
    }
    @Override
    public ByteBuffer writeToBuf(OFMatch match) {
        ByteBuffer data = ByteBuffer.allocate(256);
//        data.putInt(match.getWildcards());
//        data.putShort(match.getInputPort());
        data.put(match.getDataLayerDestination());
        data.put(match.getDataLayerSource());
        data.putShort(match.getDataLayerType());
        data.putShort(match.getDataLayerVirtualLan());
        data.put(match.getDataLayerVirtualLanPriorityCodePoint());
        data.put(match.getNetworkTypeOfService());
        data.putInt(match.getNetworkProtocol());
        data.putInt(match.getNetworkSource());
        data.putInt(match.getNetworkDestination());
        data.putShort(match.getTransportSource());
        data.putShort(match.getTransportDestination());
        return data;
//        data_byte = new byte[data.remaining()];
//        data.get(data_byte, 0, data_byte.length);
//        return data_byte;
    }
    @Override
    public byte[] write(OFMatch match, byte[] packet_in) {
        // TODO Auto-generated method stub
        byte[] packet_out = new byte[packet_in.length];
        byte vlan1 = packet_in[14];
        byte vlan2 = packet_in[15];
        String h1 = Integer.toHexString(packet_in[14]&0xff);
        String h2 = Integer.toHexString(packet_in[15]&0xff);
        String vlan = h1 + h2;
        return null;
    }

}
