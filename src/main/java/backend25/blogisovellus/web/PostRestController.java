package backend25.blogisovellus.web;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import backend25.blogisovellus.domain.AppUserRepository;
import backend25.blogisovellus.domain.KeywordRepository;
import backend25.blogisovellus.domain.Post;
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

    @GetMapping("/posts")
    public List<Post> getAllPosts() {
        return pRepo.findAll();
    }

    @GetMapping("/postkeywords") 
        public List<PostKeyword> getAllPostKeywords() {
            return pAndKRepo.findAll();
    }

    @GetMapping("posts/id/{id}")
        public Post getPostById(@PathVariable("id") Long postId) {
            return pRepo.findById(postId).orElse(null);
        }
    
    @GetMapping("posts/title/{title}")
        public Post getPostByTitle(@PathVariable("title") String title) {
            return pRepo.findPostByTitle(title);
        }
    
    @GetMapping("posts/writer/{writer}")
        public List<Post> getPostsByWriter(@PathVariable("writer") String writer) {
            return pRepo.findPostByWriterUserName(writer);
        }

    @PostMapping("posts")
        public Post newPost(@RequestBody Post newPost) {
            return pRepo.save(newPost);
        }

    @PutMapping("posts/{id}") 
        public Post editPost(@RequestBody Post editedPost, @PathVariable Long id) {
            editedPost.setPostId(id);
            return pRepo.save(editedPost);
        }
    @DeleteMapping("posts/{id}")
        public List<Post> deletePost(@PathVariable Long id) {
            pRepo.deleteById(id);
            return pRepo.findAll();
        }
        
        

}
