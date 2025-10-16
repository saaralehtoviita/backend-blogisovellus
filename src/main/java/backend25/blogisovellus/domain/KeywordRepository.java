package backend25.blogisovellus.domain;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface KeywordRepository  extends JpaRepository<Keyword, Long> {

    
    //keywordin etsimiseen, palauttaa optionalin joka on keyword, jos sellainen löytyy reposta
    Optional<Keyword> findByStrKeyword(String strKeyword);
    
}
