package com.waitingforcode.messagestores;

import com.waitingforcode.model.Product;
import org.apache.commons.io.FileUtils;
import org.springframework.integration.store.MessageGroup;
import org.springframework.messaging.Message;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * This class illustrates how to implement a group of messages. A group of messages represents all correlated messages (with
 * similar characteristics) which should live together, in the same context.
 *
 * All messages of this group, before being polled, are saved in backup files associated to given channel.
 *
 * @author Bartosz Konieczny
 */
public class ProductMessageGroupWithBackup implements MessageGroup {

    private Queue<Message<?>> messages = new LinkedList<Message<?>>();
    private Object groupId;
    private boolean additivity = true;
    private long creationTime;
    private long lastUpdateTime;
    private String backupBasePath;

    public ProductMessageGroupWithBackup(Object groupId, String backupBasePath) {
        this.creationTime = System.currentTimeMillis();
        this.groupId = groupId;
        if (!backupBasePath.endsWith("/")) {
            backupBasePath += "/";
        }
        this.backupBasePath = backupBasePath;
    }

    @Override
    public boolean canAdd(Message<?> message) {
        return this.additivity;
    }

    @Override
    public Collection<Message<?>> getMessages() {
        return this.messages;
    }

    @Override
    public Object getGroupId() {
        return this.groupId;
    }

    @Override
    public int getLastReleasedMessageSequenceNumber() {
        return 0;
    }

    /**
     * @return If additivity is false, we consider that messaging queue is complete.
     */
    @Override
    public boolean isComplete() {
        return additivity == false;
    }

    @Override
    public void complete() {
        additivity = false;
    }

    /**
     * @return 0 for unknown sequence size
     */
    @Override
    public int getSequenceSize() {
        return 0;
    }

    @Override
    public int size() {
        return messages.size();
    }

    @Override
    public Message<?> getOne() {
        // retrieve only, remove after if backup was made correctly
        lastUpdateTime = System.currentTimeMillis();
        try {
            backupMessage(this.messages.element());
            return this.messages.poll();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public long getTimestamp() {
        return this.creationTime;
    }

    @Override
    public long getLastModified() {
        return this.lastUpdateTime;
    }

    private void backupMessage(Message<?> message) throws IOException {
        File backupFile = new File(backupBasePath+message.getHeaders().getId().toString());
        FileUtils.writeStringToFile(backupFile, ((Product)message.getPayload()).getName());
    }

}
