package models;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Cat {

    private String name;
    private String model;
    private Integer age;


}
