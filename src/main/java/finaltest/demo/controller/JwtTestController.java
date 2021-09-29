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
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/jwt")
@Api(tags = "jwt")
@RequiredArgsConstructor
public class JwtTestController {
    private static RSAPublicKey RsaPublicKey;
    private static RSAPrivateKey RsaPrivateKey;

    @ApiOperation(value = "jwtTest")
    @GetMapping(value = "/jwtTest")
    public static void jwtTest(){
        KeyPairGenerator resourceKeyPairGenerator = null;
        try {
            resourceKeyPairGenerator = KeyPairGenerator.getInstance("RSA"); //NoSuchAlgorithmException 發生狀況大多為JDK版本問題
        } catch (Exception e) {
            e.printStackTrace();
        }
        resourceKeyPairGenerator.initialize(2048); //資源端初始化
        KeyPair resourceKeyPair = resourceKeyPairGenerator.generateKeyPair();
        RsaPublicKey = (RSAPublicKey) resourceKeyPair.getPublic();//給驗證端
        RsaPrivateKey = (RSAPrivateKey) resourceKeyPair.getPrivate();//資源端留著
        String signStr="";
        AuthServerUserInfoDTO dto = new AuthServerUserInfoDTO();
        //簽名
        try {
            Signature sign = Signature.getInstance("SHA256withRSA");
            sign.initSign(RsaPrivateKey);
            sign.update(JsonUtils.toJsonString(dto).getBytes(StandardCharsets.UTF_8));
            byte[] signature=sign.sign();
            signStr= Base64.getEncoder().encodeToString(signature);//簽名
            check(dto,signStr);//驗證資料與簽名
        }catch(Exception e){
            System.out.println("加密失敗");
        }

    }
    public static void check(Object obj,String signStr){
        try {
            Signature sign = Signature.getInstance("SHA256withRSA");
            sign.initVerify(RsaPublicKey);
            sign.update(JsonUtils.toJsonString(obj).getBytes(StandardCharsets.UTF_8));
            if(!sign.verify(Base64.getDecoder().decode(signStr))){//驗證送來的資料"obj"與送來的簽名"singStr"解密後內容是否相同
                System.out.println("驗證失敗");
            }
        }catch (SignatureException e){
            System.out.println("驗證失敗");
        }catch (Exception e){
            System.out.println("密鑰格式錯誤");
        }
    }
}
