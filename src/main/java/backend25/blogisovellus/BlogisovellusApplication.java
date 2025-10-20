package backend25.blogisovellus;

//import java.util.HashSet;
//import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import backend25.blogisovellus.domain.AppUser;
import backend25.blogisovellus.domain.AppUserRepository;
import backend25.blogisovellus.domain.Keyword;
import backend25.blogisovellus.domain.KeywordRepository;
import backend25.blogisovellus.domain.Post;
import backend25.blogisovellus.domain.PostKeyword;
import backend25.blogisovellus.domain.PostKeywordRepository;
import backend25.blogisovellus.domain.PostRepository;

@SpringBootApplication
public class BlogisovellusApplication {

	public static void main(String[] args) {
		SpringApplication.run(BlogisovellusApplication.class, args);
	}

	//demodataa luodaan CommandLineRunnerilla

	@Bean
	public CommandLineRunner blogCommandLineRunner(PostRepository pRepo, AppUserRepository uRepo, KeywordRepository kRepo, PostKeywordRepository pkRepo) {
		return (args) -> {
	//avainsanojen luominen ja tallentaminen repoon
	Keyword knee = new Keyword("knee");
	Keyword shoulder = new Keyword("shoulder");
	Keyword crossfit = new Keyword("crossfit");
	Keyword ankle = new Keyword("ankle");
	Keyword injury = new Keyword("injury");

	kRepo.save(crossfit);
	kRepo.save(injury);
	kRepo.save(ankle);
	kRepo.save(shoulder);
	kRepo.save(knee);

	//luodaan käyttäjiä

	AppUser u1 = new AppUser("Testi", "Testinen", "user", "$2a$10$91d8dtRJvcEbqonif/vtKuod24Sudu4OVfLBa5muyDaiaDLRl5F9i", "USER");
	AppUser a = new AppUser("Admintesti", "AdminTestinen", "admin", "$2a$10$v9iDxguTwkaQm8utbKaCLuwxir/UNJj7sgUUFHOAdCEDuMqlp7MUC", "ADMIN");
	AppUser u2 = new AppUser("Elli", "Esimerkki", "elliuser", "$2a$10$PHKydfpA9E.1YzZcpMXMdOfiMqItqFFNIjvh1iwy3u1pm/akXp4o6", "USER");
	
	//tallennetaan käyttäjät repoon
	uRepo.save(u1);
	uRepo.save(a);
	uRepo.save(u2);

	//luodaan postauksia ensin ilman keywordseja ja tallennetaan ne repoon
	//yhdistetään postaukset ja avainsanat - vastaa tiedon tallentamista postkeyword välitauluun
	Post post1 = new Post("Urheiluvammat Crossfitissa", "Blogipostaustekstiä 1.", "23.9.2025", u2);
	pRepo.save(post1);
	
	pkRepo.save(new PostKeyword(post1, crossfit));
	pkRepo.save(new PostKeyword(post1, injury));


	Post post2 = new Post("Olkapään kuntoutus", "Blogipostaustekstiä 2.", "24.9.2025", u1);
	pRepo.save(post2);

	pkRepo.save(new PostKeyword(post2, crossfit));
	pkRepo.save(new PostKeyword(post2, shoulder));


	Post post3 = new Post("Nilkan kuntoutus", "Blogipostaustekstiä 3.", "25.9.2025", u2);
	pRepo.save(post3);
	
	pkRepo.save(new PostKeyword(post3, crossfit));
	pkRepo.save(new PostKeyword(post3, ankle));



	
	
	

	};
	}


}
