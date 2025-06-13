package com.nbdeyy.mapper;

import com.nbdeyy.annotation.AutoFill;
import com.nbdeyy.entity.RSAKeyPair;
import com.nbdeyy.enumeration.OperationType;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;

@Mapper
public interface RSAKeyPairMapper {
    /**
     * 查询当前激活的密钥对
     * @return
     */
    @Select("SELECT * FROM rsa_key_pair WHERE is_active = 1 ORDER BY version DESC LIMIT 1")
    RSAKeyPair findActiveKeyPair();

    /**
     * 通过keyId获得密钥对
     * @param keyId
     * @return
     */
    @Select("SELECT * FROM rsa_key_pair WHERE key_id = #{keyId}")
    RSAKeyPair findByKeyId(String keyId);

    /**
     * 插入密钥对
     * @param keyPair
     */
    @AutoFill(value = OperationType.INSERT)
    @Insert("insert into rsa_key_pair (key_id, public_key, private_key, is_active, version, create_time, update_time, expire_time) VALUES " +
            "(#{keyId},#{publicKey},#{privateKey},#{isActive},#{version},#{createTime},#{updateTime},#{expireTime})")
    void insert(RSAKeyPair keyPair);

    /**
     * 将所有的都设置为不激活
     */
    @Update("update rsa_key_pair set is_active = 0")
    void updateAllIsActiveToFalse();

    /**
     * 清理所有过期密钥
     * @param now
     */
    @Delete("delete from rsa_key_pair where expire_time < #{now}")
    void deleteAllExpiredKeys(LocalDateTime now);
}
