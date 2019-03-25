package com.idatrix.resource.common.cache;

import com.idatrix.resource.datareport.dao.DataUploadDAO;
import com.idatrix.resource.exchange.dao.ExchangeSubscribeTaskDAO;
import com.idatrix.resource.subscribe.dao.SubscribeDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 *  在分布式情况下从Redis获取 序列号
 *   Note: 需要保证序列号唯一
 */

@Component
public class SequenceNumberManager {

    private static final Logger LOG = LoggerFactory.getLogger(SequenceNumberManager.class);

    private final static String SEQ_PREFIX = "resource-seqnum";

    private final static String SUBSCRIBE_PREFIX = "subscribe-seqnum";

    private final static String EXCHANGE_PREFIX = "exchange-seqnum";

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private DataUploadDAO dataUploadDAO;

    @Autowired
    private SubscribeDAO subscribeDAO;

    @Autowired
    private ExchangeSubscribeTaskDAO exchangeDAO;

    @PostConstruct
    public void init(){
        Long seqInLib = dataUploadDAO.getMaxTaskSeq();
        setNX(SEQ_PREFIX, seqInLib.toString());

        //TODO 第三方系统接入时候：申请的数据库信息在资源里面不能显示，则都暂存在 rc_exchange_task,生成任务 id需要获取最大数据
        Long subscirbeSeqMax = subscribeDAO.getMaxSubscribeSeq();
        Long exchangeSeqMax = exchangeDAO.getMaxSubscribeSeq();
        Long subscribeValue = subscirbeSeqMax>exchangeSeqMax?subscirbeSeqMax:exchangeSeqMax;
        setNX(SUBSCRIBE_PREFIX, subscribeValue.toString());

    }

    private boolean setNX(final String key, final String value) {
        Object obj = null;
        try {
            obj = redisTemplate.execute(new RedisCallback<Object>() {
                @Override
                public Object doInRedis(RedisConnection connection) throws DataAccessException {
                    StringRedisSerializer serializer = new StringRedisSerializer();
                    Boolean success = connection.setNX(serializer.serialize(key), serializer.serialize(value));
                    connection.close();
                    return success;
                }
            });
        } catch (Exception e) {
            LOG.error("setNX redis error, key : {}", key);
        }
        return obj != null ? (Boolean) obj : false;
    }

    private Long getValue(final String key) throws Exception{
        Object obj = null;
        Long value = null;
        try {
            obj = redisTemplate.execute(new RedisCallback<Object>() {
                @Override
                public Object doInRedis(RedisConnection connection) throws DataAccessException {
                    StringRedisSerializer serializer = new StringRedisSerializer();
                    Long value = connection.incr(serializer.serialize(key));
                    connection.close();
                    return value;
                }
            });
        } catch (Exception e) {
            LOG.error("get key-{} fail", key);
            throw new Exception(e);
        }
        return (Long)obj;
    }

    /*获取序列号*/
    public Long getSeqNum() throws Exception{
        return getValue(SEQ_PREFIX);
    }

    /*获取序列号*/
    public Long getSubscribeSeqNum() throws Exception{
        return getValue(SUBSCRIBE_PREFIX);
    }

}
