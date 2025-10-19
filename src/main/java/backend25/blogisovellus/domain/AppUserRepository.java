package backend25.blogisovellus.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    AppUser findByUserName(String userName);
    AppUser findByLastName(String lastName);

}
