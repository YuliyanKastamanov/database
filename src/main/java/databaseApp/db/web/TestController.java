package databaseApp.db.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@CrossOrigin("*")
@RestController
@RequestMapping(value = "/test")
public class TestController {




    @PostMapping()
    public ResponseEntity<String > test() {

        return new ResponseEntity<>("Test completed!", HttpStatus.CREATED);
    }
}




