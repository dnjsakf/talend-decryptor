package src;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.talend.utils.security.StudioEncryption;
import org.talend.utils.security.StudioEncryption.EncryptionKeyName;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TalendDecryptor {
    static {
        System.setProperty("log4j2.level", "OFF");
        System.setProperty("org.apache.logging.log4j.simplelog.StatusLogger.level", "OFF");
    }

    public static void main(String[] args) throws Exception {
        String cwd = System.getProperty("user.dir");

        if (args.length < 1) {
            System.out.println("Usage: java -cp \".;lib/*\" TalendDecryptor <WorkspacePath> [<ConnectionName>]");
            return;
        }

        String decryptLastConnection = args.length > 1 ? args[1] : "Local";
        String decryptLicenseManagement = "1";
        String decryptDefine = createDefine(decryptLastConnection, args[0]);
        String decryptInstallDone = "true";

        String encryptLastConnection = encrypt(decryptLastConnection);
        String encryptLicenseManagement = encrypt(decryptLicenseManagement);
        String encryptDefine = encrypt(decryptDefine);
        String encryptInstallDone = encrypt(decryptInstallDone);

        Properties props = new Properties();
        props.setProperty("connection.lastConnection", encryptLastConnection);
        props.setProperty("connection.licenseManagement", encryptLicenseManagement);
        props.setProperty("connection.define", encryptDefine);
        props.setProperty("connection.installDone", encryptInstallDone);

        File file = Paths.get(cwd, "connection_user.properties").toAbsolutePath().toFile();
        props.store(new FileOutputStream(file), null);
        System.out.println("=====>"+ file.getAbsolutePath());
    }

    public static String createDefine(String connName, String  wsPath) throws JsonProcessingException {
        // 1. 데이터를 구조화 (Map 사용)
        Map<String, Object> connection = new HashMap<>();
        connection.put("complete", true);
        connection.put("description", "Default connection");
        connection.put("dynamicFields", new HashMap<>());
        connection.put("id", "local");
        connection.put("name", connName);
        connection.put("password", "");
        connection.put("token", false);
        connection.put("user", "user@talend.com");
        connection.put("workSpace", wsPath);
        
        List<Map<String, Object>> list = Collections.singletonList(connection);
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(list);
    }

    public static String decrypt(String encryptedData) {
        String decrypted = null;
        try {
            StudioEncryption instance = StudioEncryption.getStudioEncryption(EncryptionKeyName.SYSTEM);
            if (instance != null) {
                decrypted = instance.decrypt(encryptedData);
            } else {
                System.out.println("암호화 인스턴스를 생성할 수 없습니다. studio.keys 경로를 확인하세요.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return decrypted;
    }

    public static String encrypt(String plainText) {
        String encrypted = null;
        try {
            StudioEncryption instance = StudioEncryption.getStudioEncryption(EncryptionKeyName.SYSTEM);
            if (instance != null) {
                String encryptedRaw = instance.encrypt(plainText);
                encrypted = encryptedRaw;
            } else {
                System.out.println("암호화 인스턴스 생성 실패. studio.keys 위치를 확인하세요.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encrypted;
    }

}