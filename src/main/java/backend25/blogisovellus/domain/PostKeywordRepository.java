package backend25.blogisovellus.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PostKeywordRepository extends JpaRepository<PostKeyword, Long> {

    //postkeyword-olioiden poistaminen postauksen perusteella
    void deleteAllByPost(Post post);
    //postkeyword-olioiden hakeminen postauksen perusteella
    List<PostKeyword> findAllByPost(Post post);

}
