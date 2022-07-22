package io.shulie.takin.web.biz.cache.agentimpl;

import io.shulie.takin.utils.security.MD5Utils;
import io.shulie.takin.web.biz.cache.AbstractAgentConfigCache;
import io.shulie.takin.web.biz.service.linkmanage.AppRemoteCallService;
import io.shulie.takin.web.biz.service.scriptmanage.ScriptManageService;
import io.shulie.takin.web.common.vo.agent.AgentRemoteCallVO;
import io.shulie.takin.web.data.model.mysql.FileManageEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author 无涯
 * @date 2021/6/4 4:32 下午
 */
@Component
public class FileManageSignCache extends AbstractAgentConfigCache<Object> {

    public static final String CACHE_NAME = "t:a:c:pressure:filemd5";

    @Autowired
    private ScriptManageService scriptManageService;

    public FileManageSignCache(@Autowired RedisTemplate redisTemplate) {
        super(CACHE_NAME, redisTemplate);
    }

    @PostConstruct
    public void reset() {
        queryValue(null);
    }

    @Override
    protected Object queryValue(String namespace) {
        /**
         * 把文件路径和签名保存到缓存中,cloud也要使用
         */
        List<FileManageEntity> list = scriptManageService.getAllFile();
        for(FileManageEntity entity:list){
            String key = null;
            try {
                key = MD5Utils.getInstance().getMD5(entity.getUploadPath().replaceAll("[/]", ""));
            } catch (Exception e) {
                e.printStackTrace();
            }
            String value = entity.getMd5();
            redisTemplate.opsForValue().set(CACHE_NAME+key, value);
        }
        return null;
    }

    /**
     * 正式文件
     * @param key
     * @param value
     */
    public void setValue(String key,String value) {
        redisTemplate.opsForValue().set(CACHE_NAME+key, value);
    }
}
