
package com.memoriales.service;

import com.memoriales.domain.Servicio;
import com.memoriales.repository.ServicioRepository; 
import org.springframework.beans.factory.annotation.Autowired; 
import org.springframework.stereotype.Service; 
import org.springframework.transaction.annotation.Transactional;
import java.util.List;


@Service
public class ServicioFunerarioService {
    
    @Autowired
    private ServicioRepository servicioRepository;
    
    @Transactional(readOnly = true)
    public List<Servicio> getServicios(){
        return servicioRepository.findAll();
    }
    
    @Transactional
    public void asociarServicio(Servicio servicio) {
        servicioRepository.save(servicio);
    }
    
}
