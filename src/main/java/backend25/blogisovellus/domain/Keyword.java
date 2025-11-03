package backend25.blogisovellus.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="KEYWORDS")
public class Keyword {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="keyword_id")
    private Long keywordId;

    @Column(name="ste_keyword")
    private String strKeyword;

    public Keyword() {}

    public Keyword(String strKeyword) {
        this.strKeyword = strKeyword.trim().toLowerCase();
    }

    public Long getKeywordId() {
        return keywordId;
    }

    public void setKeywordId(Long keywordId) {
        this.keywordId = keywordId;
    }

    public String getStrKeyword() {
        return strKeyword;
    }

    public void setStrKeyword(String strKeyword) {
        this.strKeyword = strKeyword.trim().toLowerCase();
    }

    @Override
    public String toString() {
        return strKeyword;
    }

}
