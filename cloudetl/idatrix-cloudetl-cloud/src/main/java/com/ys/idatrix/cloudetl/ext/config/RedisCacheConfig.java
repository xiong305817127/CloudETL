/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.ext.config;

import java.util.Arrays;

import org.pentaho.di.core.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.ys.idatrix.cloudetl.ext.utils.RedisUtil;

import redis.clients.jedis.JedisPoolConfig;

/**
 * CloudDubboConfig.java
 * 
 * @author JW
 * @since 2017年7月27日
 *
 */
@Configuration
@PropertySource("file:./config/idatrix.properties")
//@EnableCaching
public class RedisCacheConfig implements EnvironmentAware {

	private final static Logger LOGGER = LoggerFactory.getLogger(RedisCacheConfig.class);

	private Environment env;

	/**
	 * 配置JedisPoolConfig实例
	 * 
	 * @return
	 */
	@Bean
	@Conditional(RedisDeploymentCondition.class)
	public JedisPoolConfig poolConfig() {
		JedisPoolConfig poolConfig = new JedisPoolConfig();
		poolConfig.setMaxIdle(Integer.valueOf(env.getProperty("redis.maxIdle", "20").trim())); //最大能够保持idel状态的对象数
		poolConfig.setMaxTotal(Integer.valueOf(env.getProperty("redis.maxActive", "200").trim())); //最大活动对象数  
		poolConfig.setMaxWaitMillis(Integer.valueOf(env.getProperty("redis.maxWait", "5000").trim())); //当池内没有返回对象时，最大等待时间
		poolConfig.setTestOnBorrow(Boolean.valueOf(env.getProperty("redis.testOnBorrow", "true").trim())); //当调用borrow Object方法时，是否进行有效性检查
		LOGGER.debug("Redis - poolConfig()");
		return poolConfig;
	}

	/**
	 * 配置JedisConnectionFactory
	 * 
	 * @return
	 */
	@Bean
	@Conditional(RedisDeploymentCondition.class)
	public JedisConnectionFactory jedisConnectionFactory(JedisPoolConfig poolConfig) {
		JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory();
		jedisConnectionFactory.setHostName(env.getProperty("redis.host"));
		jedisConnectionFactory.setPort(Integer.valueOf(env.getProperty("redis.port", "6379").trim()));
		String pass = env.getProperty("redis.pass");
		if (!Utils.isEmpty(pass)) {
			jedisConnectionFactory.setPassword(pass);
		}
		jedisConnectionFactory.setDatabase(Integer.valueOf(env.getProperty("redis.dbIndex", "1").trim()));
		jedisConnectionFactory.setPoolConfig(poolConfig);
		LOGGER.debug("Redis - jedisConnectionFactory()");
		return jedisConnectionFactory;
	}

	@Bean
	@Conditional(RedisDeploymentCondition.class)
	public StringRedisSerializer keySerializer() {
		return new StringRedisSerializer();
	}

	@Bean
	@Conditional(RedisDeploymentCondition.class)
	public GenericJackson2JsonRedisSerializer valueSerializer() {
		return new GenericJackson2JsonRedisSerializer();
	}

	/**
	 * 配置RedisTemplate
	 * 
	 * @return
	 */
	@Bean
	@Conditional(RedisDeploymentCondition.class)
	public RedisTemplate<String, Object> redisTemplate(SimpleCacheManager cacheManager ,JedisConnectionFactory jedisConnectionFactory,
			StringRedisSerializer keySerializer, GenericJackson2JsonRedisSerializer valueSerializer) {

		RedisTemplate<String, Object> redisTemplate = new RedisTemplate<String, Object>();
		redisTemplate.setConnectionFactory(jedisConnectionFactory);
		redisTemplate.setKeySerializer(keySerializer);
		redisTemplate.setValueSerializer(valueSerializer);
		redisTemplate.setHashKeySerializer(keySerializer);
		redisTemplate.setHashValueSerializer(valueSerializer);
		
		//启用事物 会造成 连接释放时不会自动关闭,最终报 Could not get a resource from the pool 
		//本项目 不需要事物管理,如需要 可新建两个 RedisTemplate ,一个带事物一个不带 ,带事物要使用 @Transactional
		//详细见 : http://www.cnblogs.com/qijiang/p/5626461.html
		redisTemplate.setEnableTransactionSupport(false);

		RedisUtil.setRedisTemplate(redisTemplate);
		
		RedisCache cache = new RedisCache("default", null, redisTemplate, Long.valueOf(env.getProperty("redis.expiration", "0").trim()), false); 
		cacheManager.setCaches(Arrays.asList(cache));
		cacheManager.initializeCaches();
		
		LOGGER.debug("Redis - redisTemplate()");
		return redisTemplate;
	}

//	/**
//	 * 配置RedisCacheManager
//	 * 
//	 * @return
//	 */
//	@Bean
//	@Conditional(RedisDeploymentCondition.class)
//	public CacheManager redisCacheManager(RedisTemplate<String, Object> redisTemplate) {
//		RedisCacheManager redisCacheManager = new RedisCacheManager(redisTemplate);
//		redisCacheManager.setDefaultExpiration(Long.valueOf(env.getProperty("redis.expiration", "0").trim()));
//		LOGGER.debug("Redis - redisCacheManager()");
//		return redisCacheManager;
//
//	}
//
//	/**
//	 * @EnableCaching 注解需要使用 CacheManager,当没有配置redis时,给以默认的缓存管理器
//	 * @return
//	 */
//	@Bean
//	@Conditional(RedisNonDeploymentCondition.class)
//	public CacheManager defaultCacheManager() {
//		SimpleCacheManager cacheManager = new SimpleCacheManager();
//		cacheManager.setCaches(Arrays.asList(new ConcurrentMapCache("sampleCache")));
//		return cacheManager;
//	}

	/**
	 * 通过spring管理redis缓存配置
	 * 

	 * 
	 * @author Administrator
	 *
	 */
//	@Bean
//	@Conditional(RedisDeploymentCondition.class)
//	public CachingConfigurerSupport springCacheConfig(CacheManager cacheManager) {
//		LOGGER.debug("Redis - redisCacheConfig()");
//		return new CachingConfigurerSupport() {
//			@Override
//			public CacheManager cacheManager() {
//				return cacheManager;
//			}
//
//			@Override
//			public KeyGenerator keyGenerator() {
//				return new KeyGenerator() {
//					@Override
//					public Object generate(Object target, Method method, Object... objects) {
//						StringBuilder sb = new StringBuilder();
//						sb.append(target.getClass().getName());
//						sb.append(method.getName());
//						for (Object obj : objects) {
//							sb.append(obj.toString());
//						}
//						return sb.toString();
//					}
//				};
//			}
//		};
//	}

	/*
	 * @see org.springframework.context.EnvironmentAware#setEnvironment(org.
	 * springframework.core.env.Environment)
	 */
	@Override
	public void setEnvironment(Environment environment) {
		env = environment;
	}

}
