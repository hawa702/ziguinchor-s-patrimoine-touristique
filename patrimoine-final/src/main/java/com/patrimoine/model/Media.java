package com.patrimoine.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Media (id, url, type [IMAGE/VIDEO], site_id)
 */
@Entity
@Table(name = "medias")
@Data
@NoArgsConstructor
public class Media {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Chemin/URL relatif du fichier stocke, ex: /uploads/xyz.jpg */
    private String url;

    @Enumerated(EnumType.STRING)
    private TypeMedia type;

    @ManyToOne
    @JoinColumn(name = "site_id")
    private Site site;
}
