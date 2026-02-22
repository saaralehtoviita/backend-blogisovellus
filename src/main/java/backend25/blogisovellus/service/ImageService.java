package backend25.blogisovellus.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import backend25.blogisovellus.domain.Image;
import backend25.blogisovellus.domain.ImageRepository;
import backend25.blogisovellus.domain.Post;

//service-luokka kuvien tallentamista varten
@Service
public class ImageService {

    @Autowired
    private ImageRepository iRepo;
    
    //metodi kuvien tallentamista varten
    //multipartfile = springin rajapinta, edustaa yhtä tiedostoa
    //html-lomakkeelta lähetetään "file" tyyppinä (input type="file")
    public void saveImages(List<MultipartFile> files, Post post) throws IOException {
        for (MultipartFile f : files) {
            if (!f.isEmpty()) {
                Image img = new Image();
                img.setFileName(f.getOriginalFilename()); //palauttaa tiedoston alkuperäisen nimen, esim "kuva.jpg"
                img.setContentType(f.getContentType()); //palauttaa tiedoston MIME-tyypin
                img.setData(f.getBytes()); //sisältö tavuina, tarvitaan tallennettaessa tietokantaan
                img.setPost(post);
                iRepo.save(img);
            }
        }
    }

    public ResponseEntity<byte[]> getImage(Long imgId) {
        Optional<Image> image = iRepo.findById(imgId);
        if (image.isPresent()) {
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, image.get().getContentType())
                    .body(image.get().getData());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
