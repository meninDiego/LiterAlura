package br.com.alura.LiterAlura.principal;

import br.com.alura.LiterAlura.model.Autor;
import br.com.alura.LiterAlura.model.Livro;
import br.com.alura.LiterAlura.model.RespostaApi;
import br.com.alura.LiterAlura.service.ConsumoApi;
import br.com.alura.LiterAlura.service.ConverteDados;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class Principal {

   private final Scanner leitura = new Scanner(System.in);
   private final ConsumoApi consumo = new ConsumoApi();
   private final ConverteDados conversor = new ConverteDados();
   private final String endereco = "https://gutendex.com/books/";

   public void exibirMenu() {
      int opcao = -1;

      while (opcao != 0) {
         String menu = """
                    \nLITERAlura - Catálogo de Livros
                    
                    1 - Buscar Livro Pelo Título
                    2 - Listar Livros Registrados
                    3 - Listar Autores Registrados
                    4 - Listar Autores Vivos Em Determinado Ano
                    5 - Listar Livros Em Uma Determinada Linguagem
                    0 - Sair
                    
                    Escolha uma opção:
                    """;

         System.out.println(menu);
         opcao = leitura.nextInt();
         leitura.nextLine(); // limpar buffer

         switch (opcao) {
            case 1 -> buscarPorTitulo();
            case 2 -> listarLivros();
            case 3 -> listarAutores();
            case 4 -> listarAutorPorAno();
            case 5 -> listarLivrosPorLinguagem();
            case 0 -> System.out.println("Saindo...");
            default -> System.out.println("Opção inválida. Tente novamente.");
         }
      }
   }

   public void buscarPorTitulo() {
      System.out.print("Digite o título: ");
      var tituloBusca = leitura.nextLine();
      var enderecoBusca = endereco + "?search=" + tituloBusca.replace(" ", "+");

      String json = consumo.obterDados(enderecoBusca);
      RespostaApi resposta = conversor.obterDados(json, RespostaApi.class);

      for (Livro livro : resposta.getResultado()) {
         System.out.println("\n📘 Título: " + livro.getTitulo());

         System.out.print("✍ Autores: ");
         if (livro.getAutores() != null && !livro.getAutores().isEmpty()) {
            for (Autor autor : livro.getAutores()) {
               System.out.print(autor.getNome() + "; ");
            }
         } else {
            System.out.print("Desconhecido");
         }
         System.out.println(); // quebra de linha
      }
   }

   public void listarLivros() {
      // Sugestão: implementar consulta via livroService.findAll() e mostrar os livros
   }

   public void listarAutores() {
      // Implementar listagem de autores
   }

   public void listarAutorPorAno() {
      // Implementar filtro por ano de nascimento/falecimento
   }

   public void listarLivrosPorLinguagem() {
      // Implementar filtro por linguagem
   }
}
