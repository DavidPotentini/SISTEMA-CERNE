package com.github.davidpotentini.cerne2.models.canvas.ids;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.*;

import java.io.Serializable;

@Embeddable
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ChannelImplementationCanvasId implements Serializable {

    @Column(name = "SEG_COD")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long segCod;

    private Long ambcCod;


}
