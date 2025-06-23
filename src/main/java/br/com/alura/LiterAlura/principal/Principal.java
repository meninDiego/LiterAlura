package br.com.alura.LiterAlura.principal;

import br.com.alura.LiterAlura.dto.LivroDTO;
import br.com.alura.LiterAlura.model.Autor;
import br.com.alura.LiterAlura.model.Livro;
import br.com.alura.LiterAlura.model.RespostaApi;
import br.com.alura.LiterAlura.repository.AutorRepository;
import br.com.alura.LiterAlura.repository.LivroRepository;
import br.com.alura.LiterAlura.service.ConsumoApi;
import br.com.alura.LiterAlura.service.ConverteDados;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

@Component
public class Principal {

   private final Scanner leitura = new Scanner(System.in);
   private final ConsumoApi consumo = new ConsumoApi();
   private final ConverteDados conversor = new ConverteDados();
   private final String endereco = "https://gutendex.com/books/";
   private final LivroRepository livroRepository;
   private final AutorRepository autorRepository;

   public Principal(LivroRepository livroRepository, AutorRepository autorRepository) {
      this.livroRepository = livroRepository;
      this.autorRepository = autorRepository;
   }

   public void exibirMenu() {
      int opcao = -1;

      while (opcao != 0) {
         String menu = """
                \n╔════════════════════════════════════════════╗
                ║     📚  LITERAlura - Catálogo de Livros    ║
                ╠════════════════════════════════════════════╣
                ║ 1️⃣  Buscar Livro Pelo Título              ║
                ║ 2️⃣  Listar Livros Registrados             ║
                ║ 3️⃣  Listar Autores Registrados            ║
                ║ 4️⃣  Autores Vivos em Determinado Ano      ║
                ║ 5️⃣  Livros por Linguagem                  ║
                ║ 0️⃣  🚪 Sair                                ║
                ╚════════════════════════════════════════════╝
                🔎 Escolha uma opção: 
                """;

         System.out.print(menu);
         opcao = leitura.nextInt();
         leitura.nextLine(); // limpar buffer

         switch (opcao) {
            case 1 -> buscarPorTitulo();
            case 2 -> listarLivros();
            case 3 -> listarAutores();
            case 4 -> listarAutorPorAno();
            case 5 -> listarLivrosPorLinguagem();
            case 0 -> System.out.println("👋 Encerrando o sistema... Até a próxima!");
            default -> System.out.println("❌ Opção inválida. Tente novamente.");
         }
      }
   }

   public void buscarPorTitulo() {
      System.out.print("🔍 Digite o título: ");
      String tituloBusca = leitura.nextLine().trim();

      // Normalizar a busca (tudo minúsculo, sem acentos)
      String tituloBuscaNormalizado = normalizarTexto(tituloBusca);

      String enderecoBusca = endereco + "?search=" + tituloBusca.replace(" ", "+");

      String json = consumo.obterDados(enderecoBusca);
      RespostaApi resposta = conversor.obterDados(json, RespostaApi.class);

      if (resposta == null || resposta.getResultado() == null || resposta.getResultado().isEmpty()) {
         System.out.println("⚠️ Nenhum livro encontrado com título parecido com: " + tituloBusca);
         return;
      }

      // Separar a busca em palavras
      String[] palavrasBusca = tituloBuscaNormalizado.split("\\s+");

      // Filtrar livros onde o título contém pelo menos uma palavra da busca
      List<Livro> livrosFiltrados = resposta.getResultado().stream()
              .filter(livro -> {
                 if (livro.getTitulo() == null) return false;

                 String tituloLivroNormalizado = normalizarTexto(livro.getTitulo());

                 for (String palavra : palavrasBusca) {
                    if (tituloLivroNormalizado.contains(palavra)) {
                       return true;
                    }
                 }
                 return false;
              })
              .toList();

      if (livrosFiltrados.isEmpty()) {
         System.out.println("⚠️ Nenhum livro encontrado com título parecido com: " + tituloBusca);
         return;
      }

      // Processar cada livro encontrado
      for (Livro livro : livrosFiltrados) {
         Optional<Livro> livroExistente = livroRepository.findByTitulo(livro.getTitulo());

         if (livroExistente.isPresent()) {
            System.out.println("📘 Livro já registrado no banco: " + livro.getTitulo());
            continue;
         }

         List<Autor> autoresPersistidos = new ArrayList<>();

         for (Autor autor : livro.getAutores()) {
            Optional<Autor> autorOptional = autorRepository.findByNome(autor.getNome());

            Autor autorPersistido;

            if (autorOptional.isPresent()) {
               autorPersistido = autorOptional.get();
            } else {
               Autor novoAutor = new Autor();
               novoAutor.setNome(autor.getNome());
               novoAutor.setAnoDeNascimento(autor.getAnoDeNascimento());
               novoAutor.setAnoDeFalecimento(autor.getAnoDeFalecimento());

               autorPersistido = autorRepository.save(novoAutor);
            }

            autoresPersistidos.add(autorPersistido);
         }

         List<String> linguagens = livro.getLinguagens() != null ? livro.getLinguagens() : new ArrayList<>();

         Livro livroParaSalvar = new Livro();
         livroParaSalvar.setTitulo(livro.getTitulo());
         livroParaSalvar.setAutores(autoresPersistidos);
         livroParaSalvar.setLinguagens(linguagens);

         livroRepository.save(livroParaSalvar);

         System.out.println("\n✅ Livro salvo com sucesso no banco:");
         System.out.println("📘 Título: " + livro.getTitulo());

         System.out.print("✍️ Autores: ");
         if (!autoresPersistidos.isEmpty()) {
            for (Autor autor : autoresPersistidos) {
               System.out.print(autor.getNome() + "; ");
            }
         } else {
            System.out.print("Desconhecido");
         }
         System.out.println();
      }
   }

   // Método auxiliar para normalizar texto (minusculo e sem acentos)
   private String normalizarTexto(String texto) {
      if (texto == null) return "";
      String textoMinusculo = texto.toLowerCase();
      return java.text.Normalizer.normalize(textoMinusculo, java.text.Normalizer.Form.NFD)
              .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
   }

   public void listarLivros() {
      System.out.println("\n📚📖 Lista de Livros Registrados 📖📚\n");

      List<Livro> livros = livroRepository.buscarTodosComAutores();

      if (livros.isEmpty()) {
         System.out.println("⚠️ Nenhum livro encontrado no banco.");
         return;
      }

      for (Livro livro : livros) {
         LivroDTO dto = new LivroDTO(
                 livro.getTitulo(),
                 livro.getAutores().stream().map(Autor::getNome).toList(),
                 livro.getLinguagens()
         );

         System.out.println("📘 Título: " + dto.titulo());

         System.out.print("✍️ Autores: ");
         if (!dto.autores().isEmpty()) {
            dto.autores().forEach(nome -> System.out.print(nome + "; "));
         } else {
            System.out.print("Desconhecido");
         }

         System.out.print("\n🌐 Linguagens: ");
         if (!dto.linguagens().isEmpty()) {
            dto.linguagens().forEach(lang -> System.out.print(lang + "; "));
         } else {
            System.out.print("Desconhecida");
         }

         System.out.println("\n────────────────────────");
      }
   }

   public void listarAutores() {
      System.out.println("\n🧑‍🎨 Lista de Autores Registrados 🧑‍🎨\n");

      List<Autor> autores = autorRepository.findAll();

      if (autores.isEmpty()) {
         System.out.println("⚠️ Nenhum autor registrado no banco.");
         return;
      }

      for (Autor autor : autores) {
         System.out.println("👤 Nome: " + autor.getNome());

         String nascimento = autor.getAnoDeNascimento() != null
                 ? autor.getAnoDeNascimento().toString()
                 : "Desconhecido";

         String falecimento = autor.getAnoDeFalecimento() != null
                 ? autor.getAnoDeFalecimento().toString()
                 : "Desconhecido";

         System.out.println("📅 Nascimento: " + nascimento);
         System.out.println("🕯️ Falecimento: " + falecimento);
         System.out.println("────────────────────────");
      }
   }

   public void listarAutorPorAno() {
      System.out.print("\n📅 Digite o ano desejado: ");
      int ano = leitura.nextInt();
      leitura.nextLine();

      List<Autor> autoresVivos = autorRepository
              .findByAnoDeNascimentoLessThanEqualAndAnoDeFalecimentoGreaterThanEqualOrAnoDeFalecimentoIsNull(ano, ano);

      if (autoresVivos.isEmpty()) {
         System.out.println("⚠️ Nenhum autor encontrado vivo no ano " + ano + ".");
         return;
      }

      System.out.println("\n📜 Autores vivos no ano " + ano + ":\n");

      for (Autor autor : autoresVivos) {
         System.out.println("👤 Nome: " + autor.getNome());
         System.out.println("📅 Nascimento: " + autor.getAnoDeNascimento());
         System.out.println("🕯️ Falecimento: " +
                 (autor.getAnoDeFalecimento() != null ? autor.getAnoDeFalecimento() : "Ainda vivo ou desconhecido"));
         System.out.println("────────────────────────");
      }
   }

   public void listarLivrosPorLinguagem() {
      System.out.print("\n🌐 Digite o código da linguagem (ex: 'en' para inglês, 'pt' para português): ");
      String linguagem = leitura.nextLine().trim().toLowerCase();

      List<Livro> livros = livroRepository.buscarTodosComAutoresELinguagem();

      List<Livro> livrosFiltrados = livros.stream()
              .filter(l -> l.getLinguagens() != null && l.getLinguagens().contains(linguagem))
              .toList();

      if (livrosFiltrados.isEmpty()) {
         System.out.println("⚠️ Nenhum livro encontrado na linguagem '" + linguagem + "'.");
         return;
      }

      System.out.println("\n📚 Livros na linguagem '" + linguagem + "':\n");

      for (Livro livro : livrosFiltrados) {
         System.out.println("📘 Título: " + livro.getTitulo());

         System.out.print("✍️ Autores: ");
         if (livro.getAutores() != null && !livro.getAutores().isEmpty()) {
            livro.getAutores().forEach(autor -> System.out.print(autor.getNome() + "; "));
         } else {
            System.out.print("Desconhecido");
         }

         System.out.println("\n────────────────────────");
      }
   }
}