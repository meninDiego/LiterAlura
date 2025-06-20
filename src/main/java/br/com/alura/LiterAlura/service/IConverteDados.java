package br.com.alura.LiterAlura.service;

import org.springframework.data.jpa.repository.JpaRepository;

public interface IConverteDados {
    <T> T  obterDados(String json, Class<T> classe);
}
