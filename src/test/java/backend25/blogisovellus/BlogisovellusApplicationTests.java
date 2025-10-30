package backend25.blogisovellus;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import backend25.blogisovellus.web.PostController;
import backend25.blogisovellus.web.PostRestController;

@SpringBootTest
class BlogisovellusApplicationTests {

	//testaa, että sovellus käynnistyy
	@Test
	void contextLoads() {
	}

	@Autowired
	private PostController controller;

	@Test
	public void postControllerLoads() throws Exception {
		assertThat(controller).isNotNull();
	}

	@Autowired
	private PostRestController restController;

	@Test
	public void restControllerLoads() throws Exception {
		assertThat(restController).isNotNull();
	}

}
