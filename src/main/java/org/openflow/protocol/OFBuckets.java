package org.openflow.protocol;

import java.util.List;

import org.openflow.protocol.action.OFAction;

public class OFBuckets extends OFMessage{
    public static int MINIMUM_LENGTH = 16;
    
    protected int t_length;/*存储段字节长度,包括头部和任何对齐64位的填充.*/
    protected int weight;/*存储段的有关重量，为选择的组定义.*/ 
    protected long watch_port;/*状态会影响存储段是否活跃的端口。只需要快速转移故障的组。*/
    protected long watch_group;/*状态会影响存储段是否活跃的组。只需要快速转移故障的组。*/
//    uint8_tpad[4]; 
    protected List<OFAction> actions;/*与存储段关联的0或多个行为，行动列表长度 依据存储段长度计算*/

    public int getT_length() {
        return t_length;
    }
    public void setT_length(int t_length) {
        this.t_length = t_length;
    }
    public int getWeight() {
        return weight;
    }
    public void setWeight(int weight) {
        this.weight = weight;
    }
    public long getWatch_port() {
        return watch_port;
    }
    public void setWatch_port(long watch_port) {
        this.watch_port = watch_port;
    }
    public long getWatch_group() {
        return watch_group;
    }
    public void setWatch_group(long watch_group) {
        this.watch_group = watch_group;
    }
    public List<OFAction> getActions() {
        return actions;
    }
    public void setActions(List<OFAction> actions) {
        this.actions = actions;
    }

//    public OFBuckets() {
//        super();
//        this.type = OFType.ECHO_REPLY;
//        this.length = U16.t(MINIMUM_LENGTH);
//    
//    }
}
