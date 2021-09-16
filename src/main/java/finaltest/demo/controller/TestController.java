package finaltest.demo.controller;

import finaltest.demo.dto.ResponseDTO;
import finaltest.demo.utils.TimeUtils;
import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.crypto.MacProvider;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

@RestController
@RequestMapping("/test")
@Api(tags = "test")
@RequiredArgsConstructor
public class TestController {

    private final String dbUser="Admin";
    private final String dbPassWord="123";
    private final String authorization_code="5246";
    private final String token="TOKEN";
    private static RSAPublicKey rsaPublicKey ;
    private static RSAPrivateKey rsaPrivateKey ;
    private  String client_id;
    private  final String client_password="IamClient";
    private static final Key HASH_SALT = MacProvider.generateKey();

    @ApiOperation(value = "1.資源擁有者點擊client端的導向授權方按鈕")
    @GetMapping(value = "/1")
    public ResponseDTO<String> firstStep() {
        //        client打第三方api

        return ResponseDTO.<String>createSuccessBuilder()
            .setData("即將前往授權方")
            .build();

    }

    @ApiOperation(value = "2.於授權方輸入帳密取得授權碼")
    @GetMapping(value = "/2")
    public ResponseDTO<Map<String,String>> secStep(String client_id,String redirect_uri,String response_type,String scope) {
        Map<String,String> typeMap=new HashMap<>();
        typeMap.put("info","個人資訊");
        typeMap.put("image","照片");
        typeMap.put("phone","手機");


        Scanner keyIn = new Scanner(System.in);
        String user=keyIn.next();
        String password=keyIn.next();
        this.client_id=client_id;

        if(dbUser.equals(user)&&dbPassWord.equals(password)){//登入成功
            System.out.println("登入成功");

            System.out.println("是否同意向"+client_id+"提供"+typeMap.get(scope)+"資訊。");
            String agreen=keyIn.next();
            if("yes".equals(agreen)) {
                System.out.println("將授權碼附上並導回"+redirect_uri);//帳號驗證完 導回  redirect_uri+授權碼
            }

        }else {
            System.out.println("119");
        }
        Map<String,String> map=new HashMap<>();
        map.put("授權碼",authorization_code+"&"+typeMap.get(scope));

        return ResponseDTO.<Map<String,String>>createSuccessBuilder()
            .setData(map)
            .build();
    }
    @ApiOperation(value = "3.client打授權碼給授權伺服器，以驗證授權碼取回TOKEN")
    @GetMapping(value = "/3")
    public ResponseDTO<Map<String,String>> thrStep(String input_client_id,String client_password,String authorization_code) {
        //        client打第三方api

        String[] data =authorization_code.split("&");
        String code=data[0];
        String demand=data[1];

        if( this.client_id.equals(input_client_id)//確認授權碼有沒有發給這個client過
            &&this.client_password.equals(client_password)//確認client本人
            && this.authorization_code.equals(code))//驗證授權碼
        {
            //授權碼驗證完成取回TOKEN
            System.out.println("你是合格持有授權碼的勇者，這個TOKEN就交給你了");
        }else {
            System.out.println("119");
        }
        //帳號ok 導回  redirect_uri+授權碼
        //拿授權碼驗證 ->返回TOKEN ->TOKEN拿資源->返回資源
        Map<String,String> map=new HashMap<>();
        map.put("token",generateToken(demand));
        return ResponseDTO.<Map<String,String>>createSuccessBuilder()
            .setData(map)
            .build();

    }

    @ApiOperation(value = "4.client拿TOKEN跟資源伺服器要資源")
    @GetMapping(value = "/4")
    public ResponseDTO<Map<String,String>> fourStep(String token) {
        //        client打第三方api
        Claims body=getClaim(token).getBody();

        System.out.println(body.get("demand"));

        Map<String,String> map=new HashMap<>();
        map.put("姓名","Barry");
        map.put("電話","0912345678");
        map.put("住址","新竹市東區公道五路二段158號");
        //帳號ok 導回  redirect_uri+授權碼
        //拿授權碼驗證 ->返回TOKEN ->TOKEN拿資源->返回資源
        return ResponseDTO.<Map<String,String>>createSuccessBuilder()
            .setData(map)
            .build();
    }
    @ApiOperation(value = "產生密鑰")
    @GetMapping(value = "/pass")
    public ResponseDTO<Void> pass() {
        KeyPairGenerator keyPairGenerator = null;
        try {
            keyPairGenerator = KeyPairGenerator.getInstance("RSA"); //NoSuchAlgorithmException
        } catch (Exception e) {
            e.printStackTrace();
        }
        keyPairGenerator.initialize(2048); // 此處可以新增引數new SecureRandom(UUID.randomUUID().toString().getBytes())
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
         rsaPublicKey = (RSAPublicKey)keyPair.getPublic();//給驗證端
        rsaPrivateKey = (RSAPrivateKey)keyPair.getPrivate();//資源端留著


        return ResponseDTO.createVoidSuccessResponse();
    }

    // JWT產生方法
    public static String generateToken(String demand) {
        // 生成JWT

        return Jwts.builder()
            // 在Payload放入自定義的聲明方法如下
            .claim("demand", demand)
            .claim("userName", "barry")
            .claim("uid","u1548327")
            // 在Payload放入exp保留聲明
            .setExpiration(TimeUtils.toDate(OffsetDateTime.now().plusMinutes(30)))
            .signWith(SignatureAlgorithm.RS256, rsaPrivateKey).compact();
    }
    public static Jws<Claims> getClaim(String token) {
        Jws<Claims> jwt;
        try {
            jwt = Jwts.parser()
                .setSigningKey(rsaPublicKey)
                .parseClaimsJws(token);
            System.out.println("謝謝你年輕人，這輩子居然還能看到真正的TOKEN，這是給你的姓名、電話號碼、住址");
        } catch (SignatureException e) {
            throw new RuntimeException("授權憑證錯誤");
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("授權憑證過期，請重新申請");
        }
        return jwt;
    }
}
