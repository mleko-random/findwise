package com.findwise.server;

import com.findwise.SearchEngine;
import com.findwise.search.SearchEngineImpl;
import com.findwise.search.Tokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    Tokenizer tokenizer(){
        return new Tokenizer();
    }

    @Bean
    @Autowired
    SearchEngine searchEngine(Tokenizer tokenizer){
        return new SearchEngineImpl(tokenizer);
    }
}
