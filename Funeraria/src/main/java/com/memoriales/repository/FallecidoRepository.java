package com.memoriales.repository;

import com.memoriales.domain.Fallecidos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FallecidoRepository extends JpaRepository <Fallecidos,String>{
    List<Fallecidos> findByCedulaCliente(String cedulaCliente);
}