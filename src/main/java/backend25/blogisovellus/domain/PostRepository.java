package backend25.blogisovellus.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findPostByWriterUserName(String userName);

}
