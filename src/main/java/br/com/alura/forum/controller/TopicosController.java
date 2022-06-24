package br.com.alura.forum.controller;

import br.com.alura.forum.controller.dto.DetalhesDoTopicoDto;
import br.com.alura.forum.controller.dto.TopicoDto;
import br.com.alura.forum.controller.form.AtualizacaoTopicoForm;
import br.com.alura.forum.controller.form.TopicoForm;
import br.com.alura.forum.modelo.Topico;
import br.com.alura.forum.repository.CursoRepository;
import br.com.alura.forum.repository.TopicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/topicos")
public class TopicosController {

    @Autowired
    TopicoRepository topicoRepository;
    @Autowired
    CursoRepository cursoRepository;

    /*
        @GetMapping
        // localhost:topicos?nomeCurso=Spring+Boot
        // RequestParam indica que variável virá pela Url, e nomeCurso não sera obrigatorio
        // http://localhost:8080/topicos/?pagina=0&quantidade=3&ordenacao=id
        public Page<TopicoDto> lista(@RequestParam(required = false) String nomeCurso,
                                     @RequestParam int pagina,
                                     @RequestParam int quantidade,
                                     @RequestParam String ordenacao
        ) {

            Pageable paginacao = PageRequest.of(pagina, quantidade, Sort.Direction.ASC, ordenacao);

            Page<Topico> topicos;
            if (nomeCurso == null) {
                topicos = topicoRepository.findAll(paginacao);
            } else {
                topicos = topicoRepository.findByCursoNome(nomeCurso, paginacao);
            }
            return TopicoDto.converter(topicos);
        }
     */

    // localhost:topicos?nomeCurso=Spring+Boot
    // Adicionamos a anotação @EnableSpringDataWebSupport na classe main para permitir o spring pegar da requisição
    // e Repassar ao SpringData.
    // Novo Padrão http://localhost:8080/topicos/?page=0&size=10&ordenacao=id,asc
    @GetMapping
    @Cacheable(value = "listaDeTopicos") // funciona como o Id do cache
    public Page<TopicoDto> lista(
            @RequestParam(required = false) String nomeCurso,
            @PageableDefault(sort = "id", direction = Sort.Direction.DESC, page = 0, size = 10) Pageable paginacao) {
        Page<Topico> topicos;
        if (nomeCurso == null) {
            topicos = topicoRepository.findAll(paginacao);
        } else {
            topicos = topicoRepository.findByCursoNome(nomeCurso, paginacao);
        }
        return TopicoDto.converter(topicos);
    }

    @PostMapping
    @Transactional
    @CacheEvict(value = "listaDeTopicos", allEntries = true) // Vai limpar o cache ao cadastrar novo recurso
    public ResponseEntity<TopicoDto> cadastrar(@RequestBody @Valid TopicoForm form, UriComponentsBuilder uriBuilder) {
        Topico topico = form.converter(cursoRepository);
        topicoRepository.save(topico);

        // Devolvemos o cabeçalho + o Corpo com status 201
        URI uri = uriBuilder.path("/topicos/{id}").buildAndExpand(topico.getId()).toUri();
        return ResponseEntity.created(uri).body(new TopicoDto(topico));
    }

    @GetMapping("/{id}")
    // @PathVariable indica que o Id vira junto na url
    public ResponseEntity<DetalhesDoTopicoDto> detalhar(@PathVariable Long id) {
        Optional<Topico> topico = topicoRepository.findById(id);
        return topico
                .map(value -> ResponseEntity.ok(
                        new DetalhesDoTopicoDto(value))
                ).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @Transactional
    @CacheEvict(value = "listaDeTopicos", allEntries = true) // Vai limpar o cache ao atualizar recurso
    public ResponseEntity<TopicoDto> atualizar(@PathVariable Long id, @RequestBody @Valid AtualizacaoTopicoForm form) {
        Optional<Topico> topico = topicoRepository.findById(id);

        if (topico.isPresent()) {
            Topico topicoAtualizado = form.atualizar(topico.get());
            Topico topicoPersitido = topicoRepository.save(topicoAtualizado);
            return ResponseEntity.ok(new TopicoDto(topicoPersitido));
        }

        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @Transactional
    @CacheEvict(value = "listaDeTopicos", allEntries = true) // Vai limpar o cache ao deletar recurso
    public ResponseEntity<?> remover(@PathVariable Long id) {
        Optional<Topico> topico = topicoRepository.findById(id);

        if (topico.isPresent()) {
            topicoRepository.deleteById(topico.get().getId());
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

}
