package com.Funeraria.Funeraria_G3.repository;

import com.Funeraria.Funeraria_G3.domain.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Integer> {

}
