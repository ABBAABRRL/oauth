package finaltest.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class AuthSaveClientInfoDTO {


    @JsonProperty(value = "client")
    @ApiModelProperty(value = "帳號")
    private Map<String,String> client;

    public AuthSaveClientInfoDTO(){

        Map<String,String> map =new HashMap<>();
        map.put("tpi","123");
        map.put("soft","321");
        map.put("ware","624");

        this.client=map;
    }
}
