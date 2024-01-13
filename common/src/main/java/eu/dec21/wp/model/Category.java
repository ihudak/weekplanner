package eu.dec21.wp.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Category implements Model {
    private long id;
    private String name;
    private Integer priority;
    private String color;
    private Long userId;
    private Boolean deleted = false;
}
