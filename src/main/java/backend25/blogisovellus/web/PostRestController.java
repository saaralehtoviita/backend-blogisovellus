package backend25.blogisovellus.web;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import backend25.blogisovellus.domain.AppUserRepository;
import backend25.blogisovellus.domain.KeywordRepository;
import backend25.blogisovellus.domain.Post;
import backend25.blogisovellus.domain.PostKeywordRepository;
import backend25.blogisovellus.domain.PostRepository;

@RestController
public class PostRestController {

    
    //rajapintojen injektointi, tarvitaan, jotta tiedot tallentuvat tietokantaan

    private PostRepository pRepo;
    private KeywordRepository kRepo;
    private PostKeywordRepository pAndKRepo;
    private AppUserRepository uRepo;

    public PostRestController(PostRepository pRepo, KeywordRepository kRepo, PostKeywordRepository pAndKRepo, AppUserRepository uRepo) {
        this.pRepo = pRepo;
        this.kRepo = kRepo;
        this.pAndKRepo = pAndKRepo;
        this.uRepo = uRepo;
    }

    @GetMapping("/posts")
    public List<Post> getAllPosts() {
        return pRepo.findAll();
    }
}
