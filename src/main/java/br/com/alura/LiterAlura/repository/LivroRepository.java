package br.com.alura.LiterAlura.repository;

import br.com.alura.LiterAlura.model.Livro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LivroRepository extends JpaRepository<Livro, Long> {

    Optional<Livro> findByTitulo(String titulo);

    @Query("SELECT DISTINCT l FROM Livro l JOIN FETCH l.autores")
    List<Livro> buscarTodosComAutores();

    @Query("SELECT DISTINCT l FROM Livro l JOIN FETCH l.autores")
    List<Livro> buscarTodosComAutoresELinguagem();

}