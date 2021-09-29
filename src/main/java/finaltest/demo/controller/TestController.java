package finaltest.demo.controller;

import finaltest.demo.dto.*;
import finaltest.demo.utils.JsonUtils;
import finaltest.demo.utils.TimeUtils;
import io.jsonwebtoken.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

@RestController
@RequestMapping("/test")
@Api(tags = "test")
@RequiredArgsConstructor
public class TestController {

    private static Map<String, AuthServerMsgDTO> authRecordMap = new HashMap<>();
    private static RSAPublicKey authRsaPublicKey;
    private static RSAPrivateKey authRsaPrivateKey;
    private static AuthSaveClientInfoDTO asClientInfo = new AuthSaveClientInfoDTO();
    private static AuthServerUserInfoDTO asUserInfo = new AuthServerUserInfoDTO();
    private static String client_password;




    @ApiOperation(value = "1.資源擁有者點擊client端的導向授權方按鈕")
    @GetMapping(value = "/1")
    public ResponseDTO<String> firstStep(String client_id) {
        String authUrl = "http://127.0.0.1:8080/test/2";
        String redirect_uri = "http://127.0.0.1:8080/test/callback";

        return ResponseDTO.<String>createSuccessBuilder()
            .setData(
                authUrl + "?client_id=" + client_id + "&redirect_uri=" + redirect_uri + "&response_type=code&scope=info")
            .build();
    }

    @ApiOperation(value = "2.於授權方輸入帳密取得授權碼")
    @GetMapping(value = "/2")
    public ResponseDTO<Map<String, String>> secStep(
        String client_id,
        String redirect_uri,
        String response_type,
        String scope) {

        if ("code".equals(response_type)) {
            System.out.println("授權碼模式");
        }
        Map<String, String> typeMap = new HashMap<>();//顯示訊息用 不必要的
        Map<String, String> returnMap = new HashMap<>();
        typeMap.put("info", "個人資料");
        typeMap.put("email", "信箱地址");
        typeMap.put("phone", "手機號碼");

        Scanner keyIn = new Scanner(System.in);
        String user = keyIn.next();
        String password = keyIn.next();

        if (null != asUserInfo.getUserInfo().get(user) &&
            asUserInfo.getUserInfo().get(user).get("password").equals(password)) {//登入成功

            System.out.println("登入成功");
            System.out.println("是否同意向" + client_id + "提供" + typeMap.get(scope) + "資訊。");
            String agree = keyIn.next();

            if ("yes".equals(agree)) {

                String authorization_code = UUID.randomUUID().toString();
                returnMap.put("授權碼取得，將導回右側網址",
                    redirect_uri + "?authorization_code=" + authorization_code);//帳號驗證完 導回  redirect_uri+附上授權碼
                authRecordMap
                    .put(client_id, new AuthServerMsgDTO(client_id, scope, authorization_code, user));//授權伺服器儲存此次紀錄

                System.out.println("授權碼發放成功，回到swagger進行操作");
            } else {
                returnMap.put("授權失敗", "請重新嘗試");
            }

        } else {
            returnMap.put("授權失敗", "請重新嘗試");
        }

        return ResponseDTO.<Map<String, String>>createSuccessBuilder()
            .setData(returnMap)
            .build();
    }

    @ApiOperation(value = "3.client打授權碼給授權伺服器，以驗證授權碼取回TOKEN")
    @GetMapping(value = "/3")
    public ResponseDTO<Map<String, String>> thrStep(
        String client_id,
        String client_password,
        String authorization_code) {
        Map<String, String> map = new HashMap<>();
        if ( null != asClientInfo.getClient().get(client_id)//確認client有註冊過
            && asClientInfo.getClient().get(client_id).equals(client_password)//確認client本人
            && null != authRecordMap.get(client_id)
            && authRecordMap.get(client_id).getAuthorization_code().equals(authorization_code))//驗證授權碼
        {
            //授權碼驗證完成取回TOKEN
            map.put("token",
                generateToken(new TokenMessageDTO(authRecordMap.get(client_id).getScope(),//將所需資訊塞進token給予資源伺服器
                    authRecordMap.get(client_id).getUser(),
                    asUserInfo.getUserInfo().get(authRecordMap.get(client_id).getUser()).get("uid")
                )));

        } else {
            map.put("授權碼錯誤", "請聯絡人員了解情形");
        }
        return ResponseDTO.<Map<String, String>>createSuccessBuilder()
            .setData(map)
            .build();

    }

    @ApiOperation(value = "4.client拿TOKEN跟資源伺服器要資源")
    @GetMapping(value = "/4")
    public ResponseDTO<Map<String, String>> fourStep(String token) {
        String scope;
        Map<String, String> map = new HashMap<>();
        Map<String, String> infoMap;
        ResourceServerUserInfoDTO info = new ResourceServerUserInfoDTO();
        try {
            Claims body = getClaim(token).getBody();//驗證TOKEN
            TokenMessageDTO dto = JsonUtils.toObj(body.get("info"), TokenMessageDTO.class);//訊息轉回物件操作
            scope = dto.getDemand();//由TOKEN內訊息取得要拿哪個範圍資料
            infoMap = info.getUser().get(dto.getUid());//由TOKEN內訊息取得要拿誰的資料
        } catch (Exception e) {
            map.put("授權憑證錯誤", "請聯絡人員了解情形");
            return ResponseDTO.<Map<String, String>>createSuccessBuilder()
                .setData(map)
                .build();
        }
        if ("info".equals(scope)) {
            map.put("姓名", infoMap.get("姓名"));
            map.put("電話", infoMap.get("電話"));
            map.put("住址", infoMap.get("住址"));
            map.put("信箱地址", infoMap.get("信箱地址"));
        }
        if ("phone".equals(scope)) {
            map.put("電話", infoMap.get("電話"));
        }
        if ("email".equals(scope)) {
            map.put("信箱地址", infoMap.get("信箱地址"));
        }
        return ResponseDTO.<Map<String, String>>createSuccessBuilder()
            .setData(map)
            .build();
    }

    @ApiOperation(value = "client的callbackApi")
    @GetMapping(value = "/callback")
    public ResponseDTO<String> callback(String authorization_code) {
        String authUrl = "http://127.0.0.1:8080/test/3";
        String client_id = "barrychen";//需更改為註冊的client
        String password = client_password;
        return ResponseDTO.<String>createSuccessBuilder()
            .setData(
                authUrl + "?client_id=" + client_id + "&client_password=" + password + "&authorization_code=" + authorization_code)
            .build();
    }

    @ApiOperation(value = "0-產生密鑰")
    @GetMapping(value = "/pass")
    public ResponseDTO<Void> pass() {
        KeyPairGenerator authKeyPairGenerator = null;
        try {
            authKeyPairGenerator = KeyPairGenerator.getInstance("RSA"); //NoSuchAlgorithmException 發生狀況大多為JDK版本問題
        } catch (Exception e) {
            e.printStackTrace();
        }
        authKeyPairGenerator.initialize(2048); //初始化給予bits長度
        KeyPair authKeyPair = authKeyPairGenerator.generateKeyPair();
        authRsaPublicKey = (RSAPublicKey) authKeyPair.getPublic();//給資源端
        authRsaPrivateKey = (RSAPrivateKey) authKeyPair.getPrivate();//驗證端留著
        return ResponseDTO.createVoidSuccessResponse();
    }

    @ApiOperation(value = "0-client接入前註冊")
    @GetMapping(value = "/register")
    public ResponseDTO<Map<String, String>> register(String client_id) {

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < 8; i++) {//亂數產密
            int z = (int) ((Math.random() * 7) % 3);

            if (z == 1) { // 放數字
                sb.append((int) ((Math.random() * 10) + 48));
            } else if (z == 2) { // 放大寫英文
                sb.append((char) (((Math.random() * 26) + 65)));
            } else {// 放小寫英文
                sb.append(((char) ((Math.random() * 26) + 97)));
            }
        }
        asClientInfo.getClient().put(client_id, sb.toString());//驗證伺服器存Client帳密
        Map<String, String> map = new HashMap<>();
        map.put("client_password", sb.toString());
        client_password = sb.toString();
        return ResponseDTO.<Map<String, String>>createSuccessBuilder()
            .setData(map)
            .build();
    }
    // JWT產生方法
    public static String generateToken(
        TokenMessageDTO dto
    ) {
        // 生成JWT
        return Jwts.builder()
            .setHeaderParam("typ", "JWT")
            // 在Payload放入自定義的聲明方法如下
            .claim("info", dto)
            // 在Payload放入exp保留聲明
            .setExpiration(TimeUtils.toDate(OffsetDateTime.now().plusMinutes(3)))
            .signWith(SignatureAlgorithm.RS256, authRsaPrivateKey).compact();
    }
    //驗證JWT Token並取回jwt
    public static Jws<Claims> getClaim(String token) {
        Jws<Claims> jwt;
        try {
            jwt = Jwts.parser()
                .setSigningKey(authRsaPublicKey)
                .parseClaimsJws(token);
        } catch (SignatureException e) {
            throw new RuntimeException("授權憑證錯誤");
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("授權憑證過期，請重新申請");
        }
        return jwt;
    }

}
