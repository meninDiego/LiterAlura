package br.com.alura.LiterAlura.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RespostaApi {

    @JsonAlias("count")
    private int quantidade;

    @JsonAlias("next")
    private String proximo;

    @JsonAlias("previous")
    private String anterior;

    @JsonAlias("results")
    private List<Livro> resultado;

    // getters e setters
    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }

    public String getProximo() { return proximo; }
    public void setProximo(String proximo) { this.proximo = proximo; }

    public String getAnterior() { return anterior; }
    public void setAnterior(String anterior) { this.anterior = anterior; }

    public List<Livro> getResultado() { return resultado; }
    public void setResultado(List<Livro> resultado) { this.resultado = resultado; }
}
