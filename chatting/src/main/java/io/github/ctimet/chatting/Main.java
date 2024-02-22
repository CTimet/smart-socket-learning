package io.github.ctimet.chatting;

import io.github.ctimet.chatting.server.processor.MsgProcessor;
import io.github.ctimet.chatting.server.protocol.MsgProtocol;
import org.smartboot.socket.MessageProcessor;
import org.smartboot.socket.Protocol;
import org.smartboot.socket.transport.AioQuickServer;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Protocol<String> protocol = new MsgProtocol();
        MessageProcessor<String> processor = new MsgProcessor();

        AioQuickServer server = new AioQuickServer(8888, protocol, processor);
        server.start();
    }
}
