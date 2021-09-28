package finaltest.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenMessageDTO {
    @JsonProperty(value = "demand")
    @ApiModelProperty(value = "demand")
    private String demand;
    @JsonProperty(value = "userName")
    @ApiModelProperty(value = "userName")
    private String userName;
    @JsonProperty(value = "uid")
    @ApiModelProperty(value = "授權範圍")
    private String uid;


}
