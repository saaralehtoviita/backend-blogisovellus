package backend25.blogisovellus.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
//import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

//Post-luokka - tietokannan puolella POSTS


@Entity
@Table(name="POSTS")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="post_id")
    private Long postId;

    @NotEmpty(message = "Name of post cannot be empty")
    @Size(min = 1, max = 250)
    private String title;

    @NotEmpty(message = "Text field cannot be empty")
    @Size(min = 1, max = 2000)
    @Column(name="post_text")
    private String text;

    @Column(name="post_date")
    private String postDate;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<PostKeyword> postKeywords = new HashSet<>();

    @ManyToOne
    @JoinColumn(name="appuser_id")
    @JsonIgnore
    private AppUser writer;

/*     @NotEmpty(message = "A post must have at least one keyword")
    @Size(min = 1, max = 250) */
    @Transient
    private String keywordInput; // väliaikainen kenttä avainsanoille 

    //kun luodaan post-olio, tallentuu sille automaattisesti sen hetkinen päivämäärä
    public Post() {
        this.postDate = LocalDate.now().toString();
    }

    //konstruktori otsikolla, sisällöllä, päivämäärällä ja kirjoittajalla
    public Post(String title, String text, String postDate, AppUser writer) {
        this.title = title;
        this.text = text;
        this.postDate = LocalDate.now().toString();
        this.writer = writer;
    }
    

    //konstruktori kaikilla attribuuteilla (paitsi id ja keywordinput)
    public Post(String title, String text, String postDate, Set<PostKeyword> postKeywords, AppUser writer) {
        this.title = title;
        this.text = text;
        this.postDate = LocalDate.now().toString();
        this.postKeywords = postKeywords;
        this.writer = writer;
    }

    

    public Post(String title, String text, String postDate, Set<PostKeyword> postKeywords, AppUser writer, String keywordInput) {
        this.title = title;
        this.text = text;
        this.postDate = postDate;
        this.postKeywords = postKeywords;
        this.writer = writer;
        this.keywordInput = keywordInput;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public Long getPostId() {
        return postId;
    }

    public String getTitle() {
        return title;
    }

    //käyttäjän antama otsikkosyöte trimmataan (=poistetaan tyhjät)
    //muutetaan ensimmäinen kirjain aina isoksi
    public void setTitle(String title) {
        this.title = title.trim();
        this.title = title.substring(0, 1).toUpperCase() + title.substring(1);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPostDate() {
        return postDate;
    }

    //post-olion päivämääräksi automaattisesti olion luomispäivän pvm (merkkijonomuodossa jotta voi tarvittaessa muotoilla)
    public void setPostDate(String postDate) {
        this.postDate = LocalDate.now().toString();
    }

    public Set<PostKeyword> getPostKeywords() {
        return postKeywords;
    }

    public void setPostKeywords(Set<PostKeyword> postKeywords) {
        this.postKeywords = postKeywords;
    }

    public String getKeywordInput() {
        return keywordInput;
    }

    public void setKeywordInput(String keywordInput) {
        this.keywordInput = keywordInput;
    } 

    public AppUser getWriter() {
        return writer;
    }

    public void setWriter(AppUser writer) {
        this.writer = writer;
    }

    public List<String> getKeywordsAsStringList() {
        List<String> keywordsList = new ArrayList<>();
            for (PostKeyword pk : postKeywords) {
                keywordsList.add(pk.getKeyword().getStrKeyword());
            }
        return keywordsList;
    }

    public String getKeywordsAsString() {
        List<String> keywords = new ArrayList<>();
            for (PostKeyword pk : postKeywords) {
                keywords.add(pk.getKeyword().getStrKeyword());
            }
        return String.join(", ", keywords);
    }
}
