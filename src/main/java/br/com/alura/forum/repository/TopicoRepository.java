package br.com.alura.forum.repository;

import br.com.alura.forum.controller.dto.TopicoDto;
import br.com.alura.forum.modelo.Topico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TopicoRepository extends JpaRepository<Topico, Long> {

    // Topico possui um Curso
    // Acessamos o Curso no atributo Nome, Caso dentro de topico exista um campo cursoNome teremos problema
    // Para corrigir sera necessário alterar o método para findByCurso_Nome
    // Dessa forma buscara dentro da entidade Curso que esta relacionada ao Tópico
    List<Topico> findByCursoNome(String nomeCurso);

    @Query("SELECT t FROM Topico t WHERE t.curso.nome = :nomeCurso")
    List<Topico> carregarPorNomeDoCurso(@Param("nomeCurso") String nomeCurso);
}
