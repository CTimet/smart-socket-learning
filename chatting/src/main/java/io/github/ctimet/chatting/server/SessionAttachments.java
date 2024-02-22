package io.github.ctimet.chatting.server;

import io.github.ctimet.chatting.server.processor.UserLoginProcessor;
import lombok.Getter;
import lombok.Setter;
import org.smartboot.socket.extension.decoder.FixedLengthFrameDecoder;

public class SessionAttachments {
    @Getter
    @Setter
    private FixedLengthFrameDecoder decoder;

    @Getter
    @Setter
    private UserLoginProcessor userLoginProcessor;
    public SessionAttachments() {
        this.decoder = null;
    }

}
