package finaltest.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data

public class UserInfoDTO {
    @JsonProperty(value = "user")
    @ApiModelProperty(value = "帳號")
    private Map<String,String> user;

    public UserInfoDTO(){

        Map<String,String> map =new HashMap<>();
        map.put("admin","123");
        map.put("nobody","321");
        map.put("barry","624");

        this.user=map;
    }

}
