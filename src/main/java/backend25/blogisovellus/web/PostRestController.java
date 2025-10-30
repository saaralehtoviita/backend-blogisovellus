package backend25.blogisovellus.web;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.management.RuntimeErrorException;

import org.apache.catalina.connector.Response;
import org.springframework.http.HttpStatus;
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
    @GetMapping("/api/posts")
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
    //turha metodi?
    @GetMapping("/api/postkeywords") 
        public List<PostKeyword> getAllPostKeywords() {
            return pAndKRepo.findAll();
    }

    //kaikki keywordit listattuna
    @GetMapping("/api/keywords")
        public List<Keyword> getAllKeywords() {
            return kRepo.findAll();
        }
    
    //kaikki käyttäjät listattuna
    //käytetään DTO-luokkaa jossa ei ole salasanatietoja
    @GetMapping("/api/users")
        public List<AppUserDTO> getAllUsers() {
            return uRepo.findAll().stream().map(appuser -> {
                AppUserDTO dto = new AppUserDTO();
                dto.setFirstName(appuser.getFirstName());
                dto.setLastName(appuser.getLastName());
                dto.setUserName(appuser.getUserName());
                dto.setRole(appuser.getRole());
                return dto;
            })
            .collect(Collectors.toList());
        }
    
    //käyttäjän hakeminen id:n perusteella   
    @GetMapping("/api/users/id/{id}")
        public AppUserDTO getUserById(@PathVariable("id") Long userId) {
            AppUser appuser = uRepo.findById(userId).orElseThrow(() -> new RuntimeException("User not found by given id."));
            AppUserDTO dto = new AppUserDTO();
            dto.setFirstName(appuser.getFirstName());
            dto.setLastName(appuser.getLastName());
            dto.setUserName(appuser.getUserName());
            dto.setRole(appuser.getRole());
            return dto;
        }

    //postauksen hakeminen id:n perusteella
    @GetMapping("/api/posts/id/{id}")
        public PostDTO getPostById(@PathVariable("id") Long postId) {
            Post post = pRepo.findById(postId).orElseThrow(() -> new RuntimeException("No post was found by given id"));
            PostDTO dto = new PostDTO();
            dto.setTitle(post.getTitle());
            dto.setText(post.getText());
            dto.setPostDate(post.getPostDate());
            dto.setWriterUsername(post.getWriter().getUserName());
            dto.setKeywords(post.getKeywordsAsStringList());
            return dto;
        }

    //postauksen hakeminen otsikon perusteella
    @GetMapping("/api/posts/title/{title}")
        public PostDTO getPostByTitle(@PathVariable("title") String title) {
            Post post = pRepo.findPostByTitle(title).orElseThrow(() -> new RuntimeException("No post was found by given title"));
            PostDTO dto = new PostDTO();
            dto.setTitle(post.getTitle());
            dto.setText(post.getText());
            dto.setPostDate(post.getPostDate());
            dto.setWriterUsername(post.getWriter().getUserName());
            dto.setKeywords(post.getKeywordsAsStringList());
            return dto;
        }
    
    //postaukset listattuna kirjoittajan perusteella
    //käytetään ennaltamäärittelemätöntö ResponseEntityä koska etukäteen ei tiedetä, löytyykö kirjoittajan nimellä postauksia
    //jos postauksia ei löydy, palautetaan body(merkkijono)
    //jos postauksia löytyy, palautetaan lista
    @GetMapping("/api/posts/writer/{writer}")
        public ResponseEntity<?> getPostsByWriter(@PathVariable("writer") String writer) {     
            List<PostDTO> dtos = pRepo.findPostByWriterUserName(writer).stream().map(post -> {
                PostDTO dto = new PostDTO();
                dto.setTitle(post.getTitle());
                dto.setText(post.getText());
                dto.setPostDate(post.getPostDate());
                dto.setWriterUsername(post.getWriter().getUserName());
                dto.setKeywords(post.getKeywordsAsStringList());
                return dto;
            })
            .collect(Collectors.toList());

            if (dtos.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No posts where found by username: " + writer);
            }
            return ResponseEntity.ok(dtos);
        }
    
    //postaukset listattuna keywordin perusteella
    //sama periaate, kun edellinen metodi
    @GetMapping("/api/posts/keyword/{keyword}")
        public ResponseEntity<?> getPostsByKeyword(@PathVariable("keyword") String keyword) {
            List<PostDTO> dtos = pRepo.findPostByPostKeywords_Keyword_StrKeyword(keyword).stream().map(post -> {
                PostDTO dto = new PostDTO();
                dto.setTitle(post.getTitle());
                dto.setText(post.getText());
                dto.setPostDate(post.getPostDate());
                dto.setWriterUsername(post.getWriter().getUserName());
                dto.setKeywords(post.getKeywordsAsStringList());
                return dto;
            })
            .collect(Collectors.toList());
            
            if (dtos.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No posts where found by keyword: " + keyword);
            }
            return ResponseEntity.ok(dtos);
        }

    //uuden postauksen lisääminen
    //käytetään apuna PostDTO:ta tietojen välittämiseen oikealle post-oliolle
    //metodi ottaa vastaan postDTO tyyppisen parametrin 
    @PostMapping("/api/posts")
        public Post newPost(@RequestBody PostDTO postDTO) {
            Post post = new Post();
            //kopioidaan DTO:n tiedot Post-objektille 
            post.setTitle(postDTO.getTitle());
            post.setText(postDTO.getText());
            post.setPostDate(LocalDate.now().toString());

            //katsotaan, löytyykö kannasta usernamella kirjoittajaa 
            AppUser writer = uRepo.findByUserName(postDTO.getWriterUsername())
                            .orElseThrow(() -> new RuntimeException("Writer not found")); //kirjoittajan pitää löytyä kannasta
            post.setWriter(writer); //asetetaan kirjoittajan tiedot post-objektille 

            Set<PostKeyword> postKeywords = postDTO.getKeywords().stream() 
                .map(str -> {
                    Keyword keyword = kRepo.findByStrKeyword(str) //kannasta etsitään merkkijonon str perusteella ja asetetaan se keyword-objektille
                        .orElseGet(() -> {
                            Keyword k = new Keyword(); //jos ei löydy, tehdään uusi keyword-olio
                            k.setStrKeyword(str); //asetetaan merkijono sen strKeyword-arvoksi
                            return kRepo.save(k); //tallennetaan avainsana kantaan
                        });
                    PostKeyword pk = new PostKeyword(); //tehdään uusi postkeyword-objekti ja asetetaan se postaukselle ja keywordille
                    pk.setPost(post);
                    pk.setKeyword(keyword);
                    return pk;
                })
                .collect(Collectors.toSet()); //luodaan PostKeyword-setti dto-luokan keywords-listasta
            post.setPostKeywords(postKeywords);
            
            return pRepo.save(post);
        }
    
    //uuden käyttäjän lisääminen
    //käytetään DTO-luokkaa
    //käyttäjät tallentuvat siis suppeilla tiedoilla ilman mm. salasanaa
    @PostMapping("/api/users")
        public AppUser newUser(@RequestBody AppUserDTO newUser) {
            AppUser appuser = new AppUser();
            appuser.setFirstName(newUser.getFirstName());
            appuser.setLastName(newUser.getLastName());
            appuser.setUserName(newUser.getUserName());
            appuser.setRole(newUser.getRole());
            return uRepo.save(appuser);
        }

    //postauksen editoiminen id:n perusteella
    //metodista puuttuu keywordsien editoiminen
    @PutMapping("/api/posts/id/{id}") 
        public Post editPost(@RequestBody PostDTO editedPost, @PathVariable("id") Long postId) {
            Post post = pRepo.findById(postId)
            .orElseThrow(() -> new RuntimeException("Post not found"));

            post.setTitle(editedPost.getTitle());
            post.setText(editedPost.getText());
            post.setPostDate(editedPost.getPostDate());
            post.setWriter(uRepo.findByUserName(editedPost.getWriterUsername()).orElseThrow(() ->
            new RuntimeException("User not found - writer can not be updated")));
            return pRepo.save(post);
        }

    //käyttäjän editoiminen usernamen perusteella
    @PutMapping("/api/users/username/{userName}")
        public AppUserDTO editUser(@RequestBody AppUserDTO editedUser, @PathVariable("userName") String userName)  {
            AppUser appuser = uRepo.findByUserName(userName)
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            appuser.setFirstName(editedUser.getFirstName());
            appuser.setLastName(editedUser.getLastName());
            appuser.setRole(editedUser.getRole());
            uRepo.save(appuser);
            return editedUser;
        }
    
    //käyttäjän poistaminen id:n perusteella
/*     @DeleteMapping("/api/users/id/{id}")
        public List<AppUser> deleteUser(@PathVariable("id") Long userId) {
            AppUser user = uRepo.findById(userId).orElseThrow(() -> new RuntimeException("User not found by given id"));
            uRepo.delete(user);
            return uRepo.findAll();
        } */
    
    @DeleteMapping("/api/users/id/{id}")
        public ResponseEntity<String> deleteUser(@PathVariable("id") Long userId) {
            AppUser user = uRepo.findById(userId).orElseThrow(() -> new RuntimeException("User not found by given id"));
            uRepo.delete(user);
            return ResponseEntity.ok("User with id: " + userId + " was deleted succesfully.");
        }

    
    
    //postauksen poistaminen id:n perusteella
/*     @DeleteMapping("/api/posts/id/{id}")
        public List<Post> deletePost(@PathVariable("id") Long id) {
            Post post = pRepo.findById(id).orElseThrow(() -> new RuntimeException("Post not found by given id"));
            pRepo.delete(post);
            return pRepo.findAll();
        } */
    
    @DeleteMapping("/api/posts/id/{id}")
        public ResponseEntity<String> deletePost(@PathVariable("id") Long id) {
            Post post = pRepo.findById(id).orElseThrow(() -> new RuntimeException("Post not found by given id"));
            pRepo.delete(post);
            return ResponseEntity.ok("Post with id: " + id + " was deleted succesfully.");
        }

    //keywordin poistaimien id:n perusteelle
    //keywordia ei voi poistaa jos se esiintyy postkeywords-oliossa
    //responseentity sisältää HTTP status koodin, otsikot ja bodyn
    //metodissa palautetaan status ja merkkijono
    @DeleteMapping("/api/keywords/id/{id}")
        public ResponseEntity<String> deleteKeyword(@PathVariable("id") Long id) {
            if (kRepo.existsById(id)) {
            boolean keywordInUse = pAndKRepo.existsByKeyword_KeywordId(id);

            if (keywordInUse) {
                return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("Keyword is used in a post and can not be deleted");
            }
            kRepo.deleteById(id);
            return ResponseEntity.ok("Keyword deleted");
            } else {
                return ResponseEntity.badRequest().body("Keyword not found by given id");
            }
        }
}
