package io.github.ctimet.chatting.server.protocol;

import io.github.ctimet.chatting.server.SessionAttachments;
import org.smartboot.socket.Protocol;
import org.smartboot.socket.extension.decoder.FixedLengthFrameDecoder;
import org.smartboot.socket.transport.AioSession;

import java.nio.ByteBuffer;

public class MsgProtocol implements Protocol<String> {
    @Override
    public String decode(ByteBuffer readBuffer, AioSession session) {
        if (!readBuffer.hasRemaining() | readBuffer.remaining() < 4) {
            return null;
        }

        readBuffer.mark(); //标志起始位置
        int length = readBuffer.getInt(); //解析消息长度

        if (length <= (readBuffer.capacity()-4)) {
            //1类消息解码
            if (readBuffer.remaining() < length) { //消息内容不够，出现半包，继续等待数据到达
                readBuffer.reset(); //重置标志位
                return null;
            }

            byte[] body = new byte[length];
            readBuffer.get(body); // 读取完整消息
            readBuffer.mark(); // 重置标志位
            return new String(body); // 返回消息
        } else {
            //2类消息解码
            FixedLengthFrameDecoder decoder;
            SessionAttachments attachments = session.getAttachment();
            if (attachments.getDecoder() != null) {
                decoder = attachments.getDecoder();
            } else {
                decoder = new FixedLengthFrameDecoder(length);
                attachments.setDecoder(decoder);
            }

            if (!decoder.decode(readBuffer)) {
                readBuffer.reset();//出现半包
                return null;
            }

            //数据读取完毕
            ByteBuffer fullBuffer = decoder.getBuffer();
            byte[] bytes = new byte[fullBuffer.remaining()];
            fullBuffer.get(bytes);
            attachments.setDecoder(null);//释放临时缓冲区
            readBuffer.mark();//重置标志位，防止粘包
            return new String(bytes);
        }
    }
}
