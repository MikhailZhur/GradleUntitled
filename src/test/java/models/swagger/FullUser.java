package models.swagger;

import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FullUser {

    @JsonProperty("games")
    public List<GamesItem> gamesItem;

    @JsonProperty("login")
    public String login;

    @JsonProperty("pass")
    public String pass;

}
