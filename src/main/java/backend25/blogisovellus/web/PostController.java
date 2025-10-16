package backend25.blogisovellus.web;

import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import backend25.blogisovellus.domain.Keyword;
import backend25.blogisovellus.domain.KeywordRepository;
import backend25.blogisovellus.domain.Post;
import backend25.blogisovellus.domain.PostKeyword;
import backend25.blogisovellus.domain.PostKeywordRepository;
import backend25.blogisovellus.domain.PostRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@Controller
public class PostController {

    private static final Logger logger = LoggerFactory.getLogger(PostController.class);

    //rajapintojen injektointi, tarvitaan, jotta tiedot tallentuvat tietokantaan

    private PostRepository pRepo;
    private KeywordRepository kRepo;
    private PostKeywordRepository pAndKRepo;

    public PostController(PostRepository pRepo, KeywordRepository kRepo, PostKeywordRepository pAndKRepo) {
        this.pRepo = pRepo;
        this.kRepo = kRepo;
        this.pAndKRepo = pAndKRepo;
    }

    //KAIKILLE KÄYTTÄJILLE
    @ResponseBody
    @GetMapping("/index")
    public String testi() {
        return "Tervetuloa blogipostaus-sovellukseen";
    }

    //postlist-endpoint palauttaa postlist.html thymeleaf sivun
    //sovelluksen pääsivu, kaikilla pääsy 
    //model-olion avulla välitetään selaimelle preposta löytyvät tiedot nimellä posts
    @GetMapping("/postlist")
    public String postList(Model model) {
        model.addAttribute("posts", pRepo.findAll());
        return "postlist";
    }

    //yksittäisen postauksen näyttäminen, sisältö vaihtuu postauksen id:n mukaan
    @GetMapping("/post/{id}")
    public String showPost(@PathVariable Long id, Model model) {
        Post postaus = pRepo.findById(id).orElse(null);
        model.addAttribute("post", postaus);
        return "post";
    }

    //SISÄÄNKIRJAUTUNEILLE KÄYTTÄJILLE

    //uusien postausten lisääminen
    //muutetaan jossain vaiheessa sellaiseksi, että vain sisäänkirjautuneet käyttäjät pääsevät lisäämään postauksia
    //model-olion avulla välitetään selaimelle (thymeleaf) uusi Post olio (nimellä post)
    //samalla tavalla välitetään koko krepon sisältö selaimelle nimellä keywords
    //addPost ei vielä tallenna mitään
    @RequestMapping("/addPost")
    public String addPost(Model model) {
        model.addAttribute("post", new Post());
        model.addAttribute("keyword", new Keyword());
        model.addAttribute("keywords", kRepo.findAll());
        return "addPost";
    }
    //valid + bindingresult tutkivat, rikkooko tallentumassa oleva postaus sääntöjä 
    @PostMapping("/savePost")
    public String savePost(@Valid @ModelAttribute("post") Post post, BindingResult br) {
        if (br.hasErrors()) {
            return "addPost";
        }

        //postauksen tekstin muotoilu
        String inputText = post.getText().trim()
        .replaceAll("\\*\\*(.*?)\\*\\*", "<strong>$1</strong>")
        .replaceAll("\\_\\_(._?)\\_\\_", "<i>$1</i>")
        .replaceAll("\n", "<br>");
        post.setText(inputText);

        pRepo.save(post);

        //avainsanojen lisääminen
        //käyttäjän antamasta syötteestä tehdään merkkijonotaulukko, pilkku erottimena
        //poistetaan tyhjät ja muutetaan pieneksi
        String[] keywords = post.getKeywordInput().split(",");
        for (String k : keywords) {
            String newKw = k.trim().toLowerCase();

            //tarkistetaan, löytyykö sana jo keyword reposta
            //jos ei löydy, tehdään uusi sana ja tallennetaan repoon
            Keyword doesExist = kRepo.findByStrKeyword(newKw).orElse(null);
            if (doesExist == null) {
                doesExist = new Keyword(newKw);
                kRepo.save(doesExist);
            }

            //luodaan uusi postkeyword-olio ja tallennetaan se repoon
            PostKeyword pk = new PostKeyword(post, doesExist);
            pAndKRepo.save(pk);
        }
        return "redirect:postlist";     
    }

    //postauksen editoiminen, tämä ei vielä tallenna postausta vaan avaa lomakkeen 
    //oikea post-olio haetaan id:n perusteella
    @RequestMapping("/editPost/{id}")
    public String editPost(@PathVariable Long id, Model model) {
        Post post = pRepo.findById(id).orElse(null);
        if (post == null) {
            return "redirect:/postlistEdit";
        }

        String keywords = post.getPostKeywords().stream()
            .map(pk -> pk.getKeyword().getStrKeyword())
            .collect(Collectors.joining(", "));
        
        post.setKeywordInput(keywords);
        model.addAttribute("post", post);
        model.addAttribute("keywords", kRepo.findAll());
        return "editPost";
    }

    //editoidun postauksen tallentaminen
    //ModelAttribute varmistaa, että oikea post-olio pysyy mukana 
    @PostMapping("/saveEditedPost")
    @Transactional //varmistaa, että tiedot tallentuvat samalla kertaa tietokantaan
    public String saveEditedPost(@Valid @ModelAttribute("post") Post editedPost, BindingResult br, Model model) {
        logger.info("Edited post id ={}", editedPost.getPostId());
        if (br.hasErrors()) {
            model.addAttribute("keywords", kRepo.findAll());
            return "editPost";
        }

        //alkuperäisen postauksen hakeminen reposta
        //asetetaan parametrina saadun editedPostin tiedot id:n perusteella existingPost-oliolle
        Post existingPost = pRepo.findById(editedPost.getPostId())
            .orElseThrow(() -> new IllegalArgumentException("Post not found with id " + editedPost.getPostId()));

        existingPost.setTitle(editedPost.getTitle());

        String formattedText = editedPost.getText().trim()
            .replaceAll("\\*\\*(.*?)\\*\\*", "<strong>$1</strong>")
            .replaceAll("\\_\\_(._?)\\_\\_", "<i>$1</i>")
            .replaceAll("\n", "<br>");
        
        existingPost.setText(formattedText);

        existingPost.setWriter(editedPost.getWriter());

        existingPost = pRepo.save(existingPost);

        pAndKRepo.deleteAllByPost(existingPost);

/*         List<PostKeyword> oldPKs = pAndKRepo.findAllByPost(existingPost);
        pAndKRepo.deleteAll(oldPKs); */

        //avainsanojen lisääminen
        //käyttäjän antamasta syötteestä tehdään merkkijonotaulukko, pilkku erottimena
        //poistetaan tyhjät ja muutetaan pieneksi
        if (editedPost.getKeywordInput() != null && !editedPost.getKeywordInput().isBlank()) {
        String[] keywords = editedPost.getKeywordInput().split(",");
        for (String k : keywords) {
            String newKw = k.trim().toLowerCase();
            Keyword kw = kRepo.findByStrKeyword(newKw).orElseGet(() ->
            kRepo.save(new Keyword(newKw)));
            //luodaan uusi postkeyword-olio ja tallennetaan se repoon
            PostKeyword pk = new PostKeyword(existingPost, kw);
            pAndKRepo.save(pk);
        }
        }
        return "redirect:postlistEdit"; 

    }



    //METODIT ADMIN OIKEUKSILLE:

    //postauksen poistaminen
    //muista vaihtaa oikeudet vain adminille!

    @PostMapping("/deletePost/{id}")
    public String deletePost(@PathVariable Long id) {
        pRepo.deleteById(id);
        return "redirect:/postlistEdit";
    }

    @GetMapping("/postlistEdit")
    public String postListEdit(Model model) {
        model.addAttribute("posts", pRepo.findAll());
        return "postlistEdit";
    }

}
