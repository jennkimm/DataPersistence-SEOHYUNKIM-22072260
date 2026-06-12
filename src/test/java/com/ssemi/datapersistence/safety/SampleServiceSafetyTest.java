package com.ssemi.datapersistence.safety;

import com.ssemi.datapersistence.model.Sample;
import com.ssemi.datapersistence.repository.SampleRepository;
import com.ssemi.datapersistence.service.SampleService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SampleServiceSafetyTest {
    private Path tempFile;
    private SampleService service;
    private static final Random RANDOM = new Random(42L);

    @BeforeEach
    void setUp() throws IOException {
        tempFile = Files.createTempFile("samples-safety-", ".json");
        service = new SampleService(new SampleRepository(tempFile.toString()));
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(tempFile);
    }

    @Test
    void safety_무작위100건_등록후_전체_개수가_일치한다() throws IOException {
        for (int i = 0; i < 100; i++) {
            service.create(randomSample("S" + i));
        }

        assertEquals(100, service.findAll().size());
    }

    @Test
    void safety_등록후_일부_삭제하면_나머지_개수가_정확하다() throws IOException {
        int total = 50;
        for (int i = 0; i < total; i++) {
            service.create(randomSample("D" + i));
        }

        int deleteCount = 20;
        for (int i = 0; i < deleteCount; i++) {
            service.delete("D" + i);
        }

        assertEquals(total - deleteCount, service.findAll().size());
    }

    @Test
    void safety_존재하지않는ID_반복삭제시_예외만_발생하고_기존데이터_보존된다()
            throws IOException {
        service.create(randomSample("KEEP001"));

        for (int i = 0; i < 20; i++) {
            final String randomId = UUID.randomUUID().toString();
            assertThrows(IllegalArgumentException.class,
                    () -> service.delete(randomId));
        }

        assertEquals(1, service.findAll().size());
    }

    @Test
    void safety_무작위_업데이트_반복후_최종값이_정확히_저장된다() throws IOException {
        service.create(randomSample("U001"));
        Sample last = null;
        for (int i = 0; i < 30; i++) {
            last = randomSample("U001");
            service.update("U001", last);
        }

        Sample found = service.findById("U001").orElseThrow();
        assertEquals(last.getName(), found.getName());
        assertEquals(last.getInventory(), found.getInventory());
    }

    @Test
    void safety_대량등록후_전체조회_결과가_손실없이_반환된다() throws IOException {
        int count = 200;
        for (int i = 0; i < count; i++) {
            service.create(randomSample("BULK" + i));
        }

        List<Sample> all = service.findAll();
        assertEquals(count, all.size());
        assertTrue(all.stream().allMatch(s -> s.getId() != null && s.getName() != null));
    }

    private Sample randomSample(String id) {
        return new Sample(
                id,
                "시료_" + RANDOM.nextInt(10000),
                RANDOM.nextInt(120) + 1,
                0.5 + RANDOM.nextDouble() * 0.5,
                RANDOM.nextInt(1000)
        );
    }
}