package com.github.nealel.wordsmasher.api.controllers;

import com.github.nealel.wordsmasher.api.dto.BatchRequestDto;
import com.github.nealel.wordsmasher.corpus.Corpuses;
import com.github.nealel.wordsmasher.generator.BatchGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
public class GeneratorController {

    @Autowired
    private BatchGenerator generator;
    @Autowired
    private Corpuses corpuses;

    /**
     * Generates and returns a list of names, according to the request's parameters
     */
    @PostMapping("/names")
    public List<String> getNames(@RequestBody BatchRequestDto request) throws ExecutionException {
        return generator.generateBatch(request);
    }

    /**
     * Lists the names of all available data source corpuses
     */
    @GetMapping("/corpuses")
    public List<String> getCorpuses() throws IOException {
        return corpuses.getAvailableCorpuses();
    }
}
