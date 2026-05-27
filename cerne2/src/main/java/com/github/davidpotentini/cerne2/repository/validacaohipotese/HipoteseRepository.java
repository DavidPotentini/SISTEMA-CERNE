package com.github.davidpotentini.cerne2.repository.validacaohipotese;

import com.github.davidpotentini.cerne2.models.validacaohipotese.HipoteseModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HipoteseRepository extends JpaRepository<HipoteseModel, Long> {

    @Query(value = "SELECT * FROM HIPOTESE WHERE QVH_COD = :QVH_COD", nativeQuery = true)
    List<HipoteseModel> findHipoteseByQvhCod(@Param("QVH_COD") Long qvhCod);

    @Modifying
    @Query(value = "DELETE FROM HIPOTESE WHERE QVH_COD = :QVH_COD", nativeQuery = true)
    void deleteHipoteseByQvhCod(@Param("QVH_COD") Long qvhCod);
}
