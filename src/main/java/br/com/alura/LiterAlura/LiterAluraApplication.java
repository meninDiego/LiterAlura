package br.com.alura.LiterAlura;

import br.com.alura.LiterAlura.principal.Principal;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class LiterAluraApplication implements CommandLineRunner {

	private final Principal principal;

    public LiterAluraApplication(Principal principal) {
        this.principal = principal;
    }
	@Override
	public void run(String... args) throws Exception {
		principal.exibirMenu();

	}
    public static void main(String[] args) {
		SpringApplication.run(LiterAluraApplication.class, args);
	}
}
