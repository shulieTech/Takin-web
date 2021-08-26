package io.shulie.takin.web.biz.utils;

import com.alibaba.fastjson.JSON;

import io.shulie.takin.channel.bean.CommandPacket;
import io.shulie.takin.channel.protocal.ChannelProtocol;

/**
 * @author mubai
 * @date 2021-01-04 16:49
 */
public class JsonChannelProtocol implements ChannelProtocol {

    @Override
    public byte[] serialize(CommandPacket packet) {
        String json = JSON.toJSONString(packet);
        byte[] bytes = json.getBytes();
        return bytes;
    }

    @Override
    public String serializeJson(CommandPacket commandPacket) {
        return JSON.toJSONString(commandPacket);
    }

    @Override
    public CommandPacket deserialize(byte[] data) {
        String jsonStr = new String(data);
        CommandPacket commandPacket = JSON.parseObject(jsonStr, CommandPacket.class);
        return commandPacket;
    }
}
