package finaltest.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data

public class AuthServerUserInfoDTO {
    @JsonProperty(value = "userInfo")
    @ApiModelProperty(value = "帳戶資訊")
    private Map<String,Map<String,String>>  userInfo;

    public AuthServerUserInfoDTO(){

        Map<String,Map<String,String>> bigMap =new HashMap<>();
        for(int i=0;i<3;i++){
            if(i==0){
                Map<String,String> map =new HashMap<>();
                map.put("password", "123");
                map.put("uid", "u1548327");
                bigMap.put("barry",map);
            }
            if(i==1){
                Map<String,String> map =new HashMap<>();
                map.put("password", "321");
                map.put("uid", "u1875755");
                bigMap.put("tarry",map);
            }
            if(i==2){
                Map<String,String> map =new HashMap<>();
                map.put("password", "624");
                map.put("uid", "u1876455");
                bigMap.put("jerry",map);
            }
        }
        this.userInfo=bigMap;
    }

}
