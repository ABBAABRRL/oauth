package finaltest.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ClientDTO {
    @JsonProperty(value = "county")
    @ApiModelProperty(value = "縣市")
    private String county;
    @JsonProperty(value = "town")
    @ApiModelProperty(value = "鄉鎮")
    private String town;
    @JsonProperty(value = "temp")
    @ApiModelProperty(value = "溫度")
    private int temp;
    @JsonProperty(value = "rh")
    @ApiModelProperty(value = "相對溼度")
    private int rh;
}
