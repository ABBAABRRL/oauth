package finaltest.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class ResourceServerUserInfoDTO {
    @JsonProperty(value = "user")
    @ApiModelProperty(value = "帳號")
    private Map<String,Map<String,String>> user;

    public ResourceServerUserInfoDTO(){

        Map<String,Map<String,String>>  bigMap=new HashMap<>();

        for(int i=0;i<3;i++){
            if(i==0){
                Map<String,String> map =new HashMap<>();
                map.put("姓名", "Barry");
                map.put("電話", "0912345678");
                map.put("住址", "新竹市東區公道五路二段158號");
                map.put("信箱地址", "barry.chen@tpisoftware.com");
                bigMap.put("u1548327",map);
            }
            if(i==1){
                Map<String,String> map =new HashMap<>();
                map.put("姓名", "Tarry");
                map.put("電話", "0912345588");
                map.put("住址", "新竹市東區公道五路二段100號");
                map.put("信箱地址", "Tarry.chen@tpisoftware.com");
                bigMap.put("u1876455",map);
            }
            if(i==2){
                Map<String,String> map =new HashMap<>();
                map.put("姓名", "Jerry");
                map.put("電話", "0912348756");
                map.put("住址", "新竹市東區公道五路二段188號");
                map.put("信箱地址", "Jerry.chen@tpisoftware.com");
                bigMap.put("u1875755",map);
            }
        }
        this.user=bigMap;
    }
}
