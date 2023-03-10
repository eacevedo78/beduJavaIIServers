package org.bedu.servidores.controller;

import org.bedu.servidores.model.Servidor;
import org.bedu.servidores.repos.ServidorRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Optional;


@WebMvcTest(ServidorController.class)
@AutoConfigureMockMvc(addFilters = false)
class ServidorControllerTest {
    @MockBean
    private ServidorRepository sr;
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void consultarServidores() throws Exception{
        mockMvc.perform(get("/servidores"))
                .andExpect(status().isOk());
    }

    @Test
    void consultarServidor() throws Exception {
        long id = 1L;
        Servidor s = new Servidor();
        s.setId(id);
        s.setNombre("Servidor de prueba");
        s.setDescripcion("Este es un servidor de desarrollo");
        s.setIp("5.5.5.5");
        when(sr.findById(id)).thenReturn(Optional.of(s));
        mockMvc.perform(get("/servidor/{id}", id)).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.nombre").value(s.getNombre()))
                .andDo(print());
    }

    @Test
    void crearServidor() throws Exception {
        Servidor s = new Servidor();
        s.setNombre("Servidor de prueba");
        s.setDescripcion("Este es un servidor de desarrollo");
        s.setIp("5.5.5.5");
        mockMvc.perform(post("/servidor").contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization","Bearer eyJhbGciOiJIUzI1NiJ9.eyJub21icmUiOiJlbW1hbnVlbEBnbWFpbC5jb20iLCJyb2wiOiJBRE1JTiJ9.ZMulk2x0yIxOQrGv-__0ucWC6wzxR8-pXb0wlAKwAL4")
                        .content(objectMapper.writeValueAsString(s)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void actualzarServidor() throws Exception {
        long id = 1L;
        Servidor s = new Servidor();
        s.setNombre("Servidor de prueba");
        s.setDescripcion("Este es un servidor de desarrollo");
        s.setIp("5.5.5.5");
        when(sr.findById(id)).thenReturn(Optional.of(s));
        mockMvc.perform(put("/servidor/1").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(s)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void eliminarServidor() throws Exception {
        long id = 1L;
        Servidor s = new Servidor();
        s.setId(id);
        s.setNombre("Servidor de prueba");
        s.setDescripcion("Este es un servidor de desarrollo");
        s.setIp("5.5.5.5");
        //when(sr.findById(id)).thenReturn(Optional.of(s));
        doNothing().when(sr).deleteById(id);
        mockMvc.perform(delete("/servidor/{id}", id).header("Authorization","Bearer eyJhbGciOiJIUzI1NiJ9.eyJub21icmUiOiJlZGlhekBnbWFpbC5jb20iLCJyb2wiOiJBRE0ifQ.5qZp1oMIiH4AJtod2K8ClOnGpzvaSptFe-deiZ1NGWI"))
                .andExpect(status().isNotFound())
                .andDo(print());
        /*mockMvc.perform(get("/servidor/{id}", id))
                .andExpect(status().isNotFound())
                .andDo(print());*/
    }
}