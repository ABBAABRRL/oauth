package finaltest.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data

public class AuthServerDTO {
    @JsonProperty(value = "authorization_code")
    @ApiModelProperty(value = "授權碼")
    private String authorization_code;
    @JsonProperty(value = "client_id")
    @ApiModelProperty(value = "用戶端ID")
    private String client_id;
    @JsonProperty(value = "scope")
    @ApiModelProperty(value = "授權範圍")
    private String scope;
    public AuthServerDTO(){}

    public AuthServerDTO( String client_id,String scope,String authorization_code){
        this.authorization_code=authorization_code;
        this.client_id=client_id;
        this.scope=scope;
    }
}
