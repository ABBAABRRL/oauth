package finaltest.demo.controller;

import finaltest.demo.dto.AuthServerUserInfoDTO;
import finaltest.demo.utils.JsonUtils;
import io.jsonwebtoken.SignatureException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;

@RestController
@RequestMapping("/Sign")
@Api(tags = "Sign")
@RequiredArgsConstructor
public class SignTestController {
    private static RSAPublicKey RsaPublicKey;
    private static RSAPrivateKey RsaPrivateKey;

    @ApiOperation(value = "signTest")
    @GetMapping(value = "/signTest")
    public static void signTest(){
        KeyPairGenerator resourceKeyPairGenerator = null;
        try {
            resourceKeyPairGenerator = KeyPairGenerator.getInstance("RSA"); //NoSuchAlgorithmException 發生狀況大多為JDK版本問題
        } catch (Exception e) {
            e.printStackTrace();
        }
        resourceKeyPairGenerator.initialize(2048); //初始化設bits大小
        KeyPair resourceKeyPair = resourceKeyPairGenerator.generateKeyPair();
        RsaPublicKey = (RSAPublicKey) resourceKeyPair.getPublic();
        RsaPrivateKey = (RSAPrivateKey) resourceKeyPair.getPrivate();

        String json=JsonUtils.toJsonString(new AuthServerUserInfoDTO());//資料轉json格式
        //簽名
        try {
            Signature sign = Signature.getInstance("SHA256withRSA");
            sign.initSign(RsaPrivateKey);
            sign.update(json.getBytes(StandardCharsets.UTF_8));//()內資料先序列化
            byte[] signature=sign.sign();//簽名
            String signStr= Base64.getEncoder().encodeToString(signature);// Base64 encode
            check(json,signStr);//驗證資料與簽名
        }catch(Exception e){
            System.out.println("加密失敗");
        }

    }
    public static void check(String json,String signStr){
        try {
            Signature sign = Signature.getInstance("SHA256withRSA");
            sign.initVerify(RsaPublicKey);
            sign.update(json.getBytes(StandardCharsets.UTF_8));//()內資料先序列化
            if(sign.verify(Base64.getDecoder().decode(signStr))){//驗證送來的資料"obj"與送來的簽名"singStr"Decode後 內容是否相同
                System.out.println("驗證成功");
            }else {
                System.out.println("驗證失敗");
            }
        }catch (SignatureException e){
            System.out.println("驗證失敗");
        }catch (Exception e){
            System.out.println("密鑰格式錯誤");
        }
    }
}
