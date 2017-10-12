package org.openflow.protocol;

public enum OFPort {
    OFPP_MAX                ((short)0xff00),/*物理端口和逻辑交换端口的最大号码*/
    OFPP_IN_PORT            ((short)0xfff8),/*将包从输入端口发出。为了将包发回给输入端口，这个保留端口必须要明确使用。*/
    OFPP_TABLE              ((short)0xfff9),/*将包提交给第一个表。NB:这个目的端口只能在packet-out消息中使用*/
    OFPP_NORMAL             ((short)0xfffa),/*处理常规的L2/L3交换*/
    OFPP_FLOOD              ((short)0xfffb),/*VLAN中所有物理端口,除了输入端口和阻塞的或者链路断开的端口。*/
    OFPP_ALL                ((short)0xfffc),/*除了输入端口外的所有物理端口*/
    OFPP_CONTROLLER         ((short)0xfffd),/*发给控制器*/
    OFPP_LOCAL              ((short)0xfffe),/*本地openflow"port".*/
    OFPP_NONE               ((short)0xffff);/*通配端口只能在流修改（删除）、流统计请求中使用。筛选所 有流，无论是什么输出端口（包括没有输出端口的流）*/

    protected short value;

    private OFPort(short value) {
        this.value = value;
    }

    /**
     * @return the value
     */
    public short getValue() {
        return value;
    }
}
