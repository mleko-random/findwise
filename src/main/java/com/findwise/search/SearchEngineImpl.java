package com.findwise.search;

import com.findwise.IndexEntry;
import com.findwise.SearchEngine;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SearchEngineImpl implements SearchEngine {

    private final HashMap<String, List<ForwardEntry>> forwardIndex = new HashMap<>();
    private final HashMap<String, List<ReverseEntry>> reverseIndex = new HashMap<>();
    private final Tokenizer tokenizer;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public SearchEngineImpl(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
    }

    @Override
    public void indexDocument(String id, String content) {
        if (null == id || null == content) {
            throw new IllegalArgumentException("`id` and `content` cannot be null");
        }
        lock.writeLock().lock();
        try {
            if (forwardIndex.containsKey(id)) {
                removeDocument(id);
            }
            List<String> tokens = tokenizer.tokenize(content);
            List<ForwardEntry> forwardEntries = addToForwardIndex(id, tokens);
            addToReverseIndex(id, forwardEntries, tokens.size());
        } finally {
            lock.writeLock().unlock();
        }
    }

    private void removeDocument(String id) {
        List<ForwardEntry> terms = forwardIndex.get(id);
        for (var entry : terms) {
            reverseIndex.get(entry.term()).removeIf(reverseEntry -> reverseEntry.documentId().equals(id));
            if (reverseIndex.get(entry.term()).size() == 0) {
                reverseIndex.remove(entry.term());
            }
        }
    }

    private List<ForwardEntry> addToForwardIndex(String id, List<String> tokens) {
        HashMap<String, Integer> tokenOccurrences = new HashMap<>();
        for (String token : tokens) {
            tokenOccurrences.put(token, tokenOccurrences.getOrDefault(token, 0) + 1);
        }
        List<ForwardEntry> forwardEntryList = new ArrayList<>();
        for (var entry : tokenOccurrences.entrySet()) {
            forwardEntryList.add(new ForwardEntry(entry.getKey(), entry.getValue()));
        }
        forwardIndex.put(id, forwardEntryList);
        return forwardEntryList;
    }

    private void addToReverseIndex(String id, List<ForwardEntry> forwardEntries, long termCount) {
        for (var entry : forwardEntries) {
            if (!reverseIndex.containsKey(entry.term())) {
                reverseIndex.put(entry.term(), new ArrayList<>());
            }
            reverseIndex.get(entry.term()).add(
                    new ReverseEntry(id, (double) entry.occurrences() / (double) termCount)
            );
        }
    }

    @Override
    public List<IndexEntry> search(String term) {
        List<IndexEntry> resultList = new ArrayList<>();
        lock.readLock().lock();
        try {
            if (!reverseIndex.containsKey(term)) {
                return resultList;
            }
            List<ReverseEntry> termEntries = reverseIndex.get(term);
            double inverseDocumentFrequency = Math.log10((double) forwardIndex.size() / (double) termEntries.size());
            for (var entry : termEntries) {
                var indexEntry = new IndexEntryImpl(entry.documentId(), entry.termFrequency() * inverseDocumentFrequency);
                resultList.add(indexEntry);
            }
        } finally {
            lock.readLock().unlock();
        }
        resultList.sort(Comparator.comparingDouble(IndexEntry::getScore).reversed());
        return resultList;
    }
}
