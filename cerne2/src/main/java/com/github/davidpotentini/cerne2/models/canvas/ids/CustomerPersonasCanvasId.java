package com.github.davidpotentini.cerne2.models.canvas.ids;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Embeddable
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CustomerPersonasCanvasId implements Serializable {

    @Column(name = "PERSONA_COD")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long personaCod;

    private Long ambcCod;
}
