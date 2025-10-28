package backend25.blogisovellus.domain;

import java.util.List;

//tämä luokka on rest-palveluita varten
//tallennetaan väliakaisesti dataa yksinkertaisessa muodossa
//toimii välittäjäluokkana - välitetään tiedot oikealle post-luokalle olion muodostamista varten
public class PostDTO {
 
    private String title;
    private String text;
    private String postDate;
    private String writerUsername;
    private List<String> keywords;
    
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
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
    public void setPostDate(String postDate) {
        this.postDate = postDate;
    }
    public String getWriterUsername() {
        return writerUsername;
    }
    public void setWriterUsername(String writerUsername) {
        this.writerUsername = writerUsername;
    }
    public List<String> getKeywords() {
        return keywords;
    }
    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    


}
