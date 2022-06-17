package br.com.alura.forum.controller;

import br.com.alura.forum.controller.dto.TopicoDto;
import br.com.alura.forum.modelo.Topico;
import br.com.alura.forum.repository.TopicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TopicosController {

    @Autowired
    TopicoRepository topicoRepository;

    @RequestMapping("topicos")
    // localhost:topicos?nomeCurso=Spring+Boot
    public List<TopicoDto> lista(String nomeCurso) {
        List<Topico> topicos;

        if (nomeCurso == null) {
            topicos = topicoRepository.findAll();
        } else {
           topicos = topicoRepository.findByCursoNome(nomeCurso);
        }

        return TopicoDto.converter(topicos);
    }

}
