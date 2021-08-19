package com.findwise.server.controller;

import com.findwise.SearchEngine;
import com.findwise.server.request.IndexDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DocumentController {

    private final SearchEngine engine;

    @Autowired
    public DocumentController(SearchEngine engine) {
        this.engine = engine;
    }

    @PostMapping("/document")
    public boolean addDocument(@RequestBody IndexDocument document) {
        engine.indexDocument(document.id(), document.content());
        return true;
    }
}
