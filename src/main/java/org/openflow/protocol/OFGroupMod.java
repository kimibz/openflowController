package org.openflow.protocol;

import java.util.List;

import org.openflow.util.U16;

public class OFGroupMod extends OFMessage{
    public static int MINIMUM_LENGTH = 16;
    
    protected int command;
    protected long group_id;
    protected short group_type;
    protected List<OFBuckets> buckets;
    /*Group commands*/
    public enum ofp_group_mod_command{
        OFPGC_ADD     (0),/*新建群组.*/ 
        OFPGC_MODIFY  (1),/*修改所有匹配的组.*/
        OFPGC_DELETE  (2);/*删除所有匹配的组。*/
        
        protected int command;

        private ofp_group_mod_command(int command) {
            this.command = command; 
        };
    }

//Type字段必须是下列之一: /*组类型。值[128，255]保留给实验者使用.*/
    public enum ofp_group_type{
        OFPGT_ALL     ((short)0),/*所有的(多播/广播)组.*/
        OFPGT_SELECT  ((short)1),/*所选定组.*/
        OFPGT_INDIRECT((short)2),/*间接组*/
        OFPGT_FF      ((short)3);/*快速故障转移组.*/
        
        protected short group_type;

        private ofp_group_type(short group_type) {
            this.group_type = group_type; 
        };
     };
    private List<OFBuckets> actions;
    public int getCommand() {
        return command;
    }
    public void setCommand(int command) {
        this.command = command;
    }

    public long getGroup_id() {
        return group_id;
    }
    public void setGroup_id(long group_id) {
        this.group_id = group_id;
    }
    public List<OFBuckets> getActions() {
        return actions;
    }
    public void setActions(List<OFBuckets> actions) {
        this.actions = actions;
    }
    
    public OFGroupMod() {
        super();
//        this.type = OFType.GROUP_MOD;
        this.length = U16.t(MINIMUM_LENGTH);
    }
}
