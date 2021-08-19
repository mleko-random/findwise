package com.findwise.search;

import java.util.Arrays;
import java.util.List;

public class Tokenizer {

    public List<String> tokenize(String text) {
        return Arrays.asList(text.split(" "));
    }
}
