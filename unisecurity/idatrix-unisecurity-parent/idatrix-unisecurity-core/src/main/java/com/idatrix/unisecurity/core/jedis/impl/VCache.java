package com.idatrix.unisecurity.core.jedis.impl;

import com.idatrix.unisecurity.common.utils.SerializeUtil;
import com.idatrix.unisecurity.core.jedis.JedisClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("unchecked")
public class VCache {

    private Logger log = LoggerFactory.getLogger(getClass());

    private JedisClient jedisClient;

    private int db_index = 0;// 默认为0

    private VCache() {

    }

    public void setJedisClient(JedisClient jedisClient) {
        this.jedisClient = jedisClient;
    }

    public void setDb_index(int db_index) {
        this.db_index = db_index;
    }


    /**
     * 简单的Get
     *
     * @param <T>
     * @param key
     * @param requiredType
     * @return
     */
    public <T> T get(String key, Class<T>... requiredType) {
        try {
            byte[] skey = SerializeUtil.serialize(key);
            byte[] value = jedisClient.get(db_index, skey);
            return SerializeUtil.deserialize(value, requiredType);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("redis get error message：{}" + e.getMessage());
        }
        return null;
    }

    /**
     * 简单的set
     *
     * @param key
     * @param value
     */
    public void set(Object key, Object value) {
        try {
            byte[] skey = SerializeUtil.serialize(key);
            byte[] svalue = SerializeUtil.serialize(value);
            jedisClient.setex(db_index, skey, svalue, 0);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("redis set error message：{}" + e.getMessage());
        }
    }

    /**
     * 过期时间的set
     *
     * @param key
     * @param value
     * @param timer （秒）
     */
    public void setex(Object key, Object value, int timer) {
        try {
            byte[] skey = SerializeUtil.serialize(key);
            byte[] svalue = SerializeUtil.serialize(value);
            jedisClient.setex(db_index, skey, svalue, timer);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }

    }

    /**
     * @param <T>
     * @param mapkey       map
     * @param key          map里的key
     * @param requiredType value的泛型类型
     * @return

    public <T> T getVByMap(String mapkey, String key, Class<T> requiredType) {
        Jedis jds = null;
        boolean isBroken = false;
        try {
            jds = jedisManager.getJedis();
            jds.select(0);
            byte[] mkey = SerializeUtil.serialize(mapkey);
            byte[] skey = SerializeUtil.serialize(key);
            List<byte[]> result = jds.hmget(mkey, skey);
            if (null != result && result.size() > 0) {
                byte[] x = result.get(0);
                T resultObj = SerializeUtil.deserialize(x, requiredType);
                return resultObj;
            }
        } catch (Exception e) {
            isBroken = true;
            e.printStackTrace();
        } finally {
            returnResource(jds, isBroken);
        }
        return null;
    }*/

    /**
     * @param mapkey map
     * @param key    map里的key
     * @param value  map里的value

    public void setVByMap(String mapkey, String key, Object value) {
        Jedis jds = null;
        boolean isBroken = false;
        try {
            jds = jedisManager.getJedis();
            jds.select(0);
            byte[] mkey = SerializeUtil.serialize(mapkey);
            byte[] skey = SerializeUtil.serialize(key);
            byte[] svalue = SerializeUtil.serialize(value);
            jds.hset(mkey, skey, svalue);
        } catch (Exception e) {
            isBroken = true;
            e.printStackTrace();
        } finally {
            returnResource(jds, isBroken);
        }

    }*/

    /**
     * 删除Map里的值
     *
     * @param mapKey
     * @param dkey
     * @return

    public Object delByMapKey(String mapKey, String... dkey) {
        Jedis jds = null;
        boolean isBroken = false;
        try {
            jds = jedisManager.getJedis();
            jds.select(0);
            byte[][] dx = new byte[dkey.length][];
            for (int i = 0; i < dkey.length; i++) {
                dx[i] = SerializeUtil.serialize(dkey[i]);
            }
            byte[] mkey = SerializeUtil.serialize(mapKey);
            Long result = jds.hdel(mkey, dx);
            return result;
        } catch (Exception e) {
            isBroken = true;
            e.printStackTrace();
        } finally {
            returnResource(jds, isBroken);
        }
        return Long.valueOf(0);
    }*/

    /**
     * 往redis里取set整个集合
     *
     * @param <T>
     * @param setKey
     * @param requiredType
     * @return

    public <T> Set<T> getVByList(String setKey, Class<T> requiredType) {
        Jedis jds = null;
        boolean isBroken = false;
        try {
            jds = jedisManager.getJedis();
            jds.select(0);
            byte[] lkey = SerializeUtil.serialize(setKey);
            Set<T> set = new TreeSet<T>();
            Set<byte[]> xx = jds.smembers(lkey);
            for (byte[] bs : xx) {
                T t = SerializeUtil.deserialize(bs, requiredType);
                set.add(t);
            }
            return set;
        } catch (Exception e) {
            isBroken = true;
            e.printStackTrace();
        } finally {
            returnResource(jds, isBroken);
        }
        return null;
    }*/

    /**
     * 获取Set长度
     *
     * @param setKey
     * @return

    public Long getLenBySet(String setKey) {
        Jedis jds = null;
        boolean isBroken = false;
        try {
            jds = jedisManager.getJedis();
            jds.select(0);
            Long result = jds.scard(setKey);
            return result;
        } catch (Exception e) {
            isBroken = true;
            e.printStackTrace();
        } finally {
            returnResource(jds, isBroken);
        }
        return null;
    }*/

    /**
     * 删除Set
     *
     * @param dkey
     * @return

    public Long delSetByKey(String key, String... dkey) {
        Jedis jds = null;
        boolean isBroken = false;
        try {
            jds = jedisManager.getJedis();
            jds.select(0);
            Long result = 0L;
            if (null == dkey) {
                result = jds.srem(key);
            } else {
                result = jds.del(key);
            }
            return result;
        } catch (Exception e) {
            isBroken = true;
            e.printStackTrace();
        } finally {
            returnResource(jds, isBroken);
        }
        return Long.valueOf(0);
    }*/

    /**
     * 随机 Set 中的一个值
     *
     * @param key
     * @return

    public String srandmember(String key) {
        Jedis jds = null;
        boolean isBroken = false;
        try {
            jds = jedisManager.getJedis();
            jds.select(0);
            String result = jds.srandmember(key);
            return result;
        } catch (Exception e) {
            isBroken = true;
            e.printStackTrace();
        } finally {
            returnResource(jds, isBroken);
        }
        return null;
    }*/

    /**
     * 往redis里存Set
     *
     * @param setKey
     * @param value

    public void setVBySet(String setKey, String value) {
        Jedis jds = null;
        boolean isBroken = false;
        try {
            jds = jedisManager.getJedis();
            jds.select(0);
            jds.sadd(setKey, value);
        } catch (Exception e) {
            isBroken = true;
            e.printStackTrace();
        } finally {
            returnResource(jds, isBroken);
        }
    }*/

    /**
     * 取set
     *
     * @param key
     * @return

    public Set<String> getSetByKey(String key) {
        Jedis jds = null;
        boolean isBroken = false;
        try {
            jds = jedisManager.getJedis();
            jds.select(0);
            Set<String> result = jds.smembers(key);
            return result;
        } catch (Exception e) {
            isBroken = true;
            e.printStackTrace();
        } finally {
            returnResource(jds, isBroken);
        }
        return null;

    }*/


    /**
     * 往redis里存List
     *
     * @param listKey
     * @param value

    public void setVByList(String listKey, Object value) {
        Jedis jds = null;
        boolean isBroken = false;
        try {
            jds = jedisManager.getJedis();
            jds.select(0);
            byte[] lkey = SerializeUtil.serialize(listKey);
            byte[] svalue = SerializeUtil.serialize(value);
            jds.rpush(lkey, svalue);
        } catch (Exception e) {
            isBroken = true;
            e.printStackTrace();
        } finally {
            returnResource(jds, isBroken);
        }
    }*/

    /**
     * 往redis里取list
     *
     * @param <T>
     * @param listKey
     * @param start
     * @param end
     * @param requiredType
     * @return

    public <T> List<T> getVByList(String listKey, int start, int end, Class<T> requiredType) {
        Jedis jds = null;
        boolean isBroken = false;
        try {
            jds = jedisManager.getJedis();
            jds.select(0);
            byte[] lkey = SerializeUtil.serialize(listKey);
            List<T> list = new ArrayList<T>();
            List<byte[]> xx = jds.lrange(lkey, start, end);
            for (byte[] bs : xx) {
                T t = SerializeUtil.deserialize(bs, requiredType);
                list.add(t);
            }
            return list;
        } catch (Exception e) {
            isBroken = true;
            e.printStackTrace();
        } finally {
            returnResource(jds, isBroken);
        }
        return null;
    }*/

    /**
     * 获取list长度
     *
     * @param listKey
     * @return

    public Long getLenByList(String listKey) {
        Jedis jds = null;
        boolean isBroken = false;
        try {
            jds = jedisManager.getJedis();
            jds.select(0);
            byte[] lkey = SerializeUtil.serialize(listKey);
            Long result = jds.llen(lkey);
            return result;
        } catch (Exception e) {
            isBroken = true;
            e.printStackTrace();
        } finally {
            returnResource(jds, isBroken);
        }
        return null;
    }*/

    /**
     * 删除
     *
     * @param dkey
     * @return

    public Long delByKey(String... dkey) {
        Jedis jds = null;
        boolean isBroken = false;
        try {
            jds = jedisManager.getJedis();
            jds.select(0);
            byte[][] dx = new byte[dkey.length][];
            for (int i = 0; i < dkey.length; i++) {
                dx[i] = SerializeUtil.serialize(dkey[i]);
            }
            Long result = jds.del(dx);
            return result;
        } catch (Exception e) {
            isBroken = true;
            e.printStackTrace();
        } finally {
            returnResource(jds, isBroken);
        }
        return Long.valueOf(0);
    }*/

    /**
     * 判断是否存在
     *
     * @param existskey
     * @return

    public boolean exists(String existskey) {
        Jedis jds = null;
        boolean isBroken = false;
        try {
            jds = jedisManager.getJedis();
            jds.select(0);
            byte[] lkey = SerializeUtil.serialize(existskey);
            return jds.exists(lkey);
        } catch (Exception e) {
            isBroken = true;
            e.printStackTrace();
        } finally {
            returnResource(jds, isBroken);
        }
        return false;
    }*/
}