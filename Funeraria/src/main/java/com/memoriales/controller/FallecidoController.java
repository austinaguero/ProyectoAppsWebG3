package com.memoriales.controller;

import com.memoriales.domain.Fallecidos; 
import com.memoriales.domain.Servicio; 
import com.memoriales.service.FallecidoService; 
import com.memoriales.service.ServicioFunerarioService; 
import org.springframework.beans.factory.annotation.Autowired; 
import org.springframework.stereotype.Controller; 
import org.springframework.ui.Model; 
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/fallecido")
public class FallecidoController {
   
   @Autowired 
   private FallecidoService fallecidosService; 
   
   
   @Autowired 
   private ServicioFunerarioService servicioFunerarioService;
   
   @GetMapping("/listado") public String listado(Model model) { 
        var fallecidos = fallecidosService.getFallecidos(); 
        var servicios = servicioFunerarioService.getServicios();
        
        model.addAttribute("fallecidos", fallecidos);
        model.addAttribute("servicios", servicios); 
        model.addAttribute("totalFallecidos", fallecidos.size());
        
        model.addAttribute("fallecido", new Fallecidos());
        model.addAttribute("servicio", new Servicio());
        
        return "/fallecido/listado";      
   }
    
    @PostMapping("/guardar")
    public String guardar(Fallecidos fallecido) {
        fallecidosService.guardar(fallecido); 
        return "redirect:/fallecido/listado";
    }
    
    @PostMapping("/asociar-servicio")
    public String asociarServicio(Servicio servicio) {
        servicioFunerarioService.asociarServicio(servicio); 
        return "redirect:/fallecido/listado";
    }
}
