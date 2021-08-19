package com.findwise.search;

import com.findwise.IndexEntry;
import com.findwise.search.IndexEntryImpl;
import com.findwise.search.SearchEngineImpl;
import com.findwise.search.Tokenizer;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SearchEngineImplTest {

    @Test
    void indexing() {
        var engine = new SearchEngineImpl(new Tokenizer());
        engine.indexDocument("document 1", "the brown fox jumped over the brown dog");
        engine.indexDocument("document 2", "the lazy brown dog sat in the corner");
        engine.indexDocument("document 3", "the red fox bit the lazy dog");

        var result = engine.search("brown").stream().map(IndexEntry::getId).toList();
        assertEquals(result, List.of("document 1", "document 2"));
    }

    @Test
    void scoreCalculation() {
        var engine = new SearchEngineImpl(new Tokenizer());
        engine.indexDocument("1", "this is a sample a");
        engine.indexDocument("2", "example this is another example another example");
        var result = engine.search("example");
        assertEquals(result, List.of(new IndexEntryImpl("2", 0.12901285528456335)));
    }

    @Test
    void sorting() {
        var engine = new SearchEngineImpl(new Tokenizer());
        engine.indexDocument("document 1", "the brown fox jumped over the brown dog");
        engine.indexDocument("document 2", "the lazy brown dog sat in the corner");
        engine.indexDocument("document 3", "the red fox bit the lazy dog");
        var result = engine.search("fox").stream().map(IndexEntry::getId).toList();
        assertEquals(result, List.of("document 3", "document 1"));
    }

    @Test
    void reIndexing() {
        var engine = new SearchEngineImpl(new Tokenizer());
        engine.indexDocument("1", "this is a sample");
        assertEquals(
                engine.search("sample").stream().map(IndexEntry::getId).toList(),
                List.of("1")
        );

        engine.indexDocument("1", "this is an example");
        assertEquals(
                engine.search("example").stream().map(IndexEntry::getId).toList(),
                List.of("1")
        );
        assertEquals(
                engine.search("sample").stream().map(IndexEntry::getId).toList(),
                List.of()
        );
    }

}