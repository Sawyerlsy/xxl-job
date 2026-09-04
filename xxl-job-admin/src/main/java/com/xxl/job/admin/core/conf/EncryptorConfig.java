package com.xxl.job.admin.core.conf;

import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * EncryptorConfig 加密属性
 * <p>主密钥优先从JVM参数和环境变量读取，未设置时使用硬编码默认值，兼容本地启动和Docker Swarm部署</p>
 *
 * <h3>密钥配置方式（按优先级从高到低）</h3>
 * <ol>
 *     <li>JVM参数：java -Djasypt.encryptor.password=自定义密钥 -jar app.jar</li>
 *     <li>环境变量：在docker-stack.yml的environment段配置 JASYPT_ENCRYPTOR_PASSWORD=自定义密钥</li>
 *     <li>硬编码默认值：上述均未设置时使用（兼容已有部署，无需改动即可启动）</li>
 * </ol>
 *
 * <h3>验证方式</h3>
 * <ul>
 *     <li>本地jar启动（不传参数）：使用硬编码默认值，行为与优化前一致，启动后数据库/Redis连接成功即正常</li>
 *     <li>本地jar启动（传JVM参数）：java -Djasypt.encryptor.password=自定义密钥 -jar app.jar，需配合该密钥重新加密ENC()密文</li>
 *     <li>Docker Swarm部署：docker-stack.yml中environment段添加 JASYPT_ENCRYPTOR_PASSWORD=自定义密钥，启动后数据库/Redis连接成功即正常</li>
 * </ul>
 *
 * @author Sawyer
 * @version 1.0
 */
@Configuration
@ConditionalOnClass(StringEncryptor.class)
public class EncryptorConfig {

    /**
     * 加密主密钥
     * 优先级：JVM参数(-Djasypt.encryptor.password=xxx) > 环境变量(JASYPT_ENCRYPTOR_PASSWORD) > 硬编码默认值
     */
    private String password = System.getProperty(
            "jasypt.encryptor.password",
            System.getenv().getOrDefault("JASYPT_ENCRYPTOR_PASSWORD", "86058C6993E36366BE81EE357018988F")
    );

    @Bean("jasyptStringEncryptor")
    public StringEncryptor stringEncryptor() {
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword(password);
        // 算法 PBEWithMD5AndDES 是过时的 PKCS#5 v1.5 标准，MD5 有碰撞漏洞，DES 仅 56 位密钥.这里建议PBEWithHMACSHA512AndAES_256，但是由于现场已经使用了PBEWithMD5AndDES，所以暂时不做修改
        // config.setAlgorithm("PBEWithHMACSHA512AndAES_256");
        config.setAlgorithm("PBEWithMD5AndDES");
        // PBEWithHMACSHA512AndAES_256是设置config.setKeyObtentionIterations("10000");
        config.setKeyObtentionIterations("1000");
        // PBEWithHMACSHA512AndAES_256是设置config.setPoolSize("4");
        config.setPoolSize("1");
        config.setProviderName("SunJCE");
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
        config.setStringOutputType("base64");
        encryptor.setConfig(config);
        return encryptor;
    }
}
