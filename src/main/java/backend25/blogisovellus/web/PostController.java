package backend25.blogisovellus.web;

import java.util.HashSet;
import java.util.Set;

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
import jakarta.validation.Valid;

@Controller
public class PostController {

    //rajapintojen injektointi, tarvitaan, jotta tiedot tallentuvat tietokantaan

    private PostRepository pRepo;
    private KeywordRepository kRepo;
    private PostKeywordRepository pAndKRepo;

    public PostController(PostRepository pRepo, KeywordRepository kRepo, PostKeywordRepository pAndKRepo) {
        this.pRepo = pRepo;
        this.kRepo = kRepo;
        this.pAndKRepo = pAndKRepo;
    }

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






}
