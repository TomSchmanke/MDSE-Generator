package TODO;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import TODO;
import TODO;

@RestController
public class FahrzeugController {

    @Autowired FahrzeugRepository repository;

    @GetMapping("/fahrzeugs")
    public List<Fahrzeug> GetAll(){
        return repository.findAll();
    }

    @GetMapping("/fahrzeugs/{id}")
    public Fahrzeug get(@PathVariable int id) {
        Optional<Fahrzeug> res = repository.find(id);
        if(res.isPresent())
            return res.get();
        return null;
    }

    @DeleteMapping("/fahrzeugs/{id}")
    public void delete(@PathVariable int id) {
        repository.deleteById(id);
    }

    @PostMapping("/fahrzeugs")
    public ResponseEntity<Fahrzeug> Create(@RequestBody Fahrzeug subject) {
        subject.setPasswort(passwordEncoder.encode(subject.getPasswort()));
        Benutzer savedSubject = repository.save(subject);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(savedSubject.getId()).toUri();
        return ResponseEntity.created(location).body(savedSubject);
    }

    @PutMapping("/fahrzeugs/{id}")
    public ResponseEntity<Fahrzeug> Update(@RequestBody Fahrzeug subject, @PathVariable int id) {
        Optional<Fahrzeug> res = repository.findById(id);
            if (!res.isPresent())
            return ResponseEntity.notFound().build();

            if (!subject.getPasswort().equals(res.get().getPasswort())) {
            subject.setPasswort(passwordEncoder.encode(subject.getPasswort()));
            }
            subject.setId(id);
            repository.save(subject);

            return ResponseEntity.noContent().build();
    }

}
