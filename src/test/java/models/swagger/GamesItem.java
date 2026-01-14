package models.swagger;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GamesItem {
    @JsonProperty("company")
    public String company;

    @JsonProperty("description")
    public String description;

    @JsonProperty("dlcs")
    public List<DlcItem> dlcsItem;

    @JsonProperty("gameId")
    public Integer gameId;

    @JsonProperty("genre")
    public String genre;

    @JsonProperty("isFree")
    public Boolean isFree;

    @JsonProperty("price")
    public Integer price;

    @JsonProperty("publish_date")
    public String publishDate;

    @JsonProperty("rating")
    public Integer rating;

    @JsonProperty("requiredAge")
    public Boolean requiredAge;

    @JsonProperty("requirements")
    public Requirements requirements;

    @JsonProperty("tags")
    public List<String> tags;

    @JsonProperty("title")
    public String title;
}
