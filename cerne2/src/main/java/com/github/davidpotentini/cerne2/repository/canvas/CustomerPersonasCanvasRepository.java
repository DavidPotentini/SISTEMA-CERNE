package com.github.davidpotentini.cerne2.repository.canvas;

import com.github.davidpotentini.cerne2.models.canvas.CustomerPersonasCanvasModel;
import com.github.davidpotentini.cerne2.models.canvas.ids.CustomerPersonasCanvasId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CustomerPersonasCanvasRepository extends JpaRepository<CustomerPersonasCanvasModel, Long> {

    @Query(value = "SELECT * FROM CUSTOMER_PERSONAS_CANVAS WHERE AMBC_COD = :AMBC_COD", nativeQuery = true)
    List<CustomerPersonasCanvasModel> findCustomerPersonasByAmbcCod(@Param("AMBC_COD") Long ambcCod);
}
