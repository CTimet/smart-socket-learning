package io.github.ctimet.chatting.server.processor;

import cn.hutool.json.JSONObject;
import org.smartboot.socket.transport.AioSession;

import java.util.function.Consumer;

public class UserLoginProcessor {
    private AioSession session;
    public UserLoginProcessor(AioSession session) {
        this.session = session;
    }

    public void process(JSONObject object, Consumer<String> uidConsumer) {
        //这里处理用户登录的逻辑
        //我们约定json中有个uid值，既登录时客户端应该主动向服务端发送uid，这里直接读取
        String uid = object.getStr("uid");
        //为了存储map，我们需要调用回调方法
        uidConsumer.accept(uid);
    }
}
