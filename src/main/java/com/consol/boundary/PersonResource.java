package com.consol.boundary;

import com.consol.control.PersonService;
import com.consol.entity.IdTO;
import com.consol.entity.PersonTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
public class PersonResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(PersonResource.class);

    private final PersonService personService;

    public PersonResource(final PersonService personService) {
        this.personService = personService;
    }

    @PostMapping(value = "/person", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<IdTO> addPerson(@RequestBody final PersonTO personTO) {
        LOGGER.info("Received POST: {}", personTO);
        return ResponseEntity.ok(personService.store(personTO));
    }

    @GetMapping(value = "/person/{uuid}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<PersonTO> getPerson(@PathVariable("uuid") final String uuid) {
        LOGGER.info("Received GET with uuid: {}", uuid);
        return ResponseEntity.ok(personService.getPersonById(uuid));
    }

    @GetMapping(value = "/person/random", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<PersonTO> getRandomPerson() {
        return ResponseEntity.ok(personService.getRandomPerson());
    }

    @GetMapping(value = "/person/failRandom", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<PersonTO> maybeFail() {
        final Optional<PersonTO> personTO = personService.maybeFail();
        return personTO.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.internalServerError().build());
    }
}
