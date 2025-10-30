package backend25.blogisovellus;


import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.boot.test.context.SpringBootTest;

import backend25.blogisovellus.domain.Post;
//import backend25.blogisovellus.domain.PostKeywordRepository;
import backend25.blogisovellus.domain.PostRepository;

//annotaatio ajonaikaisen tietokannan (h2) käytön testaamista varten 
@DataJpaTest

//@SpringBootTest
//annotaatio, kun testataan ulkoista tietokantaa:
// @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class BlogisovellusRepositoryTestsJPA {

    @Autowired
    private PostRepository pRepo;

/*     @Autowired
    private PostKeywordRepository pAndKRepo; */

    //postauksen otsikolla etsiminen - palauttaako oikean kirjoittajan (username)
    @Test
    public void findByTitleShouldReturnUserName() {
    Post post = pRepo.findPostByTitle("Ankle rehabilitation")
        .orElseThrow(() -> new RuntimeException("No posts by title found"));

    assertThat(post.getWriter().getUserName()).isEqualTo("elliuser");
    }

    @Test
    public void findByWriterShouldReturnPosts() {
        List<Post> posts = pRepo.findPostByWriterUserName("elliuser");
        assertThat(posts).hasSize(3);
        assertThat(posts.get(0).getTitle()).isEqualTo("Injuries in Crosssfit");
        assertThat(posts.get(1).getTitle()).isEqualTo("Ankle rehabilitation");
        assertThat(posts.get(2).getTitle()).isEqualTo("How to prevent injuries in Crossfit?");
    }


}
