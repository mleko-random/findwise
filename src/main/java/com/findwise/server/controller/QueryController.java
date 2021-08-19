package com.findwise.server.controller;

import com.findwise.IndexEntry;
import com.findwise.SearchEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class QueryController {

    private final SearchEngine engine;

    @Autowired
    public QueryController(SearchEngine engine) {
        this.engine = engine;
    }

    @GetMapping("/query")
    public List<IndexEntry> query(@RequestParam String term) {
        return engine.search(term);
    }
}
