package com.ys.idatrix.cloudetl.ext.utils;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.di.core.util.IdatrixPropertyUtil;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.util.CollectionUtils;

/**
 *  基于spring和redis的getRedisTemplate()工具类 <br>
 *  针对所有的hash 都是以h开头的方法<br>
 *  针对所有的Set  都是以s开头的方法 (ps.部分通用方法也是以s开头) <br>
 *  针对所有的List 都是以l开头的方法
 */
public class RedisUtil {

	public static final Log  logger = LogFactory.getLog("RedisUtil");
	
	private static RedisTemplate<String, Object> redisTemplate;

	
	public static void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
		RedisUtil.redisTemplate = redisTemplate;
	}
	
	public static RedisTemplate<String, Object> getRedisTemplate() {
		if(redisTemplate == null) {
			throw new RuntimeException("redis没有配置!");
		}
		return  redisTemplate;
	}
	
	public static boolean isCacheEnable() {
		String ddProperty = IdatrixPropertyUtil.getProperty("idatrix.redis.cache.enable", "false");
		return "true".equalsIgnoreCase(ddProperty) && redisTemplate != null;
	}


	// =============================common============================
	/**
	 * 指定缓存失效时间
	 * 
	 * @param key
	 *            键
	 * @param time
	 *            时间(秒)
	 * @return
	 */
	public  static  boolean expire(String key, long time) {
		try {
			if (time > 0) {
				getRedisTemplate().expire(key, time, TimeUnit.SECONDS);
			}
			return true;
		} catch (Exception e) {
			logger.error("redis 操作失败.",e);
			return false;
		}
	}

	/**
	 * 根据key 获取过期时间
	 * 
	 * @param key
	 *            键 不能为null
	 * @return 时间(秒) 返回0代表为永久有效
	 */
	public  static  long getExpire(String key) {
		return getRedisTemplate().getExpire(key, TimeUnit.SECONDS);
	}

	/**
	 * 判断key是否存在
	 * 
	 * @param key
	 *            键
	 * @return true 存在 false不存在
	 */
	public  static  boolean hasKey(String key) {
		try {
			return getRedisTemplate().hasKey(key);
		} catch (Exception e) {
			logger.error("redis 操作失败.",e);
			return false;
		}
	}

	/**
	 * 删除缓存
	 * 
	 * @param key
	 *            可以传一个值 或多个
	 */
	@SuppressWarnings("unchecked")
	public  static  void del(String... key) {
		if (key != null && key.length > 0) {
			if (key.length == 1) {
				getRedisTemplate().delete(key[0]);
			} else {
				getRedisTemplate().delete(CollectionUtils.arrayToList(key));
			}
		}
	}

	// ============================String=============================
	/**
	 * 普通缓存获取
	 * 
	 * @param key
	 *            键
	 * @return 值
	 */
	public  static  Object get(String key) {
		return key == null ? null : getRedisTemplate().opsForValue().get(key);
	}

	/**
	 * 普通缓存放入
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @return true成功 false失败
	 */
	public  static  boolean set(String key, Object value) {
		try {
			getRedisTemplate().opsForValue().set(key, value);
			return true;
		} catch (Exception e) {
			logger.error("redis 操作失败.",e);
			return false;
		}

	}

	/**
	 * 普通缓存放入并设置时间
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @param time
	 *            时间(秒) time要大于0 如果time小于等于0 将设置无限期
	 * @return true成功 false 失败
	 */
	public  static  boolean set(String key, Object value, long time) {
		try {
			if (time > 0) {
				getRedisTemplate().opsForValue().set(key, value, time, TimeUnit.SECONDS);
			} else {
				set(key, value);
			}
			return true;
		} catch (Exception e) {
			logger.error("redis 操作失败.",e);
			return false;
		}
	}

	/**
	 * 递增
	 * 
	 * @param key
	 *            键
	 * @param by
	 *            要增加几(大于0)
	 * @return
	 */
	public  static  long incr(String key, long delta) {
		if (delta < 0) {
			throw new RuntimeException("递增因子必须大于0");
		}
		return getRedisTemplate().opsForValue().increment(key, delta);
	}

	/**
	 * 递减
	 * 
	 * @param key
	 *            键
	 * @param by
	 *            要减少几(小于0)
	 * @return
	 */
	public  static  long decr(String key, long delta) {
		if (delta < 0) {
			throw new RuntimeException("递减因子必须大于0");
		}
		return getRedisTemplate().opsForValue().increment(key, -delta);
	}

	// ================================Map=================================
	/**
	 * HashGet
	 * 
	 * @param key
	 *            键 不能为null
	 * @param item
	 *            项 不能为null
	 * @return 值
	 */
	public  static  Object hget(String key, String item) {
		return getRedisTemplate().opsForHash().get(key, item);
	}

	/**
	 * 获取hashKey对应的所有键值
	 * 
	 * @param key
	 *            键
	 * @return 对应的多个键值
	 */
	public  static  Map<Object, Object> hmget(String key) {
		return getRedisTemplate().opsForHash().entries(key);
	}
	

	/**
	 * HashSet
	 * 
	 * @param key
	 *            键
	 * @param map
	 *            对应多个键值
	 * @return true 成功 false 失败
	 */
	public  static  boolean hmset(String key, Map<Object, Object> map) {
		try {
			getRedisTemplate().opsForHash().putAll(key, map);
			return true;
		} catch (Exception e) {
			logger.error("redis 操作失败.",e);
			return false;
		}
	}

	/**
	 * HashSet 并设置时间
	 * 
	 * @param key
	 *            键
	 * @param map
	 *            对应多个键值
	 * @param time
	 *            时间(秒)
	 * @return true成功 false失败
	 */
	public  static  boolean hmset(String key, Map<String, Object> map, long time) {
		try {
			getRedisTemplate().opsForHash().putAll(key, map);
			if (time > 0) {
				expire(key, time);
			}
			return true;
		} catch (Exception e) {
			logger.error("redis 操作失败.",e);
			return false;
		}
	}

	/**
	 * 向一张hash表中放入数据,如果不存在将创建
	 * 
	 * @param key
	 *            键
	 * @param item
	 *            项
	 * @param value
	 *            值
	 * @return true 成功 false失败
	 */
	public  static  boolean hset(String key, String item, Object value) {
		try {
			getRedisTemplate().opsForHash().put(key, item, value);
			return true;
		} catch (Exception e) {
			logger.error("redis 操作失败.",e);
			return false;
		}
	}

	/**
	 * 向一张hash表中放入数据,如果不存在将创建
	 * 
	 * @param key
	 *            键
	 * @param item
	 *            项
	 * @param value
	 *            值
	 * @param time
	 *            时间(秒) 注意:如果已存在的hash表有时间,这里将会替换原有的时间
	 * @return true 成功 false失败
	 */
	public  static  boolean hset(String key, String item, Object value, long time) {
		try {
			getRedisTemplate().opsForHash().put(key, item, value);
			if (time > 0) {
				expire(key, time);
			}
			return true;
		} catch (Exception e) {
			logger.error("redis 操作失败.",e);
			return false;
		}
	}

	/**
	 * 删除hash表中的值
	 * 
	 * @param key
	 *            键 不能为null
	 * @param item
	 *            项 可以使多个 不能为null
	 */
	public  static  void hdel(String key, Object... itemKeys) {
		getRedisTemplate().opsForHash().delete(key, itemKeys);
	}

	/**
	 * 判断hash表中是否有该项的值
	 * 
	 * @param key
	 *            键 不能为null
	 * @param item
	 *            项 不能为null
	 * @return true 存在 false不存在
	 */
	public  static  boolean hHasKey(String key, String item) {
		return getRedisTemplate().opsForHash().hasKey(key, item);
	}

	/**
	 * hash递增 如果不存在,就会创建一个 并把新增后的值返回
	 * 
	 * @param key
	 *            键
	 * @param item
	 *            项
	 * @param by
	 *            要增加几(大于0)
	 * @return
	 */
	public  static  double hincr(String key, String item, double by) {
		return getRedisTemplate().opsForHash().increment(key, item, by);
	}

	/**
	 * hash递减
	 * 
	 * @param key
	 *            键
	 * @param item
	 *            项
	 * @param by
	 *            要减少记(小于0)
	 * @return
	 */
	public  static  double hdecr(String key, String item, double by) {
		return getRedisTemplate().opsForHash().increment(key, item, -by);
	}

	// ============================set=============================
	/**
	 * 根据key获取Set中的所有值
	 * 
	 * @param key
	 *            键
	 * @return
	 */
	public  static  Set<Object> sGet(String key) {
		try {
			return getRedisTemplate().opsForSet().members(key);
		} catch (Exception e) {
			logger.error("redis 操作失败.",e);
			return null;
		}
	}

	/**
	 * 根据value从一个set中查询,是否存在
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @return true 存在 false不存在
	 */
	public  static  boolean sHasKey(String key, Object value) {
		try {
			return getRedisTemplate().opsForSet().isMember(key, value);
		} catch (Exception e) {
			logger.error("redis 操作失败.",e);
			return false;
		}
	}

	/**
	 * 将数据放入set缓存
	 * 
	 * @param key
	 *            键
	 * @param values
	 *            值 可以是多个
	 * @return 成功个数
	 */
	public  static  long sSet(String key, Object... values) {
		try {
			return getRedisTemplate().opsForSet().add(key, values);
		} catch (Exception e) {
			logger.error("redis 操作失败.",e);
			return 0;
		}
	}

	/**
	 * 将set数据放入缓存
	 * 
	 * @param key
	 *            键
	 * @param time
	 *            时间(秒)
	 * @param values
	 *            值 可以是多个
	 * @return 成功个数
	 */
	public  static  long sSetAndTime(String key, long time, Object... values) {
		try {
			Long count = getRedisTemplate().opsForSet().add(key, values);
			if (time > 0)
				expire(key, time);
			return count;
		} catch (Exception e) {
			logger.error("redis 操作失败.",e);
			return 0;
		}
	}

	/**
	 * 获取set缓存的长度
	 * 
	 * @param key
	 *            键
	 * @return
	 */
	public  static  long sGetSetSize(String key) {
		try {
			return getRedisTemplate().opsForSet().size(key);
		} catch (Exception e) {
			logger.error("redis 操作失败.",e);
			return 0;
		}
	}

	/**
	 * 移除值为value的
	 * 
	 * @param key
	 *            键
	 * @param values
	 *            值 可以是多个
	 * @return 移除的个数
	 */
	public  static  long setRemove(String key, Object... values) {
		try {
			Long count = getRedisTemplate().opsForSet().remove(key, values);
			return count;
		} catch (Exception e) {
			logger.error("redis 操作失败.",e);
			return 0;
		}
	}
	// ===============================list=================================

	/**
	 * 获取list缓存的内容
	 * 
	 * @param key
	 *            键
	 * @param start
	 *            开始
	 * @param end
	 *            结束 0 到 -1代表所有值
	 * @return
	 */
	public  static  List<Object> lGet(String key, long start, long end) {
		try {
			return getRedisTemplate().opsForList().range(key, start, end);
		} catch (Exception e) {
			logger.error("redis 操作失败.",e);
			return null;
		}
	}

	/**
	 * 获取list缓存的长度
	 * 
	 * @param key
	 *            键
	 * @return
	 */
	public  static  long lGetListSize(String key) {
		try {
			return getRedisTemplate().opsForList().size(key);
		} catch (Exception e) {
			logger.error("redis 操作失败.",e);
			return 0;
		}
	}

	/**
	 * 通过索引 获取list中的值
	 * 
	 * @param key
	 *            键
	 * @param index
	 *            索引 index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推
	 * @return
	 */
	public  static  Object lGetIndex(String key, long index) {
		try {
			return getRedisTemplate().opsForList().index(key, index);
		} catch (Exception e) {
			logger.error("redis 操作失败.",e);
			return null;
		}
	}
	
	/**
	 *  队列方式,从左边放入值到队列
	 * @param key
	 * @param value
	 * @return
	 */
	public  static  Object lpush(String key,  Object value) {
		try {
			return getRedisTemplate().opsForList().leftPush(key, value);
		} catch (Exception e) {
			logger.error("redis 操作失败.",e);
			return null;
		}
	}
	
	/**
	 *  队列方式,从右边获取值
	 * @param key
	 * @param value
	 * @return
	 */
	public  static  Object lpop(String key) {
		try {
			return getRedisTemplate().opsForList().rightPop(key);
		} catch (Exception e) {
			logger.error("redis 操作失败.",e);
			return null;
		}
	}

	/**
	 * 将list放入缓存
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @param time
	 *            时间(秒)
	 * @return
	 */
	public  static  boolean lSet(String key, Object value) {
		try {
			getRedisTemplate().opsForList().rightPush(key, value);
			return true;
		} catch (Exception e) {
			logger.error("redis 操作失败.",e);
			return false;
		}
	}

	/**
	 * 将list放入缓存
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @param time
	 *            时间(秒)
	 * @return
	 */
	public  static  boolean lSet(String key, Object value, long time) {
		try {
			getRedisTemplate().opsForList().rightPush(key, value);
			if (time > 0)
				expire(key, time);
			return true;
		} catch (Exception e) {
			logger.error("redis 操作失败.",e);
			return false;
		}
	}

	/**
	 * 将list放入缓存
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @param time
	 *            时间(秒)
	 * @return
	 */
	public  static  boolean lSet(String key, List<Object> value) {
		try {
			getRedisTemplate().opsForList().rightPushAll(key, value);
			return true;
		} catch (Exception e) {
			logger.error("redis 操作失败.",e);
			return false;
		}
	}

	/**
	 * 将list放入缓存
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @param time
	 *            时间(秒)
	 * @return
	 */
	public  static  boolean lSet(String key, List<Object> value, long time) {
		try {
			getRedisTemplate().opsForList().rightPushAll(key, value);
			if (time > 0)
				expire(key, time);
			return true;
		} catch (Exception e) {
			logger.error("redis 操作失败.",e);
			return false;
		}
	}

	/**
	 * 根据索引修改list中的某条数据
	 * 
	 * @param key
	 *            键
	 * @param index
	 *            索引
	 * @param value
	 *            值
	 * @return
	 */
	public  static  boolean lUpdateIndex(String key, long index, Object value) {
		try {
			getRedisTemplate().opsForList().set(key, index, value);
			return true;
		} catch (Exception e) {
			logger.error("redis 操作失败.",e);
			return false;
		}
	}

	/**
	 * 移除N个值为value
	 * 
	 * @param key
	 *            键
	 * @param count
	 *            移除多少个
	 * @param value
	 *            值
	 * @return 移除的个数
	 */
	public  static  long lRemove(String key, long count, Object value) {
		try {
			Long remove = getRedisTemplate().opsForList().remove(key, count, value);
			return remove;
		} catch (Exception e) {
			logger.error("redis 操作失败.",e);
			return 0;
		}
	}
	
	/**
	 * 移除除了start到end之外的所有数据(只保留start到end的数据),0表示第一个,-1表示最后一个
	 * 
	 * @param key
	 *            键
	 * @param count
	 *            移除多少个
	 * @param value
	 *            值
	 * @return 移除的个数
	 */
	public  static  void ltrim(String key, long start, long end) {
		try {
			getRedisTemplate().opsForList().trim(key, start, end);
		} catch (Exception e) {
			logger.error("redis 操作失败.",e);
		}
	}
	
	//=============================zset====================================
	
	
	/**
	 * 通过索引获取排序set缓存的内容
	 * 
	 * @param key
	 *            键
	 * @param start
	 *            开始
	 * @param end
	 *            结束 0 到 -1代表所有值
	 * @return
	 */
	public  static Set<TypedTuple<Object>>  zGet(String key, long start, long end) {
		try {
			return getRedisTemplate().opsForZSet().rangeWithScores(key, start, end);
		} catch (Exception e) {
			logger.error("redis 操作失败.",e);
			return null;
		}
	}

	/**
	 * 通过Score获取排序set缓存的内容
	 * 
	 * @param key
	 *            键
	 * @param start
	 *            开始
	 * @param end
	 *            结束 0 到 -1代表所有值
	 * @return
	 */
	public  static  Set<Object> zGet(String key, double min,double max) {
		try {
			return getRedisTemplate().opsForZSet().rangeByScore(key, min, max);
		} catch (Exception e) {
			logger.error("redis 操作失败.",e);
			return null;
		}
	}

	/**
	 * 通过索引 获取排序set中的值
	 * 
	 * @param key
	 *            键
	 * @param index
	 *            索引 index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推
	 * @return
	 */
	public  static TypedTuple<Object>  zGetByIndex(String key, long index) {
		try {
			 Set<TypedTuple<Object>> result = getRedisTemplate().opsForZSet().rangeWithScores(key, index, index);
			if(result.iterator().hasNext()) {
				return result.iterator().next();
			}
			return null;
		} catch (Exception e) {
			logger.error("redis 操作失败.",e);
			return null;
		}
	}
	
	/**
	 * 通过Score 获取排序set中的值
	 * 
	 * @param key
	 *            键
	 * @param index
	 *            索引 index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推
	 * @return
	 */
	public  static Object zGetByScore(String key, double score) {
		try {
			Set<Object> result = getRedisTemplate().opsForZSet().rangeByScore(key, score, score);
			if(result.iterator().hasNext()) {
				return result.iterator().next();
			}
			return null;
		} catch (Exception e) {
			logger.error("redis 操作失败.",e);
			return null;
		}
	}
	
	/**
	 * 获取排序set缓存的长度
	 * 
	 * @param key
	 *            键
	 * @return
	 */
	public  static  long zGetSize(String key) {
		try {
			return getRedisTemplate().opsForZSet().size(key);
		} catch (Exception e) {
			logger.error("redis 操作失败.",e);
			return 0;
		}
	}
	
	/**
	 * 获取排序set缓存的值的score
	 * 
	 * @param key
	 *            键
	 * @return
	 */
	public  static  double zGetScore(String key, Object value) {
		try {
			return getRedisTemplate().opsForZSet().score(key,value);
		} catch (Exception e) {
			logger.error("redis 操作失败.",e);
			return 0;
		}
	}
	
	/**
	 * 获取排序set缓存的值的索引
	 * 
	 * @param key
	 *            键
	 * @return
	 */
	public  static  long zGetIndex(String key, Object value) {
		try {
			return getRedisTemplate().opsForZSet().rank(key,value);
		} catch (Exception e) {
			logger.error("redis 操作失败.",e);
			return 0;
		}
	}



	/**
	 * 将排序set放入缓存
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @param time
	 *            时间(秒)
	 * @return
	 */
	public  static  boolean zSet(String key, double score , Object value) {
		try {
			return getRedisTemplate().opsForZSet().add(key, value, score);
		} catch (Exception e) {
			logger.error("redis 操作失败.",e);
			return false;
		}
	}

	/**
	 * 将排序set放入缓存
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @param time
	 *            时间(秒)
	 * @return
	 */
	public  static  boolean zSet(String key, double score , Object value, long time) {
		try {
			getRedisTemplate().opsForZSet().add(key, value, score);
			if (time > 0)
				expire(key, time);
			return true;
		} catch (Exception e) {
			logger.error("redis 操作失败.",e);
			return false;
		}
	}

	/**
	 * 将排序set放入缓存
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @param time
	 *            时间(秒)
	 * @return
	 */
	public  static  boolean zSet(String key, List<Object> values) {
		try {
			
			Set<ZSetOperations.TypedTuple<Object>> tuples = new HashSet<ZSetOperations.TypedTuple<Object>>();
			for(Double i=0d;i<values.size();i++) {
				 ZSetOperations.TypedTuple<Object> objectTypedTuple = new DefaultTypedTuple<Object>(values.get(i.intValue()),i);
				 tuples.add(objectTypedTuple);
			}
			getRedisTemplate().opsForZSet().add(key, tuples);
			return true;
		} catch (Exception e) {
			logger.error("redis 操作失败.",e);
			return false;
		}
	}
	

	/**
	 * 将排序set放入缓存
	 * @param key
	 * @param values
	 * @return
	 */
	public  static  boolean zSet(String key, Map<Double,Object> values) {
		try {
			
			Set<ZSetOperations.TypedTuple<Object>> tuples = new HashSet<ZSetOperations.TypedTuple<Object>>();
			for(Double score : values.keySet()) {
				 ZSetOperations.TypedTuple<Object> objectTypedTuple = new DefaultTypedTuple<Object>(values.get(score),score);
				 tuples.add(objectTypedTuple);
			}
			getRedisTemplate().opsForZSet().add(key, tuples);
			return true;
		} catch (Exception e) {
			logger.error("redis 操作失败.",e);
			return false;
		}
	}

	/**
	 * 移除值为value的项
	 * 
	 * @param key
	 *            键
	 * @param count
	 *            移除多少个
	 * @param value
	 *            值
	 * @return 移除的个数
	 */
	public  static  long zRemove(String key, Object... value) {
		try {
			Long remove = getRedisTemplate().opsForZSet().remove(key, value);
			return remove;
		} catch (Exception e) {
			logger.error("redis 操作失败.",e);
			return 0;
		}
	}
	
	/**
	 * 根据索引移除项
	 * 
	 * @param key
	 *            键
	 * @param count
	 *            移除多少个
	 * @param value
	 *            值
	 * @return 移除的个数
	 */
	public  static  long zRemoveByIndex(String key, long start ,long end) {
		try {
			Long remove = getRedisTemplate().opsForZSet().removeRange(key, start, end);
			return remove;
		} catch (Exception e) {
			logger.error("redis 操作失败.",e);
			return 0;
		}
	}
	
	/**
	 * 根据score移除项
	 * 
	 * @param key
	 *            键
	 * @param count
	 *            移除多少个
	 * @param value
	 *            值
	 * @return 移除的个数
	 */
	public  static  long zRemoveByScore(String key, double min ,double max) {
		try {
			Long remove = getRedisTemplate().opsForZSet().removeRangeByScore(key, min, max);
			return remove;
		} catch (Exception e) {
			logger.error("redis 操作失败.",e);
			return 0;
		}
	}
	


	/**
	 * 搜索
	 * 
	 * @param key
	 *            键
	 * @param index
	 *            索引
	 * @param value
	 *            值
	 * @return
	 */
	public  static  TypedTuple<Object> zScan(String key, String  patternKey,boolean minScore) {
		try {
			//System.out.println("开始搜索======"+System.currentTimeMillis());
			ScanOptions options = ScanOptions.scanOptions().match("*"+patternKey+"*").count(1000).build();  
			Cursor<TypedTuple<Object>> cursor = getRedisTemplate().opsForZSet().scan(key, options);
			 TypedTuple<Object> result = null ;
			 while (cursor.hasNext()) {  
				 TypedTuple<Object> r = cursor.next();
				 if(result == null) {
					 result =r;
					 continue ;
				 }
				 if((minScore && r.getScore() < result.getScore()) || (!minScore && r.getScore() > result.getScore())) {
					 result = r;
				 }
			 }
			// System.out.println(System.currentTimeMillis());
			return result;
		} catch (Exception e) {
			logger.error("redis 操作失败.",e);
			return null;
		}
	}

}