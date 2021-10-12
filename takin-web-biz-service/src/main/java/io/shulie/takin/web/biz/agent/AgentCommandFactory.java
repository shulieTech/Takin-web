package io.shulie.takin.web.biz.agent;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

import com.google.common.collect.Sets;
import io.shulie.takin.channel.ServerChannel;
import io.shulie.takin.channel.bean.CommandPacket;
import io.shulie.takin.channel.bean.CommandRespType;
import io.shulie.takin.channel.bean.CommandResponse;
import io.shulie.takin.channel.bean.CommandSend;
import io.shulie.takin.channel.bean.CommandStatus;
import io.shulie.takin.web.common.exception.ExceptionCode;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.future.ResponseFuture;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Component;

/**
 * @author 无涯
 * @date 2021/1/22 3:37 下午
 */
@Component
@Slf4j
public class AgentCommandFactory {

    @Autowired
    private ServerChannel serverChannel;
    @Autowired
    @Qualifier("redisTemplate")
    private RedisTemplate redisTemplate;

    /**
     * redisKey改造
     * agentId:command:moduleId:tenantId:envCode:id
     */
    private final String agentKey = "%s:%s:%s:%s:%s:%s";

    @Value("${agent.interactive.takin.web.url:http://127.0.0.1:10086/takin-web}")
    private String takinWebUrl;

    public CommandResponse send(AgentCommandEnum commandEnum, String agentId, Map<String, Object> params)
        throws Exception {
        TakinWebCommandPacket takinPacket = getSendPacket(commandEnum, agentId, params);
        checkPacket(takinPacket);
        String key = String.format(agentKey, takinPacket.getAgentId(), takinPacket.getSend().getCommand(),
            takinPacket.getSend().getModuleId(), WebPluginUtils.traceTenantId(),"envCode" , takinPacket.getId());

        ResponseFuture<CommandPacket> future = new ResponseFuture<>(
            takinPacket.getTimeoutMillis() == null ? 3000 : takinPacket.getTimeoutMillis());
        CommandPacket commandPacket = new CommandPacket();
        BeanUtils.copyProperties(takinPacket, commandPacket);
        boolean sendResult = false;
        try {
            sendResult = serverChannel.send(commandPacket, future::success);
        } catch (Exception e) {
            throw new TakinWebException(ExceptionCode.AGENT_SEND_ERROR, "agentId：" + takinPacket.getAgentId() +
                "上传命令失败：" + e.getLocalizedMessage());
        }
        if (!sendResult) {
            log.error("send command failed, serverChannel.send got a false result ...");
            throw new TakinWebException(ExceptionCode.AGENT_REGISTER_ERROR, "agentId：" + takinPacket.getAgentId() + "未注册");
        }

        CommandPacket result = null;
        try {
            result = future.waitFor();
        } catch (InterruptedException e) {
            throw new TakinWebException(ExceptionCode.AGENT_SEND_ERROR, "agentId：" + takinPacket.getAgentId() +
                "获取返回结果失败：" + e.getLocalizedMessage());
        }
        if (CommandRespType.COMMAND_HTTP_PUSH.equals(takinPacket.getCommandRespType())) {
            return CommandResponse.success("返回数据至" + takinPacket.getResponsePushUrl());
        }
        if (result == null) {
            throw new TakinWebException(ExceptionCode.AGENT_RESPONSE_ERROR, "agentId：" + takinPacket.getAgentId() +
                "响应结果为空");
        }
        CommandStatus commandStatus = result.getStatus();
        String commandModule = commandPacket.getSend().getModuleId();
        switch (commandStatus) {
            case COMMAND_SEND:
                log.error("execute command error. no response for this command [{}]:", commandModule);
                redisTemplate.opsForValue().set(key, CommandStatus.COMMAND_SEND);
                throw new TakinWebException(ExceptionCode.AGENT_RESPONSE_ERROR, "agentId："
                    + takinPacket.getAgentId() + "执行" + commandModule + "超过" + takinPacket.getTimeoutMillis()
                    + "未得到agent响应");
            case COMMAND_RUNNING:
                log.error("execute command error. execute [{}] command timeout: " + commandModule);
                redisTemplate.opsForValue().set(key, CommandStatus.COMMAND_RUNNING);
                throw new TakinWebException(ExceptionCode.AGENT_RESPONSE_ERROR, "agentId："
                    + takinPacket.getAgentId() + "执行" + commandModule + "超时" + takinPacket.getTimeoutMillis());
            case COMMAND_COMPLETED_FAIL:
                log.error("execute command error. execute [{}] command failed: " + commandModule);
                throw new TakinWebException(ExceptionCode.AGENT_RESPONSE_ERROR, "agentId："
                    + takinPacket.getAgentId() + "执行" + commandModule + "失败");
            case COMMAND_COMPLETED_SUCCESS:
                log.info("execute command success. execute [{}] command success: " + commandModule);
                return result.getResponse();
            default: {}
        }
        throw new TakinWebException(ExceptionCode.AGENT_RESPONSE_ERROR, "agentId：" + takinPacket.getAgentId() + "响应失败");
    }

    /**
     * 发送命令体
     *
     * @return
     */
    private TakinWebCommandPacket getSendPacket(AgentCommandEnum commandEnum, String agentId,
        Map<String, Object> params) {
        CommandSend commandSend = new CommandSend();
        commandSend.setModuleId(commandEnum.getModuleId());
        commandSend.setCommand(commandEnum.getCommand());
        commandSend.setAgentId(agentId);
        commandSend.setCommandId(commandEnum.getCommandId());
        if (params != null) {
            commandSend.setParam(params);
        }
        TakinWebCommandPacket takinWebCommandPacket = new TakinWebCommandPacket();
        takinWebCommandPacket.setStatus(CommandStatus.COMMAND_SEND);
        takinWebCommandPacket.setSend(commandSend);
        takinWebCommandPacket.setId(UUID.randomUUID().toString());
        takinWebCommandPacket.setCommandRespType(commandEnum.getCommandRespType());
        if (StringUtils.isNotBlank(commandEnum.getResponsePushUrl())) {
            takinWebCommandPacket.setResponsePushUrl(takinWebUrl + commandEnum.getResponsePushUrl());
        }
        takinWebCommandPacket.setAgentId(agentId);
        if (commandEnum.getTimeoutMillis() != null) {
            takinWebCommandPacket.setTimeoutMillis(commandEnum.getTimeoutMillis());
        }
        takinWebCommandPacket.setIsAllowMultipleExecute(commandEnum.getIsAllowMultipleExecute());
        return takinWebCommandPacket;
    }

    private void checkPacket(TakinWebCommandPacket takinWebPacket) {
        if (!takinWebPacket.getIsAllowMultipleExecute()) {
            // 不允许多次执行，检测命令执行状态
            Set<String> keys = this.keys(String.format(agentKey, takinWebPacket.getAgentId(),
                //redisKey改造
                takinWebPacket.getSend().getCommand(), takinWebPacket.getSend().getModuleId(),"*","*", "*"));
            if (keys.size() > 0) {
                for (String agentKey : keys) {
                    CommandStatus commandStatus = null;
                    try {
                        String[] temp = agentKey.split(":");
                        // 最后一个
                        CommandPacket commandPacket = serverChannel.getCurrentCommand(temp[0], temp[temp.length - 1]);
                        commandStatus = Optional.ofNullable(commandPacket).map(CommandPacket::getStatus).orElse(null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (commandStatus == null) {
                        continue;
                    }
                    if (CommandStatus.COMMAND_COMPLETED_SUCCESS.equals(commandStatus)
                        || CommandStatus.COMMAND_COMPLETED_FAIL.equals(commandStatus)) {
                        redisTemplate.delete(agentKey);
                        continue;
                    }
                    log.error("send command failed,命令{} serverChannel.send not allow multiple execute",
                        takinWebPacket.getSend().getModuleId());
                    throw new TakinWebException(ExceptionCode.AGENT_SEND_ERROR, "agentId：" + takinWebPacket.getAgentId()
                        + ",命令" + takinWebPacket.getSend().getModuleId() + "不允许多次执行");
                }
            }
        }
    }

    public Set<String> keys(String pattern) {
        Set<String> keys = Sets.newHashSet();
        this.scan(pattern, item -> {
            //符合条件的key
            String key = new String(item, StandardCharsets.UTF_8);
            keys.add(key);
        });
        return keys;
    }

    private void scan(String pattern, Consumer<byte[]> consumer) {
        this.redisTemplate.execute((RedisConnection connection) -> {
            try (Cursor<byte[]> cursor = connection.scan(ScanOptions.scanOptions().count(Long.MAX_VALUE).match(pattern)
                .build())) {
                cursor.forEachRemaining(consumer);
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        });
    }

}
