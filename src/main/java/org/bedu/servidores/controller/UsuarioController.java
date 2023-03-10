package org.bedu.servidores.controller;

import jakarta.validation.Valid;
import org.bedu.servidores.model.Usuario;
import org.bedu.servidores.repos.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
public class UsuarioController {
    @Autowired
    private UsuarioRepository usuarioRepository;

    //Consultar todos los usuarios
    @GetMapping("/usuarios")
    public ResponseEntity<List<Usuario>> consultarUsuarios(){
        List<Usuario> usuarios = usuarioRepository.findAll();
        return ResponseEntity.ok(usuarios);
    }

    //Consultar un usuario por id
    @GetMapping("/usuario/{id}")
    public ResponseEntity<Usuario> consultarUsuario(@PathVariable Long id){
        Usuario usu = usuarioRepository.findById(id).orElseGet(()->null);
        if(usu == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"El usuario no existe");
        return ResponseEntity.ok(usu);
    }

    @GetMapping("/usuario/correo/{correo}")
    public ResponseEntity<Usuario> consultaRol(@PathVariable String correo){
        Usuario usu = usuarioRepository.findOneByCorreo(correo).orElseGet(()->null);
        if(usu == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"El usuario no existe");
        return ResponseEntity.ok(usu);
    }

    //Crear un nuevo usuario
    @PostMapping("/usuario")
    public ResponseEntity<Usuario> crearUsuario(@Valid @RequestBody Usuario usuario){
        Usuario usu = usuarioRepository.save(usuario);
        return ResponseEntity.ok(usu);
    }

    //Modificar un usuario
    @PutMapping("/usuario/{id}")
    public ResponseEntity<Usuario> modificarUsuario(@PathVariable Long id,
                                                        @Valid @RequestBody Usuario usuario ){
        Usuario usu = usuarioRepository.findById(id).orElseGet(()->null);
        if(usu == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"El usuario no existe");
        usu.setCorreo(usuario.getCorreo());
        usu.setNombre(usuario.getNombre());
        usu.setRol(usuario.getRol());
        usu.setPassword(usuario.getPassword());
        usu = usuarioRepository.save(usu);
        return ResponseEntity.ok(usu);
    }

    //Eliminar un usuario
    @DeleteMapping("/usuario/{id}")
    public ResponseEntity<Long> borrarUsuario(@PathVariable Long id){
        Usuario usu = usuarioRepository.findById(id).orElseGet(() -> null);

        if(usu == null )
            return ResponseEntity.notFound().build();
        else if(usu != null && usu.getCredenciales().size() > 0)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"El usuario tiene credenciales asignadas");
        else
            usuarioRepository.deleteById(id);

        return new ResponseEntity<>(id, HttpStatus.OK);
    }
}
