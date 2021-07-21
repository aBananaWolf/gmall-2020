package com.atguigu.com.gmall.sso.properties;

import com.atguigu.core.utils.RsaUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * @author wyl
 * @create 2020-07-03 12:47
 */
@ConfigurationProperties("com.atguigu.token")
public class JWTProperties {
    private Integer expire;
    private String genPath;
    private String salt;
    private String cookieName;
    private Integer cookieMaxAge;
    private PrivateKey privateKey;
    private PublicKey publicKey;
    private String privateKeyStr;
    private String publicKeyStr;

    @PostConstruct
    void init() throws Exception {
        if (!StringUtils.isNotEmpty(privateKeyStr) && !StringUtils.isNotEmpty(publicKeyStr) ) {
            char lastCharacter = genPath.charAt(genPath.length() - 1);
            int i = genPath.lastIndexOf("/");
            if (i != -1 && genPath.charAt(i) == lastCharacter)
                genPath = genPath.substring(0, genPath.length() - 1);
            int k = genPath.lastIndexOf("\\");
            if (k != -1 && genPath.charAt(k) == lastCharacter)
                genPath = genPath.substring(0, genPath.length() - 1);

            this.privateKeyStr = genPath + "\\gmall_rsa.pri";
            this.publicKeyStr = genPath + "\\gmall_rsa.pub";
        }
        File priFile = new File(privateKeyStr);
        File pubFile = new File(publicKeyStr);
        if (!priFile.exists() || !pubFile.exists())
            RsaUtils.generateKey(publicKeyStr, privateKeyStr,salt);
        byte[] priBytes = Files.readAllBytes(Paths.get(priFile.toURI()));
        byte[] pubBytes = Files.readAllBytes(Paths.get(pubFile.toURI()));
        this.privateKey = RsaUtils.getPrivateKey(priBytes);
        this.publicKey = RsaUtils.getPublicKey(pubBytes);
        this.cookieMaxAge = expire * 60;
    }

    public Integer getCookieMaxAge() {
        return cookieMaxAge;
    }

    public void setCookieMaxAge(Integer cookieMaxAge) {
        this.cookieMaxAge = cookieMaxAge;
    }

    public Integer getExpire() {
        return expire;
    }

    public void setExpire(Integer expire) {
        this.expire = expire;
    }

    public String getGenPath() {
        return genPath;
    }

    public void setGenPath(String genPath) {
        this.genPath = genPath;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getCookieName() {
        return cookieName;
    }

    public void setCookieName(String cookieName) {
        this.cookieName = cookieName;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(PrivateKey privateKey) {
        this.privateKey = privateKey;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public String getPrivateKeyStr() {
        return privateKeyStr;
    }

    public void setPrivateKeyStr(String privateKeyStr) {
        this.privateKeyStr = privateKeyStr;
    }

    public String getPublicKeyStr() {
        return publicKeyStr;
    }

    public void setPublicKeyStr(String publicKeyStr) {
        this.publicKeyStr = publicKeyStr;
    }
}
