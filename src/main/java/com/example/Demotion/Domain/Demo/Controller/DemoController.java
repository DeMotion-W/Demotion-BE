package com.example.Demotion.Domain.Demo.Controller;

import com.example.Demotion.Domain.Demo.Entity.Demo;
import com.example.Demotion.Domain.Demo.Repository.DemoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/demo")
@RequiredArgsConstructor
public class DemoController {

    private final DemoRepository demoRepository;

    @PostMapping
    public ResponseEntity<Demo> createDemo(@RequestBody Demo demo) {
        Demo saved = demoRepository.save(demo);
        return ResponseEntity.ok(saved);
    }
}
