
package com.memoriales.service;

import com.memoriales.domain.Fallecidos;
import com.memoriales.repository.FallecidoRepository;
import org.springframework.beans.factory.annotation.Autowired; 
import org.springframework.stereotype.Service; 
import org.springframework.transaction.annotation.Transactional; 
import java.util.List;


@Service
public class FallecidoService {
    
    
    @Autowired
private FallecidoRepository fallecidoRepository;

    @Transactional(readOnly = true)
    public List <Fallecidos> getFallecidos(){
        return fallecidoRepository.findAll();
    }
    @Transactional(readOnly = true)
        public Fallecidos getFallecido(String cedulaFallecido) {
           return fallecidoRepository.findById(cedulaFallecido).orElse(null); 
    }
     
    @Transactional
    public void guardar(Fallecidos fallecido) {
        fallecidoRepository.save(fallecido);
    }
    
    @Transactional
    public void eliminar(Fallecidos fallecido) {
        fallecidoRepository.delete(fallecido);
    }    
}
