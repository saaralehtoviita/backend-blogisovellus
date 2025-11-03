package backend25.blogisovellus.web;

import java.util.ArrayList;
import java.util.List;
//import java.util.HashSet;
//import java.util.Set;
//import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import backend25.blogisovellus.domain.AppUser;
import backend25.blogisovellus.domain.AppUserRepository;
import backend25.blogisovellus.domain.Keyword;
import backend25.blogisovellus.domain.KeywordRepository;
import backend25.blogisovellus.domain.Post;
import backend25.blogisovellus.domain.PostKeyword;
import backend25.blogisovellus.domain.PostKeywordRepository;
import backend25.blogisovellus.domain.PostRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;

@Controller
public class PostController {

    private static final Logger logger = LoggerFactory.getLogger(PostController.class);

    //rajapintojen injektointi, tarvitaan, jotta tiedot tallentuvat tietokantaan

    private PostRepository pRepo;
    private KeywordRepository kRepo;
    private PostKeywordRepository pAndKRepo;
    private AppUserRepository uRepo;

    public PostController(PostRepository pRepo, KeywordRepository kRepo, PostKeywordRepository pAndKRepo, AppUserRepository uRepo) {
        this.pRepo = pRepo;
        this.kRepo = kRepo;
        this.pAndKRepo = pAndKRepo;
        this.uRepo = uRepo;
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
    //modelilla välitetään selaimelle preposta löytyvä postaus nimellä post 
    @GetMapping("/post/{id}")
    public String showPost(@PathVariable Long id, Model model) {
        Post postaus = pRepo.findById(id).orElse(null);
        model.addAttribute("post", postaus);
        return "post";
    }

    //kaikki avainsanat merkkijono-listana
    //toistaiseksi metodia ei käytetä mihinkään
    //tätä voisi myöhemmin hyödyntää, kun haetaan postauksia avainsanan perusteella - käyttäjä siis näkisi, mitä avainsanoja on olemassa
    @GetMapping("/keywordlist")
    public String keywordList(Model model) {
        List<Keyword> keywordObjects = kRepo.findAll();
        List<String> keywords = new ArrayList<>();
        for (Keyword k : keywordObjects) {
            keywords.add(k.getStrKeyword());
        }
        model.addAttribute("keywords", keywords);
        return "keywordlist";
    }

    //postaukset listattuna keywordin perusteella
    //postlist templatessa käyttäjä syöttää lomakkeelle merkkijonon (th:name="input")
    //käyttäjän painaessa search-painiketta, aktivoituu allaoleva metodi

    @GetMapping("/postlistKw")
        public String showPostsByKeyword(@RequestParam("input") String input, Model model) {
            //luodaan ensin listat keywordolioisa ja sitten niiten keyword-merkkijoinoista
            List<Keyword> keywordObjects = kRepo.findAll();
            List<String> keywords = new ArrayList<>();
            for (Keyword k : keywordObjects) {
            keywords.add(k.getStrKeyword());
        }   
            String message = "Keyword not found";
            //jos käyttäjän antama sana löytyy merkkijonolistalta
            //input-merkkijonon perusteella haetaan postaus-reposta kaikki postaukset joista löytyy kyseinen avainsana
            //modelin välityksellä postsByKeyword välitetään thymeleafille, josta postaukset listataan
            //modelin välityksellä kwByUser välitetään thymeleafille 
            if (keywords.contains(input)) {
            List<Post> postsByKeyword = pRepo.findPostByPostKeywords_Keyword_StrKeyword(input);
            model.addAttribute("postsByKeyword", postsByKeyword);
            model.addAttribute("kwByUser", input);
            return "postlistKw";
            } else { //jos ei löydy, läheteään viesti sekä kaikki postaukset selaimelle ja ohjataan takaisin postlist näkymään
                model.addAttribute("message", message);
                model.addAttribute("posts", pRepo.findAll());
                return "postlist";
            }
        }

    //SISÄÄNKIRJAUTUNEILLE KÄYTTÄJILLE

    @GetMapping("/postlist_username/{userName}")
    public String showPostsByUserName(@PathVariable String userName, Model model) {
        //String userName = authentication.getName();
        List<Post> postsByUserName = pRepo.findPostByWriterUserName(userName);
        model.addAttribute("postsByUserName", postsByUserName);
        return "postlist_username";
    }

    //uusien postausten lisääminen
    //muutetaan jossain vaiheessa sellaiseksi, että vain sisäänkirjautuneet käyttäjät pääsevät lisäämään postauksia
    //model-olion avulla välitetään selaimelle (thymeleaf) uusi Post olio (nimellä post)
    //samalla tavalla välitetään koko krepon sisältö selaimelle nimellä keywords
    //addPost ei vielä tallenna mitään
    @RequestMapping("/addPost")
    @PreAuthorize("hasAuthority('USER')")
    public String addPost(Model model) {
        model.addAttribute("post", new Post());
        model.addAttribute("keyword", new Keyword());
        model.addAttribute("keywords", kRepo.findAll());
        return "addPost";
    }
    //valid + bindingresult tutkivat, rikkooko tallentumassa oleva postaus sääntöjä 
    @PostMapping("/savePost")
    @PreAuthorize("hasAuthority('USER')")
    public String savePost(@Valid @ModelAttribute("post") Post post, BindingResult br, Authentication authentication) {
        if (br.hasErrors()) {
            return "addPost";
        }
        //haetaan sisäänkirjautuneen käyttäjän tiedot authentication avulla
        //asetetaan tiedot kirjoittajalle
        //jos kirjoittajaa ei löydy -> virheilmoitus
        String username = authentication.getName();
        AppUser writer = uRepo.findByUserName(username).orElseThrow(() ->
        new IllegalStateException("User not found: " + username));
        post.setWriter(writer);

        //postauksen tekstin muotoilu
        String inputText = post.getText().trim()
            .replaceAll("\\*\\*(.*?)\\*\\*", "<strong>$1</strong>")
            .replaceAll("\\_\\_(.*?)\\_\\_", "<i>$1</i>")
            .replaceAll("\n", "<br>");
        post.setText(inputText);

        //tallennetaan ensin post-olio repoon ilman avainsanoja
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
        //pRepo.save(post);
        return "redirect:/postlist_username/" + writer.getUserName();     
    }

    //postauksen editoiminen, tämä ei vielä tallenna postausta vaan avaa lomakkeen 
    //oikea post-olio haetaan id:n perusteella
    @RequestMapping("/editPost/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')") //anyauthority mahdollistaa useamman roolin
    public String editPost(@PathVariable Long id, Model model, Authentication authentication) {
        //tarkistetaan id:n perusteella löytyyko postaus reposta
        Post post = pRepo.findById(id).orElse(null);
        if (post == null) {
            return "redirect:/postlistEdit";
        }

        //tarkastetaan, onko kirjautuneella käyttäjällä admin rooli
        boolean isAdmin = authentication.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ADMIN"));
        //tehdään merkkijono autentikoidun/(sisäänkirjautuneen) käyttäjän tunnuksesta
        String currUser = authentication.getName();

        //jos käyttäjä ei ole admin eikä kirjoittajan tiedot vastaa kirjautunutta käyttäjää ->
        //ohjataan takaisin käyttäjän omalle sivulle
        //tähän tilanteeseen päädytään vain, jos käyttäjä kirjoittaa suoraan urliin osoitteen 
        //tähän kohtaan voisi lisätä virheidenkäsittelyä
        if (!isAdmin && !post.getWriter().getUserName().equals(currUser)) {
            return "redirect:/postlist_username/" + authentication.getName();
        }

        //haetaan postauksen keywordit ja tehdään niistä merkkijono, pilkku erottimena
        String keywords = post.getKeywordsAsString();
    
        //asetetaan keywords-merkkijono inputin paikalle
        post.setKeywordInput(keywords);
        //lisätään modelille post-olio, täytyy vastata thymeleafista löytyvää tietoa 
        model.addAttribute("post", post);
        //lisätään modelille avainsana-repon kaikki tiedot
        //model.addAttribute("keywords", kRepo.findAll()); turha askel, koska käytetään myöhemmin keywordInputia 
        return "editPost";
    }

    //editoidun postauksen tallentaminen
    //ModelAttribute varmistaa, että oikea post-olio pysyy mukana 
    @PostMapping("/saveEditedPost")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @Transactional //varmistaa, että tiedot tallentuvat samalla kertaa tietokantaan
    public String saveEditedPost(@Valid @ModelAttribute("post") Post editedPost, BindingResult br, Model model, Authentication authentication) {
        //logger.info("Edited post id ={}", editedPost.getPostId());
        if (br.hasErrors()) {
            //model.addAttribute("keywords", kRepo.findAll());
            return "editPost";
        }

        //alkuperäisen postauksen hakeminen reposta
        //asetetaan parametrina saadun editedPostin tiedot id:n perusteella existingPost-oliolle
        Post existingPost = pRepo.findById(editedPost.getPostId())
            .orElseThrow(() -> new IllegalArgumentException("Post not found with id " + editedPost.getPostId()));

        //asetetaan postauksen otsikko 
        existingPost.setTitle(editedPost.getTitle());

        //haetaan postauksen teksti ja muotoillaan se
        String formattedText = editedPost.getText().trim()
            .replaceAll("\\*\\*(.*?)\\*\\*", "<strong>$1</strong>")
            .replaceAll("\\_\\_(.*?)\\_\\_", "<i>$1</i>")
            .replaceAll("\n", "<br>");
        
        //asetetaan muotoilti teksti postauksen sisällöksi
        existingPost.setText(formattedText);

        //tallennetaan postaus repoon
        existingPost = pRepo.save(existingPost);

        //poistetaan kaikki postaukseen liitetyt postkeyword-oliot jotta voidaan lisätä uudet
        pAndKRepo.deleteAllByPost(existingPost);

        //avainsanojen lisääminen
        //käyttäjän antamasta syötteestä tehdään merkkijonotaulukko, pilkku erottimena
        if (editedPost.getKeywordInput() != null && !editedPost.getKeywordInput().isBlank()) {
        String[] keywords = editedPost.getKeywordInput().split(",");
        for (String k : keywords) {
            String newKw = k.trim().toLowerCase(); //poistetaan tyhjät ja muutetaan pieneksi
            Keyword kw = kRepo.findByStrKeyword(newKw).orElseGet(() ->
            kRepo.save(new Keyword(newKw))); //tehdään uusi keyword, jos sitä ei löydy reposta
            //luodaan uusi postkeyword-olio ja tallennetaan se repoon
            PostKeyword pk = new PostKeyword(existingPost, kw);
            pAndKRepo.save(pk);
        }
        }
        //ohjataan tallennuksen jälkeen adminit omalle sivulle ja käyttäjät omalle
        if (authentication.getAuthorities().stream().anyMatch(a ->
        a.getAuthority().equals("ADMIN"))) { 
        return "redirect:/postlistEdit";
        } else {
            return "redirect:/postlist_username/" + authentication.getName();
        }

    }



    //METODIT ADMIN OIKEUKSILLE:

    //postauksen poistaminen
    //muista vaihtaa oikeudet vain adminille!

    @PostMapping("/deletePost/{id}")
    @PreAuthorize("hasAuthority('ADMIN')") //postgren kanssa hasRole
    public String deletePost(@PathVariable Long id) {
        Post post = pRepo.findById(id).orElseThrow(() -> new RuntimeException("Post not found by given id"));
        pRepo.delete(post);
        return "redirect:/postlistEdit";
    }

    @GetMapping("/postlistEdit")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String postListEdit(Model model) {
        model.addAttribute("posts", pRepo.findAll());
        return "postlistEdit";
    }

}
