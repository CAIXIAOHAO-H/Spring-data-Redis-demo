package cwh.redis.config;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import cwh.redis.listener.AnimalListener;
import cwh.redis.listener.CatListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
public class JedisAutoConfiguration {

    public JedisAutoConfiguration() {
    }

    @Bean
    public JedisPoolConfig jedisPoolConfig(JedisConfig config) {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(config.getMaxIdle());
        jedisPoolConfig.setMaxWaitMillis(config.getMaxWaitMillis());
        jedisPoolConfig.setMaxTotal(config.getMaxTotal());
        jedisPoolConfig.setMinIdle(config.getMinIdle());
        return jedisPoolConfig;
    }

    @Bean
    public JedisConnectionFactory jedisConnectionFactory(JedisConfig config) {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setHostName(config.getHost());
        configuration.setPort(config.getPort());
        configuration.setDatabase(config.getDatabase());
        configuration.setPassword(RedisPassword.of(config.getPassword()));

        JedisClientConfiguration.JedisPoolingClientConfigurationBuilder jpb =
                (JedisClientConfiguration.JedisPoolingClientConfigurationBuilder) JedisClientConfiguration.builder();
        jpb.poolConfig(jedisPoolConfig(config));

        return new JedisConnectionFactory(configuration, jpb.build());
    }

    @Bean
    public RedisTemplate redisTemplate(JedisConfig config) {
        RedisTemplate template = new RedisTemplate();

        StringRedisSerializer srs = new StringRedisSerializer();

        template.setConnectionFactory(jedisConnectionFactory(config));
        template.setKeySerializer(srs);
        template.setHashKeySerializer(srs);
        template.setValueSerializer(getDefaultSerializer());
        template.setHashValueSerializer(getDefaultSerializer());
        template.afterPropertiesSet();
        return template;
    }

    @Bean({"jackson2JsonRedisSerializer"})
    public Jackson2JsonRedisSerializer<Object> getDefaultSerializer() {
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        // 在序列化中增加类信息，否则无法反序列化。
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        return jackson2JsonRedisSerializer;
    }

    @Bean
    public RedisMessageListenerContainer container(JedisConnectionFactory jedisConnectionFactory){
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(jedisConnectionFactory);
        //container.addMessageListener(new RedisExpiredListener(), new PatternTopic("__keyevent@0__:expired"));
        container.addMessageListener(new CatListener(),new PatternTopic("cat"));
        container.addMessageListener(new AnimalListener(),new PatternTopic("cat"));
        return container;
    }

}
