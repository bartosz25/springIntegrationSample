package com.waitingforcode.messagestores;

import org.springframework.integration.store.BasicMessageGroupStore;
import org.springframework.integration.store.MessageGroup;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * A message store is "messages container". It holds all messages of given channel. Here we'll use a simple implementation based
 * on {@link java.util.HashMap}. However, you can imagine as well to implement physical storage,
 * as files or database. By the way, some of basic message stores are already implemented in Spring Integration (as JDBC
 * message store).
 *
 * Handled messages are grouped. As you could see, MessageStore are associated to channels definitions. The name (id) of
 * associated channel will be used here as the name of group. So in our case, we'll define {@link ProductMessageStore} to
 * channel identified by queueChannelWithMS. The name of the group of this channel's messages will be "queueChannelWithMS".
 *
 * @author Bartosz Konieczny
 */
public class ProductMessageStore<Product> implements BasicMessageGroupStore {

    private final Map<Object, MessageGroup> messages = new HashMap<Object, MessageGroup>();

    private String backupBasePath;

    public void setBackupBasePath(String path) {
        this.backupBasePath = path;
    }

    public String getBackupBasePath() {
        return this.backupBasePath;
    }

    @Override
    public int messageGroupSize(Object groupId) {
        return getMessageGroup(groupId).size();
    }

    @Override
    public MessageGroup getMessageGroup(Object groupId) {
        if (messages.get(groupId) == null) {
            messages.put(groupId, new ProductMessageGroupWithBackup(groupId, backupBasePath));
        }
        return messages.get(groupId);
    }

    @Override
    public MessageGroup addMessageToGroup(Object groupId, Message<?> message) {
        MessageGroup group = getMessageGroup(groupId);
        if (group.canAdd(message)) {
            group.getMessages().add(message);
        }
        return group;
    }

    @Override
    public Message<?> pollMessageFromGroup(Object groupId) {
        return getMessageGroup(groupId).getOne();
    }

    @Override
    public void removeMessageGroup(Object groupId) {
        messages.remove(groupId);
    }
}
