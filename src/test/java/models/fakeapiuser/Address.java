package models.fakeapiuser;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {
    @JsonProperty("city")
    private String city;

    @JsonProperty("street")
    private String street;

    @JsonProperty("number")
    private Integer number;

    @JsonProperty("zipcode")
    private String zipCode;

    @JsonProperty("Geolocation")
    private Geolocation geolocation;

}
