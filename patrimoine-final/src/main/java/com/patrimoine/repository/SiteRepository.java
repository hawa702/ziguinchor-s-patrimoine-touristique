package com.patrimoine.repository;

import com.patrimoine.model.Site;
import com.patrimoine.model.TypeSite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SiteRepository extends JpaRepository<Site, Long> {

    List<Site> findByVilleContainingIgnoreCase(String ville);
    List<Site> findByType(TypeSite type);

    @Query("SELECT s FROM Site s WHERE " +
           "(:ville IS NULL OR LOWER(s.ville) LIKE LOWER(CONCAT('%', :ville, '%'))) AND " +
           "(:type IS NULL OR s.type = :type)")
    List<Site> rechercher(@Param("ville") String ville, @Param("type") TypeSite type);
}
