package com.github.nealel.wordsmasher.api;

import com.github.nealel.wordsmasher.generator.BatchGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class GeneratorController {

    @Autowired
    private BatchGenerator generator;

    @GetMapping("/names")
    public List<String> getNames(@RequestBody BatchRequestDto request) {
        return generator.generateBatch(request);
    }
}
