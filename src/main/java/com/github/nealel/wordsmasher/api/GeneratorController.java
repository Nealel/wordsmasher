package com.github.nealel.wordsmasher.api;

import com.github.nealel.wordsmasher.corpus.FileCorpusLoader;
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
    private FileCorpusLoader fileCorpusLoader;

    @PostMapping("/names")
    public List<String> getNames(@RequestBody BatchRequestDto request) throws ExecutionException {
        return generator.generateBatch(request);
    }

    @GetMapping("/corpuses")
    public List<String> getCorpuses() throws IOException {
        return fileCorpusLoader.getAvailableFiles();
    }
}
