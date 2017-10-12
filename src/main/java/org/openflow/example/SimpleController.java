/**
 *
 */
package org.openflow.example;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.openflow.example.cli.Options;
import org.openflow.example.cli.ParseException;
import org.openflow.example.cli.SimpleCLI;
import org.openflow.io.OFMessageAsyncStream;
import org.openflow.protocol.OFEchoReply;
import org.openflow.protocol.OFError;
import org.openflow.protocol.OFFeaturesReply;
import org.openflow.protocol.OFFlowMod;
import org.openflow.protocol.OFFlowRemoved;
import org.openflow.protocol.OFMatch;
import org.openflow.protocol.OFMatch2;
import org.openflow.protocol.OFMessage;
import org.openflow.protocol.OFPacketIn;
import org.openflow.protocol.OFPacketOut;
import org.openflow.protocol.OFPhysicalPort;
import org.openflow.protocol.OFPort;
import org.openflow.protocol.OFPortStatus;
import org.openflow.protocol.OFType;
import org.openflow.protocol.action.OFAction;
import org.openflow.protocol.action.OFActionOutput;
import org.openflow.protocol.action.OFActionVirtualLanIdentifier;
import org.openflow.protocol.factory.BasicFactory;
import org.openflow.service.ErrorServiceImp;
import org.openflow.service.Feature_ReplyServiceImp;
import org.openflow.service.Flow_ModServiceImp;
import org.openflow.service.PortStatusServiceImp;
import org.openflow.util.LRULinkedHashMap;
import org.openflow.util.U16;
import org.openflow.util.U32;
import org.openflow.util.macToString;

/**
 * @author xigua
 *
 */
public class SimpleController implements SelectListener {
    protected ExecutorService es;//类似一个线程池，该线程就会继续执行与运行任务无关的其它任务
    protected BasicFactory factory;//创建OF MSg的基本工厂
    protected SelectLoop listenSelectLoop;//socket的循环
    protected ServerSocketChannel listenSock;//交换机的NIO通道 
    protected List<SelectLoop> switchSelectLoops;//交换器循环列表
    protected Map<SocketChannel,OFSwitch> switchSockets;//存放和控制器连接的swtich的sockets端口号
    protected Integer threadCount;//线程个数
    protected int port;//绑定的端口号

    protected class OFSwitch {
        protected SocketChannel sock;
        protected OFMessageAsyncStream stream;
        protected Map<Integer, Short> macTable =new LRULinkedHashMap<Integer, Short>(64001, 64000);

        public OFSwitch(SocketChannel sock, OFMessageAsyncStream stream) {
            this.sock = sock;
            this.stream = stream;
        }
        public void pushFlowMod(OFMatch match){
            OFFlowMod flow_mod = (OFFlowMod) factory.getMessage(OFType.FLOW_MOD);
            flow_mod.setBufferId(1233);
            flow_mod.setCommand((short) 0);
            flow_mod.setCookie(0);
            flow_mod.setFlags((short) 1);
            flow_mod.setHardTimeout((short) 0);
            flow_mod.setIdleTimeout((short) 10);
            match.setWildcards(0);
            flow_mod.setMatch(match);
            flow_mod.setOutPort((short) 2);
            flow_mod.setPriority((short) 1254);
            OFActionOutput action = new OFActionOutput();
//            OFActionStripVirtualLan vlan_action = new OFActionStripVirtualLan();
            OFActionVirtualLanIdentifier vlan_action = new OFActionVirtualLanIdentifier();
            vlan_action.setVirtualLanIdentifier((short) 100);
            action.setMaxLength((short) 0);
            action.setPort((short)2);
            List<OFAction> actions = new ArrayList<OFAction>();
            actions.add(action);
            actions.add(vlan_action);
            flow_mod.setActions(actions);
            flow_mod.setLength(U16.t(OFFlowMod.MINIMUM_LENGTH+OFActionOutput.MINIMUM_LENGTH+OFActionVirtualLanIdentifier.MINIMUM_LENGTH));
            try {
                stream.write(flow_mod);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        public void handlePacketIn(OFPacketIn pi) {
            // Build the Match
            OFMatch match = new OFMatch();
            OFMatch2 match2 = new OFMatch2();
//            ChangePackageServiceImp change_service =new ChangePackageServiceImp();
            match.loadFromPacket(pi.getPacketData(), pi.getInPort());
            byte[] dlDst = match.getDataLayerDestination();
            String dlDst_String = macToString.toString(dlDst);//目标mac地址
            Integer dlDstKey = Arrays.hashCode(dlDst);
            byte[] dlSrc = match.getDataLayerSource();
            Integer dlSrcKey = Arrays.hashCode(dlSrc);
            String dlSrc_String = macToString.toString(dlSrc);//源mac网地址
            String vlanId = "=0x"+ Integer.toHexString(U16.f(match.getDataLayerVirtualLan()));//vlanID
            String vlan_priority = Integer.toHexString(match.
                    getDataLayerVirtualLanPriorityCodePoint() & 0xFF);//vlan优先级
            String Src_Ip =  OFMatch.cidrToString(match.getNetworkSource(), match.getNetworkSourceMaskLen());
            String Dst_Ip =  OFMatch.cidrToString(match.getNetworkDestination(), match.getNetworkDestinationMaskLen());
            String packet_type = "0x"+ Integer.toHexString(U16.f(match.getDataLayerType()));
            //一层vlan
            if(pi.getPacketData().length == 54){
            System.err.println("发送的是一层Vlan。"+"vlanId："+vlanId+",vlan优先级："+vlan_priority);
            System.err.println("输入端口："+U16.f(match.getInputPort())+",发送包类型："+packet_type+
                    ",发送源mac地址:"+dlSrc_String+",目标mac地址:"+dlDst_String+",发送源Ip地址:"
                    +Src_Ip+",目标IP地址:"+Dst_Ip+",原因："+pi.getReason());
//            System.out.println(match.toString());
            }
            //二层Vlan
            else if (pi.getPacketData().length == 58){
                match2.loadFromPacket(pi.getPacketData(), pi.getInPort());
                String Src_Ip2 =  OFMatch2.cidrToString(match2.getNetworkSource(), match2.getNetworkSourceMaskLen());
                String Dst_Ip2 =  OFMatch2.cidrToString(match2.getNetworkDestination(), match2.getNetworkDestinationMaskLen());
                String packet_type2 = "=0x"+ Integer.toHexString(U16.f(match2.getDataLayerType2()));
                String packet_type1 = "=0x"+ Integer.toHexString(U16.f(match2.getDataLayerType()));
                String vlan2_ID = "=0x"+ Integer.toHexString(U16.f(match2.getDataLayerVirtualLan2()));
                String vlan2_priority = Integer.toHexString(match2.
                        getDataLayerVirtualLanPriorityCodePoint2() & 0xFF);//vlan优先级
                System.out.println("发送的是二层Vlan"+"。一层vlanId:"+vlanId+",一层vlan优先级："+vlan_priority
                        +"。二层vlanId:"+vlan2_ID+",二层vlan优先级："+vlan2_priority);
                System.err.println("输入端口："+U16.f(match.getInputPort())+",发送包类型,一层："+packet_type1+",二层："+packet_type2+
                        ",发送源mac地址:"+dlSrc_String+",目标mac地址:"+dlDst_String+",发送源Ip地址:"
                        +Src_Ip2+",目标IP地址:"+Dst_Ip2+",原因："+pi.getReason());
//                System.out.println(match2.toString());
            }
            //不处理vlan输出
            else{
            System.out.println("packet_in消息：输入端口："+U16.f(match.getInputPort())+"，发送包类型："+packet_type+
                    "，发送源mac地址:"+dlSrc_String+",目标mac地址:"+dlDst_String+",发送源Ip地址:"
                    +Src_Ip+",目标IP地址:"+Dst_Ip+",vlanId："+vlanId+",vlan优先级："+vlan_priority+"原因："+pi.getReason());
//            System.out.println(match.toString());
            }
//            System.out.println(match.toString());
            //先将pi.getpacketData存入bytebuffer 之后get之后再改
//            ByteBuffer new_packetdata = ByteBuffer.allocate(pi.getPacketData().length);
//            change_service.changeVlan(match).writeTo(new_packetdata);
//            byte[] packetdata_byte = new byte[new_packetdata.remaining()];
//            new_packetdata.get(packetdata_byte, 0, packetdata_byte.length);//改好VLAN的packetdata
            int bufferId = pi.getBufferId();

            // 如果src不是组播，则存入table
            if ((dlSrc[0] & 0x1) == 0) {
                if (!macTable.containsKey(dlSrcKey) ||
                        !macTable.get(dlSrcKey).equals(pi.getInPort())) {
                    macTable.put(dlSrcKey, pi.getInPort());
                }
            }

            Short outPort = null;
            // 如果dst不是组播，则去除
            if ((dlDst[0] & 0x1) == 0) {
                outPort = macTable.get(dlDstKey);
            }

            // 推送flow mod如果我们知道这个packet去往何处
            if (outPort != null) {
                OFFlowMod fm = (OFFlowMod) factory.getMessage(OFType.FLOW_MOD);
                fm.setBufferId(bufferId);
                fm.setCommand((short) 0);
                fm.setCookie(0);
                fm.setFlags((short) 0);
                fm.setHardTimeout((short) 0);
                fm.setIdleTimeout((short) 0);
                match.setInputPort(pi.getInPort());
                match.setWildcards(0);
                fm.setMatch(match);
                fm.setOutPort((short) OFPort.OFPP_NONE.getValue());
                fm.setPriority((short) 0);
                OFActionOutput action = new OFActionOutput();
//                OFActionVirtualLanIdentifier vlan_action = new OFActionVirtualLanIdentifier();
                action.setMaxLength((short) 0);
                action.setPort(outPort);
//                vlan_action.setVirtualLanIdentifier((short) 100);
                List<OFAction> actions = new ArrayList<OFAction>();
//                actions.add(vlan_action);
                actions.add(action);
                fm.setActions(actions);
                fm.setLength(U16.t(OFFlowMod.MINIMUM_LENGTH+OFActionOutput.MINIMUM_LENGTH));
                try {
                    stream.write(fm);
                    System.out.println("向交换机发送flow_mod消息,时效为25秒,匹配域为"+match.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // Send a packet out
            if (outPort == null || pi.getBufferId() == 0xffffffff) {
                OFPacketOut po = new OFPacketOut();
                po.setBufferId(bufferId);
                po.setInPort(pi.getInPort());

                // set actions
                OFActionOutput action = new OFActionOutput();
                action.setMaxLength((short) 0);
                action.setPort((short) ((outPort == null) ? OFPort.OFPP_FLOOD
                        .getValue() : outPort));
                List<OFAction> actions = new ArrayList<OFAction>();
                actions.add(action);
                po.setActions(actions);
                po.setActionsLength((short) OFActionOutput.MINIMUM_LENGTH);

                // 设置data
                if (bufferId == 0xffffffff) {
                    byte[] packetData = pi.getPacketData();
                    po.setLength(U16.t(OFPacketOut.MINIMUM_LENGTH
                            + po.getActionsLength() + packetData.length));
                    po.setPacketData(packetData);
                    System.out.println("向交换机发送packet_out消息,携带packet_in.data");
                } else {
                    po.setLength(U16.t(OFPacketOut.MINIMUM_LENGTH
                            + po.getActionsLength()));
                    System.out.println("向交换机发送packet_out消息");
//                    byte[] packetData = pi.getPacketData();
//                    po.setPacketData(packetData);
                }
                try {
                    stream.write(po);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public String toString() {
            InetAddress remote = sock.socket().getInetAddress();
            return remote.getHostAddress() + ":" + sock.socket().getPort();
        }

        public OFMessageAsyncStream getStream() {
            return stream;
        }
    }

    public SimpleController(int port) throws IOException{
        //打开监听信道
        listenSock = ServerSocketChannel.open();
        //设置为非阻塞模式
        listenSock.configureBlocking(false);
        //与本地端口绑定 
        listenSock.socket().bind(new java.net.InetSocketAddress(port));
        //确保一个进程关闭socket后，即使它还没释放端口，同一主机上的其他进程可以立刻重用该端口
        listenSock.socket().setReuseAddress(true);
        this.port = port;
        switchSelectLoops = new ArrayList<SelectLoop>();
        switchSockets = new ConcurrentHashMap<SocketChannel,OFSwitch>();
        threadCount = 1;
        listenSelectLoop = new SelectLoop(this);
        // 将这个连接注册为ACCEPT
        listenSelectLoop.register(listenSock, SelectionKey.OP_ACCEPT, listenSock);

        this.factory = new BasicFactory();
    }
    /* doLoop()函数里面的handleEvent
     * 用来处理总的NIO通道(ServerSocketChannel)和各个交换机的通道SocketChannel
     * (non-Javadoc)
     * @see org.openflow.example.SelectListener#handleEvent(java.nio.channels.SelectionKey, java.lang.Object)
     */
    @Override
    public void handleEvent(SelectionKey key, Object arg) throws IOException {
//        handlePushFlowEvent(key,(SocketChannel) arg);
        if (arg instanceof ServerSocketChannel)
            handleListenEvent(key, (ServerSocketChannel)arg);//ServerSocketChannel总的通道
        else
            handleSwitchEvent(key, (SocketChannel) arg);//SocketChannel各个交换机的通道
    }

    protected void handleListenEvent(SelectionKey key, ServerSocketChannel ssc)
            throws IOException {
        SocketChannel sock = listenSock.accept();
        OFMessageAsyncStream stream = new OFMessageAsyncStream(sock, factory);
        switchSockets.put(sock, new OFSwitch(sock, stream));
        System.err.println("Got new connection from " + switchSockets.get(sock));
        List<OFMessage> l = new ArrayList<OFMessage>();
        l.add(factory.getMessage(OFType.HELLO));//返回一个HELLO类型的Msg
        l.add(factory.getMessage(OFType.FEATURES_REQUEST));//返回一个FEATURES_REQUEST类型的Msg
//        l.add(flow_mod);
        stream.write(l);

        int ops = SelectionKey.OP_READ;
        if (stream.needsFlush())
            ops |= SelectionKey.OP_WRITE;

        // 将这个交换机HASH到进程内
        SelectLoop sl = switchSelectLoops.get(sock.hashCode() % switchSelectLoops.size());
        sl.register(sock, ops, sock);
        // 强迫返回或者输入一个新的SelectionKey
        sl.wakeup();
//        System.out.println(switchSockets.toString());
    }
    protected void handleSwitchEvent(SelectionKey key, SocketChannel sock) {
        OFSwitch sw = switchSockets.get(sock);
        OFMessageAsyncStream stream = sw.getStream();
        Feature_ReplyServiceImp feature_service = new Feature_ReplyServiceImp();
        PortStatusServiceImp portStatus_service = new PortStatusServiceImp();
        Flow_ModServiceImp flow_service = new Flow_ModServiceImp();
//        ErrorServiceImp error_service = new ErrorServiceImp();
        Map<String,String> physicalPort = new HashMap<String,String>();
        try {
            if (key.isReadable()) {
                List<OFMessage> msgs = stream.read();//读取报文
                if (msgs == null) {
                    key.cancel();
                    switchSockets.remove(sock);
                    return;
                }

                for (OFMessage m : msgs) {
                    switch (m.getType()) {
                        case PACKET_IN:
                            System.err.println("交换机："+sw+",GOT PACKET_IN");
                            sw.handlePacketIn((OFPacketIn) m);
                            break;
                        case HELLO:
                            System.err.println("GOT HELLO from " + sw);
                            break;
                        case FEATURES_REPLY:
                            System.err.println("GOT FEATURES_REPLY from " + sw);
                            physicalPort = feature_service.getPortName((OFFeaturesReply) m);
                            for (Map.Entry<String, String> entry : physicalPort.entrySet()) {  
                                System.err.println("PortName = " + entry.getKey() + ", HardAddress = " + entry.getValue());  
                            }  
                            List<OFMatch> match_list = new ArrayList<OFMatch>();
                            match_list = flow_service.getMatchList();
                            for(int i=0; i<match_list.size() ; i++){
                                OFMatch match = new OFMatch();
                                match = match_list.get(i);
                                sw.pushFlowMod(match);
                            }
                            break;
                        case PORT_STATUS:
                            String portStatus_reason = portStatus_service.getReason((OFPortStatus) m);
                            //强制转化类型
                            OFPortStatus p_status_mes = (OFPortStatus) m;
                            OFPhysicalPort p_status_port = p_status_mes.getDesc();
                            System.err.println("原因："+portStatus_reason+"，端口："+p_status_port.getName()+"硬件地址："+
                                    macToString.toString(p_status_port.getHardwareAddress()));
                            break;
                        case ECHO_REQUEST:
//                            OFEchoRequest echo_request = (OFEchoRequest) m;
//                            byte[] payload = echo_request.getPayload();//输出时延，但未空。
//                            System.err.println("收到ECHO_REQUEST报文 ，"+"payload:"+
//                                    StringByteSerializer.byteToHexString(payload));
                            OFEchoReply reply = (OFEchoReply) stream
                                .getMessageFactory().getMessage(
                                        OFType.ECHO_REPLY);
                            reply.setXid(m.getXid());
                            stream.write(reply);
                            break;
                        case FLOW_REMOVED:
                            OFFlowRemoved flow_removed = (OFFlowRemoved) m;
                            System.err.println("交换机："+sw+",发送flow_removed的原因："+
                            flow_service.getFlowRemovedReason(flow_removed)+
                            ",流表匹配个数："+U32.t(flow_removed.getPacketCount()));
                            break;
                        case ERROR:
//                            OFError error = (OFError) m;
//                            System.err.println("错误信息："+error_service.getErrorTypeMsg(error.getErrorType()));
                            break;
                        default:
                            System.err.println("为处理消息格式为: "
                                    + m.getType() + " from "
                                    + sock.socket().getInetAddress());
                    }
                }                
            }
            if (key.isWritable()) {
                stream.flush();
            }

            /**
             * 如果register是READ或者WRITE中的一个，而不是both，才会在一段时间后造成stream死锁
             */
            if (stream.needsFlush())
                key.interestOps(SelectionKey.OP_WRITE);
            else
                key.interestOps(SelectionKey.OP_READ);
        } catch (IOException e) {
            // 如果发生异常，与交换机断开连接
            key.cancel();
            switchSockets.remove(sock);
        }
    }

    public void run() throws IOException{
        System.err.println("Starting " + this.getClass().getCanonicalName() + 
                " on port " + this.port + " with " + this.threadCount + " threads");
        // 处理器核心个数等于固定的线程个数(每次提交一个任务就创建一个线程)
        es = Executors.newFixedThreadPool(threadCount);


        // 在一个threadCount上执行一个select loop
        for (int i = 0; i < threadCount; ++i) {
            final SelectLoop sl = new SelectLoop(this);
            switchSelectLoops.add(sl);
            es.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        sl.doLoop();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }}
            );
        }

        // 开始监听循环
        listenSelectLoop.doLoop();
    }

    /*
     * CLI序列化
     */
    public static SimpleCLI parseArgs(String[] args) {
        Options options = new Options();
        options.addOption("h", "help", "print help");
        // options.addOption("n", true, "the number of packets to send");
        options.addOption("p", "port", 6633, "the port to listen on");
        options.addOption("t", "threads", 1, "the number of threads to run");
        try {
            SimpleCLI cmd = SimpleCLI.parse(options, args);
            if (cmd.hasOption("h")) {
                printUsage(options);
                System.exit(0);
            }
            return cmd;
        } catch (ParseException e) {
            System.err.println(e);
            printUsage(options);
        }

        System.exit(-1);
        return null;
    }

    public static void printUsage(Options options) {
        SimpleCLI.printHelp("Usage: "
                + SimpleController.class.getCanonicalName() + " [options]",
                options);
    }
    public static void main(String [] args) throws IOException {
        SimpleCLI cmd = parseArgs(args);
        int port = Integer.valueOf(cmd.getOptionValue("p"));//PORT=6633
        SimpleController sc = new SimpleController(port);
        sc.threadCount = Integer.valueOf(cmd.getOptionValue("t"));
        sc.run();
    }
}
