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
public class DlcItem {
    @JsonProperty("description")
    public String description;

    @JsonProperty("dlcName")
    public String dlcName;

    @JsonProperty("isDlcFree")
    public Boolean isDlcFree;

    @JsonProperty("price")
    public Integer price;

    @JsonProperty("rating")
    public Integer rating;

    @JsonProperty("similarDlc")
    public SimilarDlc similarDlc;

}
