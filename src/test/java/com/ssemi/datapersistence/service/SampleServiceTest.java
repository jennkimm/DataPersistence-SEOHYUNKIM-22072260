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

    @Test
    void findById_존재하는ID이면_Optional로_반환한다() throws IOException {
        service.create(new Sample("S002", "시료B", 45, 0.88, 200));

        Optional<Sample> found = service.findById("S002");

        assertTrue(found.isPresent());
        assertEquals("시료B", found.get().getName());
    }

    @Test
    void findById_존재하지않는ID이면_빈_Optional을_반환한다() throws IOException {
        Optional<Sample> found = service.findById("NONE");

        assertTrue(found.isEmpty());
    }

    @Test
    void findAll_여러건_저장후_전체를_반환한다() throws IOException {
        service.create(new Sample("S010", "시료X", 30, 0.90, 50));
        service.create(new Sample("S011", "시료Y", 60, 0.85, 80));

        List<Sample> all = service.findAll();

        assertEquals(2, all.size());
    }

    @Test
    void update_존재하는ID이면_필드가_수정된다() throws IOException {
        service.create(new Sample("S003", "시료C", 60, 0.90, 100));

        service.update("S003", new Sample("S003", "수정된시료C", 90, 0.85, 150));

        Sample found = service.findById("S003").orElseThrow();
        assertEquals("수정된시료C", found.getName());
        assertEquals(150, found.getInventory());
    }

    @Test
    void update_존재하지않는ID이면_예외를_던진다() {
        assertThrows(IllegalArgumentException.class,
                () -> service.update("NONE",
                        new Sample("NONE", "없는시료", 60, 0.9, 0)));
    }

    @Test
    void delete_존재하는ID이면_목록에서_제거된다() throws IOException {
        service.create(new Sample("S004", "시료D", 30, 0.92, 50));

        service.delete("S004");

        assertTrue(service.findById("S004").isEmpty());
    }

    @Test
    void delete_존재하지않는ID이면_예외를_던진다() {
        assertThrows(IllegalArgumentException.class,
                () -> service.delete("NONE"));
    }
}