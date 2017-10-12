/**
 *
 */
package org.openflow.example;

import java.io.IOException;
import java.nio.channels.SelectionKey;

/**
 * @author xigua
 *
 */
public interface SelectListener {
    /**
     * 告诉selectListener，一个事件刚刚发生
     * @param key 所选择的selectionkey
     * @param arg 
     * @throws IOException
     */
    void handleEvent(SelectionKey key, Object arg) throws IOException;
}
