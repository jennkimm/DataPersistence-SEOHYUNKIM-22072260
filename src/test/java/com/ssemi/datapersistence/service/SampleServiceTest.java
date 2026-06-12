package com.ssemi.datapersistence.service;

import com.ssemi.datapersistence.model.Sample;
import com.ssemi.datapersistence.repository.SampleRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class SampleServiceTest {
    private Path tempFile;
    private SampleService service;

    @BeforeEach
    void setUp() throws IOException {
        tempFile = Files.createTempFile("samples-test-", ".json");
        service = new SampleService(new SampleRepository(tempFile.toString()));
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(tempFile);
    }

    @Test
    void create_저장하면_전체목록에서_조회된다() throws IOException {
        service.create(new Sample("S001", "테스트시료", 60, 0.95, 100));

        List<Sample> all = service.findAll();

        assertEquals(1, all.size());
        assertEquals("S001", all.get(0).getId());
    }

    @Test
    void create_중복ID이면_예외를_던진다() throws IOException {
        service.create(new Sample("S001", "시료A", 60, 0.95, 100));

        assertThrows(IllegalArgumentException.class,
                () -> service.create(new Sample("S001", "시료B", 30, 0.90, 50)));
    }
}