package models.swagger;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Requirements {

    @JsonProperty("hardDrive")
    public Integer hardDrive;

    @JsonProperty("osName")
    public String osName;

    @JsonProperty("ramGb")
    public Integer ramGb;

    @JsonProperty("videoCard")
    public String videoCard;

}
