package eu.dec21.wp.categories.entity;

import eu.dec21.wp.helper.Constraints;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "categories", uniqueConstraints = { @UniqueConstraint(columnNames = { "user_id", "name" } ) } )
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 25)
    @NonNull
    private String name;

    @Column(name = "priority", nullable = false)
    @NonNull
    @Min(Constraints.minPrio)
    @Max(Constraints.maxPrio)
    private Integer priority;

    @Column(name = "color", nullable = true, length = 255)
    private String color;

    @Column(name = "user_id", nullable = false)
    @NonNull
    private Long userId;

    public void setPriority(int priority) {
        if (priority > Constraints.maxPrio) {
            priority = Constraints.maxPrio;
        } else if (priority < Constraints.minPrio) {
            priority = Constraints.minPrio;
        }
        this.priority = priority;
    }
}
