import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import javax.crypto.Cipher;

import java.security.*;
import java.util.Base64;

public class GenRSAPrivateKeyTest {
    static {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }

    public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchProviderException {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA", "BC");
        kpg.initialize(2048);
        KeyPair kp = kpg.generateKeyPair();

        String privateKey = Base64.getEncoder().encodeToString(kp.getPrivate().getEncoded());
        System.out.println("-----BEGIN PRIVATE KEY-----");
        System.out.println(privateKey);
        System.out.println("-----END PRIVATE KEY-----");

    }

    @Test
    public void testMaxAesBits() throws NoSuchAlgorithmException {
        System.out.println("Max allowed key length for AES: " +
                Cipher.getMaxAllowedKeyLength("AES") + " bits");
    }



}
