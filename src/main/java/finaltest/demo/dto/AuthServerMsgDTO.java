package finaltest.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AuthServerMsgDTO {
    @JsonProperty(value = "authorization_code")
    @ApiModelProperty(value = "授權碼")
    private String authorization_code;
    @JsonProperty(value = "client_id")
    @ApiModelProperty(value = "用戶端ID")
    private String client_id;
    @JsonProperty(value = "scope")
    @ApiModelProperty(value = "授權範圍")
    private String scope;
    @JsonProperty(value = "user")
    @ApiModelProperty(value = "持有者id")
    private String user;
    public AuthServerMsgDTO( String client_id,String scope,String authorization_code,String user){
        this.authorization_code=authorization_code;
        this.client_id=client_id;
        this.scope=scope;
        this.user=user;
    }
}
