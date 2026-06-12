package com.ssemi.datapersistence.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ssemi.datapersistence.model.Sample;

import java.io.IOException;
import java.util.List;

public class SampleRepository {
    private final JsonFileRepository<Sample> fileRepo;

    public SampleRepository(String filePath) {
        this.fileRepo = new JsonFileRepository<>(filePath,
                new TypeReference<List<Sample>>() {});
    }

    public List<Sample> findAll() throws IOException {
        return fileRepo.readAll();
    }

    public void saveAll(List<Sample> samples) throws IOException {
        fileRepo.writeAll(samples);
    }
}