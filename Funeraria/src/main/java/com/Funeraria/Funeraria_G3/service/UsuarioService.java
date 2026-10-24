package com.Funeraria.Funeraria_G3.service;

import com.Funeraria.Funeraria_G3.domain.Usuario;
import com.Funeraria.Funeraria_G3.repository.UsuarioRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    // El repositorio es final para asegurar la inmutabilidad
    private final UsuarioRepository usuarioRepository;

    // Colaboradores que pueden ser responsables de un sepelio
    @Transactional(readOnly = true)
    public List<Usuario> getColaboradores() {
        return usuarioRepository.findByRolNombreRolAndEstadoNombreEstado("Personal operativo", "Activo");
    }
}
