package br.com.alura.LiterAlura.dto;

import java.util.List;

public record LivroDTO(String titulo, List<String> autores, List<String> linguagens) { }

