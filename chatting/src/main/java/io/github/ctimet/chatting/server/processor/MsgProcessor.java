package io.github.ctimet.chatting.server.processor;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import io.github.ctimet.chatting.server.SessionAttachments;
import org.smartboot.socket.MessageProcessor;
import org.smartboot.socket.StateMachineEnum;
import org.smartboot.socket.transport.AioSession;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MsgProcessor implements MessageProcessor<String> {
    private static final Map<String, AioSession> sessionMap = new ConcurrentHashMap<>();

    @Override
    public void process(AioSession session, String s) {
        //我们约定，客户端和服务端之间的通信都是用的json
        JSONObject object = JSONUtil.parseObj(s);
        //解析这个消息需要哪个处理器处理，如果是用户登录(login)，我们就交给login去处理
        switch (object.getStr("unit")) {
            case "login" -> {
                //首先看看这个session有没有绑定一个UserLoginProcessor
                SessionAttachments attachments = session.getAttachment();
                UserLoginProcessor processor;
                if (attachments.getUserLoginProcessor() == null) {
                    processor = new UserLoginProcessor(session);
                    attachments.setUserLoginProcessor(processor);
                } else {
                    processor = attachments.getUserLoginProcessor();
                }

                //把用户登录的屁事交给UserLoginProcessor去处理
                processor.process(object, uid -> sessionMap.put(uid, session));
                //到现在，我们就获取了这个Session对应的用户的uid
            }
            case "chat" -> {
                //遇到chat指令，我们就把消息转发给对应的用户
                //我们约定，此时的json中应该有个toUid字段，表示消息要发给谁
                String toUid = object.getStr("toUid");
                AioSession toSession = sessionMap.get(toUid);
                if (toSession != null) {
                    try {
                        toSession.writeBuffer().writeAndFlush(s.getBytes());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        //以上的所有，我们约定，当客户端创建新的与服务端的链接时，应该先发送login处理的json，在发送chat处理的json
    }

    @Override
    public void stateEvent(AioSession session, StateMachineEnum stateMachineEnum, Throwable throwable) {
        MessageProcessor.super.stateEvent(session, stateMachineEnum, throwable);
    }
}
