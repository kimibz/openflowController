package org.openflow.example;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/***
 * @author xigua
 * simple SelectLoop for simple java controller
 */


public class SelectLoop {
    protected SelectListener callback;
    protected boolean dontStop;
    protected Object registrationLock;
    protected int registrationRequests = 0;
    protected Queue<Object[]> registrationQueue;//队列先进先出原则
    protected Selector selector;
    protected long timeout;

    public SelectLoop(SelectListener cb) throws IOException {
        callback = cb;
        dontStop = true;
        selector = SelectorProvider.provider().openSelector();
        registrationLock = new Object();
        registrationQueue = new ConcurrentLinkedQueue<Object[]>();
        timeout = 0;
    }

    /**
     * 初始化selectLoop
     * @param cb 当select回复对连接的回复
     * @param timeout 超时时间
     * @throws IOException
     */
    public SelectLoop(SelectListener cb, long timeout) throws IOException {
        callback = cb;
        dontStop = true;
        selector = SelectorProvider.provider().openSelector();
        registrationLock = new Object();
        registrationQueue = new ConcurrentLinkedQueue<Object[]>();
        this.timeout = timeout;
    }

    public void register(SelectableChannel ch, int ops, Object arg)
            throws ClosedChannelException {
        registrationQueue.add(new Object[] {ch, ops, arg});
    }

    /** 用这个selectloop向所提供的的selectChannel注册（register）的时候，注意，
     * 当注册进行时，这个方法是阻塞的。建议在一个空闲时间
     * 使用这个方法来初始化selectLoop
     * @param ch the channel
     * @param ops interest ops
     * @param arg 和selectListener一起回复给arg
     * @return
     * @throws ClosedChannelException
     */
    public synchronized SelectionKey registerBlocking(SelectableChannel ch, int ops, Object arg)
            throws ClosedChannelException {
        synchronized (registrationLock) {
            registrationRequests++;
        }
        selector.wakeup();
        SelectionKey key = ch.register(selector, ops, arg);
        synchronized (registrationLock) {
            registrationRequests--;
            registrationLock.notifyAll();//唤醒一个等待的线程
        }
        return key;
    }

    /****
     * 最高层的IO来循环这些IO事件和TIMER事件
     */
    public void doLoop() throws IOException {
        int nEvents;
        processRegistrationQueue();

        while (dontStop) {
            nEvents = selector.select(timeout);//监听注册通道，当其中有注册的 IO,操作可以进行时，该函数返回
            if (nEvents > 0) {
                //取得迭代器.selectedKeys()中包含了每个准备好某一I/O操作的信道的SelectionKey  
                // Selected-key Iterator 代表了所有通过 select()方法监测到可以进行 IO 操作的 channel,  
                // 这个集合可以通过 selectedKeys() 拿到  
                for (Iterator<SelectionKey> i = selector.selectedKeys()
                        .iterator(); i.hasNext();) {
                    SelectionKey sk = i.next();//获取连接到CONTROLLER的SWITCH的编号
                    i.remove();//注册任务完成

                    if (!sk.isValid())//检查连接Server的Client是否已经实例化
                        continue;

                    Object arg = sk.attachment();
                    callback.handleEvent(sk, arg);
                }
            }

            if (this.registrationQueue.size() > 0)
                processRegistrationQueue();

            if (registrationRequests > 0) {
                synchronized (registrationLock) {
                    while (registrationRequests > 0) {
                        try {
                            registrationLock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    protected void processRegistrationQueue() {
        // 向队列queue添加任何element
        for (Iterator<Object[]> it = registrationQueue.iterator(); it.hasNext();) {
            Object[] args = it.next();
            SelectableChannel ch = (SelectableChannel) args[0];
            try {
                ch.register(selector, (Integer) args[1], args[2]);
            } catch (ClosedChannelException e) {
                e.printStackTrace();
            }
            it.remove();
        }
    }

    /**
     * 强迫一个selectLoop立即回复并且重选select
     * 例子：如果一个新的ITEM被添加到这个SELECT LOOP里当这个SELECT LOOP是阻塞的时候
     */
    public void wakeup() {
       if (selector != null) {
           selector.wakeup();
       }
    }

    /**
     * 关闭这个select loop
     */
    public void shutdown() {
        this.dontStop = false;
        wakeup();
    }
}
