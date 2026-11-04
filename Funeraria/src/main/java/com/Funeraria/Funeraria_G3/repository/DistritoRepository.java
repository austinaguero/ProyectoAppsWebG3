package com.Funeraria.Funeraria_G3.repository;

import com.Funeraria.Funeraria_G3.domain.Distrito;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DistritoRepository extends JpaRepository<Distrito, Integer> {

    public List<Distrito> findAllByOrderByIdDistritoAsc();

}
