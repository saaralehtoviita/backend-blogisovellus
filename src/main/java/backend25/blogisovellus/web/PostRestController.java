package backend25.blogisovellus.web;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import backend25.blogisovellus.domain.AppUser;
import backend25.blogisovellus.domain.AppUserDTO;
import backend25.blogisovellus.domain.AppUserRepository;
import backend25.blogisovellus.domain.Keyword;
import backend25.blogisovellus.domain.KeywordRepository;
import backend25.blogisovellus.domain.Post;
import backend25.blogisovellus.domain.PostDTO;
import backend25.blogisovellus.domain.PostKeyword;
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

    //POST-testaamista varten testimetodi:
    @PostMapping("/test")
        public ResponseEntity<String> testPost() {
            return ResponseEntity.ok("POST works");
        }

    //kaikki postaukset listattuna
    @GetMapping("/posts")
    public List<PostDTO> getAllPosts() {
        return pRepo.findAll().stream().map(post -> {
            PostDTO dto = new PostDTO();
            dto.setTitle(post.getTitle());
            dto.setText(post.getText());
            dto.setPostDate(post.getPostDate());
            dto.setWriterUsername(post.getWriter().getUserName());
            dto.setKeywords(post.getKeywordsAsStringList());
            return dto;
        })
        .collect(Collectors.toList());
    }

    //kaikki postkeywordit listattuna
    @GetMapping("/postkeywords") 
        public List<PostKeyword> getAllPostKeywords() {
            return pAndKRepo.findAll();
    }

    //kaikki keywordit listattuna
    @GetMapping("/keywords")
        public List<Keyword> getAllKeywords() {
            return kRepo.findAll();
        }
    
    //kaikki käyttäjät listattuna - tämä metodi ei julkinen koska sisältää henkilötietoja
    @GetMapping("/users")
        public List<AppUser> getAllUsers() {
            return uRepo.findAll();
        }
    
    //käyttäjän hakeminen id:n perusteella   
    @GetMapping("/users/id/{id}")
        public AppUser getUserById(@PathVariable("id") Long userId) {
            return uRepo.findById(userId).orElse(null);
        }

    //postauksen hakeminen id:n perusteella
    @GetMapping("posts/id/{id}")
        public Post getPostById(@PathVariable("id") Long postId) {
            return pRepo.findById(postId).orElse(null);
        }

    //postauksen hakeminen otsikon perusteella
    @GetMapping("posts/title/{title}")
        public Post getPostByTitle(@PathVariable("title") String title) {
            return pRepo.findPostByTitle(title);
        }
    
    //postaukset listattuna kirjoittajan perusteella
    @GetMapping("posts/writer/{writer}")
        public List<Post> getPostsByWriter(@PathVariable("writer") String writer) {
            return pRepo.findPostByWriterUserName(writer);
        }
    
    //postaukset listattuna keywordin perusteella
    @GetMapping("posts/keyword/{keyword}")
        public List<Post> getPostsByKeyword(@PathVariable("keyword") String keyword) {
            return pRepo.findPostByPostKeywords_Keyword_StrKeyword(keyword);
        }

    //uuden postauksen lisääminen
    //käytetään apuna PostDTO:ta tietojen välittämiseen oikealle post-oliolle
    @PostMapping("/posts")
        public Post newPost(@RequestBody PostDTO postDTO) {
            Post post = new Post();
            //kopioidaan DTO:n tiedot Post-objektille 
            post.setTitle(postDTO.getTitle());
            post.setText(postDTO.getText());
            post.setPostDate(LocalDate.now().toString());

            AppUser writer = uRepo.findByUserName(postDTO.getWriterUsername())
                            .orElseThrow(() -> new RuntimeException("Writer not found"));
            post.setWriter(writer);
            
            return pRepo.save(post);
        }
    
    //uuden käyttäjän lisääminen
    @PostMapping("/users")
        public AppUser newUser(@RequestBody AppUser newUser) {
            return uRepo.save(newUser);
        }

    //postauksen editoiminen id:n perusteella
    @PutMapping("posts/id/{id}") 
        public Post editPost(@RequestBody Post editedPost, @PathVariable("id") Long postId) {
            editedPost.setPostId(postId);
            return pRepo.save(editedPost);
        }

    //käyttäjän editoiminen id:n perusteella
    @PutMapping("users/id/{id}")
        public AppUser editUser(@RequestBody AppUser editedUser, @PathVariable("id") Long userId)  {
            editedUser.setUserId(userId);
            return uRepo.save(editedUser);
        }
    
    //käyttäjän poistaminen id:n perusteella
    @DeleteMapping("users/id/{id}")
        public List<AppUser> deleteUser(@PathVariable("id") Long userId) {
            uRepo.deleteById(userId);
            return uRepo.findAll();
        }
    
    //postauksen poistaminen id:n perusteella
    @DeleteMapping("posts/id/{id}")
        public List<Post> deletePost(@PathVariable("id") Long id) {
            pRepo.deleteById(id);
            return pRepo.findAll();
        }
}
