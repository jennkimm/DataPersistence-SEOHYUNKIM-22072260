package com.ssemi.datapersistence.service;

import com.ssemi.datapersistence.model.Sample;
import com.ssemi.datapersistence.repository.SampleRepository;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class SampleService {
    private final SampleRepository repository;

    public SampleService(SampleRepository repository) {
        this.repository = repository;
    }

    public void create(Sample sample) throws IOException {
        List<Sample> samples = repository.findAll();
        boolean duplicate = samples.stream()
                .anyMatch(s -> s.getId().equals(sample.getId()));
        if (duplicate) {
            throw new IllegalArgumentException("이미 존재하는 ID: " + sample.getId());
        }
        samples.add(sample);
        repository.saveAll(samples);
    }

    public List<Sample> findAll() throws IOException {
        return repository.findAll();
    }

    public Optional<Sample> findById(String id) throws IOException {
        return repository.findAll().stream()
                .filter(s -> s.getId().equals(id))
                .findFirst();
    }

    public void update(String id, Sample updated) throws IOException {
        List<Sample> samples = repository.findAll();
        boolean found = false;
        for (int i = 0; i < samples.size(); i++) {
            if (samples.get(i).getId().equals(id)) {
                samples.set(i, updated);
                found = true;
                break;
            }
        }
        if (!found) throw new IllegalArgumentException("존재하지 않는 ID: " + id);
        repository.saveAll(samples);
    }

    public void delete(String id) throws IOException {
        List<Sample> samples = repository.findAll();
        boolean removed = samples.removeIf(s -> s.getId().equals(id));
        if (!removed) throw new IllegalArgumentException("존재하지 않는 ID: " + id);
        repository.saveAll(samples);
    }
}