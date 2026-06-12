# DataPersistence PoC 구현 계획

> **에이전트 작업자용:** 필수 서브스킬: superpowers:subagent-driven-development (권장) 또는 superpowers:executing-plans를 사용하여 이 계획을 작업 단위로 구현할 것. 단계는 체크박스(`- [ ]`) 구문으로 진행 상황을 추적한다.

**목표:** S-Semi 반도체 시료 데이터를 JSON 파일로 영속화하는 CRUD 콘솔 애플리케이션 PoC를 TDD로 구현한다.

**아키텍처:** `JsonFileRepository`가 제네릭 JSON 읽기/쓰기를 담당하고, `SampleRepository`가 도메인 특화 저장소 역할을 하며, `SampleService`가 CRUD 비즈니스 로직을 제공한다. `App`은 메뉴 루프로 사용자 입력을 처리한다.

**기술 스택:** Java 21, Maven, Jackson Databind 2.17.1, JUnit 5.11.4

---

## 파일 목록

| 역할 | 경로 |
|------|------|
| 빌드 설정 | `pom.xml` |
| 도메인 모델 | `src/main/java/com/ssemi/datapersistence/model/Sample.java` |
| 제네릭 저장소 | `src/main/java/com/ssemi/datapersistence/repository/JsonFileRepository.java` |
| 시료 저장소 | `src/main/java/com/ssemi/datapersistence/repository/SampleRepository.java` |
| 서비스 | `src/main/java/com/ssemi/datapersistence/service/SampleService.java` |
| 콘솔 출력 | `src/main/java/com/ssemi/datapersistence/view/ConsoleView.java` |
| 메인 | `src/main/java/com/ssemi/datapersistence/App.java` |
| Regression 테스트 | `src/test/java/com/ssemi/datapersistence/service/SampleServiceTest.java` |
| Safety 테스트 | `src/test/java/com/ssemi/datapersistence/safety/SampleServiceSafetyTest.java` |

---

### 작업 1: Maven 프로젝트 구조 설정

**파일:**
- 생성: `pom.xml`
- 생성: `src/main/java/com/ssemi/datapersistence/.gitkeep` (디렉터리 확보용)
- 생성: `src/test/java/com/ssemi/datapersistence/.gitkeep`

- [ ] **단계 1: 디렉터리 구조 생성**

```powershell
mkdir src\main\java\com\ssemi\datapersistence\model
mkdir src\main\java\com\ssemi\datapersistence\repository
mkdir src\main\java\com\ssemi\datapersistence\service
mkdir src\main\java\com\ssemi\datapersistence\view
mkdir src\test\java\com\ssemi\datapersistence\service
mkdir src\test\java\com\ssemi\datapersistence\safety
```

- [ ] **단계 2: pom.xml 작성**

`pom.xml` 전체 내용:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
             http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.ssemi</groupId>
    <artifactId>data-persistence</artifactId>
    <version>1.0-SNAPSHOT</version>

    <properties>
        <maven.compiler.source>21</maven.compiler.source>
        <maven.compiler.target>21</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

    <dependencies>
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
            <version>2.17.1</version>
        </dependency>
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter</artifactId>
            <version>5.10.2</version>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
                <version>3.2.5</version>
            </plugin>
        </plugins>
    </build>
</project>
```

- [ ] **단계 3: 의존성 다운로드 확인**

```powershell
mvn dependency:resolve
```

기대 결과: `BUILD SUCCESS`

- [ ] **단계 4: 커밋**

```bash
git add pom.xml
git commit -m "chore: Maven 프로젝트 초기 설정 (Jackson + JUnit 5)"
```

---

### 작업 2: Sample 도메인 모델

**파일:**
- 생성: `src/main/java/com/ssemi/datapersistence/model/Sample.java`

- [ ] **단계 1: Sample 클래스 작성**

`src/main/java/com/ssemi/datapersistence/model/Sample.java`:

```java
package com.ssemi.datapersistence.model;

public class Sample {
    private String id;
    private String name;
    private int averageProductionTime;  // 단위: 분
    private double yieldRate;           // 0.0 ~ 1.0
    private int inventory;

    public Sample() {}

    public Sample(String id, String name, int averageProductionTime,
                  double yieldRate, int inventory) {
        this.id = id;
        this.name = name;
        this.averageProductionTime = averageProductionTime;
        this.yieldRate = yieldRate;
        this.inventory = inventory;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getAverageProductionTime() { return averageProductionTime; }
    public void setAverageProductionTime(int averageProductionTime) {
        this.averageProductionTime = averageProductionTime;
    }

    public double getYieldRate() { return yieldRate; }
    public void setYieldRate(double yieldRate) { this.yieldRate = yieldRate; }

    public int getInventory() { return inventory; }
    public void setInventory(int inventory) { this.inventory = inventory; }
}
```

- [ ] **단계 2: 컴파일 확인**

```powershell
mvn compile
```

기대 결과: `BUILD SUCCESS`

- [ ] **단계 3: 커밋**

```bash
git add src/main/java/com/ssemi/datapersistence/model/Sample.java
git commit -m "feat: Sample 도메인 모델 추가"
```

---

### 작업 3: JsonFileRepository (제네릭 JSON 파일 저장소)

**파일:**
- 생성: `src/main/java/com/ssemi/datapersistence/repository/JsonFileRepository.java`
- 생성: `src/main/java/com/ssemi/datapersistence/repository/SampleRepository.java`

- [ ] **단계 1: JsonFileRepository 작성**

`src/main/java/com/ssemi/datapersistence/repository/JsonFileRepository.java`:

```java
package com.ssemi.datapersistence.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JsonFileRepository<T> {
    private final File file;
    private final ObjectMapper mapper = new ObjectMapper();
    private final TypeReference<List<T>> typeRef;

    public JsonFileRepository(String filePath, TypeReference<List<T>> typeRef) {
        this.file = new File(filePath);
        this.typeRef = typeRef;
    }

    public List<T> readAll() throws IOException {
        if (!file.exists() || file.length() == 0) return new ArrayList<>();
        return mapper.readValue(file, typeRef);
    }

    public void writeAll(List<T> items) throws IOException {
        mapper.writerWithDefaultPrettyPrinter().writeValue(file, items);
    }
}
```

- [ ] **단계 2: SampleRepository 작성**

`src/main/java/com/ssemi/datapersistence/repository/SampleRepository.java`:

```java
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
```

- [ ] **단계 3: 컴파일 확인**

```powershell
mvn compile
```

기대 결과: `BUILD SUCCESS`

- [ ] **단계 4: 커밋**

```bash
git add src/main/java/com/ssemi/datapersistence/repository/
git commit -m "feat: JsonFileRepository, SampleRepository 추가"
```

---

### 작업 4: SampleService — Create (TDD)

**파일:**
- 생성: `src/main/java/com/ssemi/datapersistence/service/SampleService.java`
- 생성: `src/test/java/com/ssemi/datapersistence/service/SampleServiceTest.java`

- [ ] **단계 1: 실패하는 테스트 작성**

`src/test/java/com/ssemi/datapersistence/service/SampleServiceTest.java`:

```java
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
```

- [ ] **단계 2: 테스트를 실행하여 실패 확인**

```powershell
mvn test -Dtest=SampleServiceTest
```

기대 결과: `FAIL` — `SampleService` 클래스가 존재하지 않아 컴파일 에러

- [ ] **단계 3: SampleService 스켈레톤 + create 구현**

`src/main/java/com/ssemi/datapersistence/service/SampleService.java`:

```java
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
```

- [ ] **단계 4: 테스트를 실행하여 통과 확인**

```powershell
mvn test -Dtest=SampleServiceTest#create_저장하면_전체목록에서_조회된다+create_중복ID이면_예외를_던진다
```

기대 결과: `PASS` 2건

- [ ] **단계 5: 커밋**

```bash
git add src/main/java/com/ssemi/datapersistence/service/SampleService.java
git add src/test/java/com/ssemi/datapersistence/service/SampleServiceTest.java
git commit -m "feat: SampleService Create 구현 및 테스트"
```

---

### 작업 5: SampleService — Read (TDD)

**파일:**
- 수정: `src/test/java/com/ssemi/datapersistence/service/SampleServiceTest.java`

- [ ] **단계 1: Read 테스트 추가 (기존 파일에 append)**

`SampleServiceTest.java`에 다음 테스트 메서드를 클래스 내부에 추가:

```java
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
```

- [ ] **단계 2: 테스트를 실행하여 통과 확인**

`findById`와 `findAll`은 작업 4에서 이미 구현됨.

```powershell
mvn test -Dtest=SampleServiceTest
```

기대 결과: `PASS` 5건

- [ ] **단계 3: 커밋**

```bash
git add src/test/java/com/ssemi/datapersistence/service/SampleServiceTest.java
git commit -m "test: SampleService Read 테스트 추가"
```

---

### 작업 6: SampleService — Update / Delete (TDD)

**파일:**
- 수정: `src/test/java/com/ssemi/datapersistence/service/SampleServiceTest.java`

- [ ] **단계 1: Update / Delete 테스트 추가**

`SampleServiceTest.java` 클래스 내부에 추가:

```java
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
```

- [ ] **단계 2: 테스트를 실행하여 통과 확인**

```powershell
mvn test -Dtest=SampleServiceTest
```

기대 결과: `PASS` 9건

- [ ] **단계 3: 커밋**

```bash
git add src/test/java/com/ssemi/datapersistence/service/SampleServiceTest.java
git commit -m "test: SampleService Update/Delete 테스트 추가"
```

---

### 작업 7: Safety Tests (무작위 데이터 안정성 테스트)

**파일:**
- 생성: `src/test/java/com/ssemi/datapersistence/safety/SampleServiceSafetyTest.java`

- [ ] **단계 1: Safety 테스트 작성**

`src/test/java/com/ssemi/datapersistence/safety/SampleServiceSafetyTest.java`:

```java
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
```

- [ ] **단계 2: 테스트를 실행하여 실패 확인 (아직 구현부 없음)**

이미 `SampleService`가 구현되어 있으므로, 실행해서 통과 확인:

```powershell
mvn test -Dtest=SampleServiceSafetyTest
```

기대 결과: `PASS` 5건

- [ ] **단계 3: 커밋**

```bash
git add src/test/java/com/ssemi/datapersistence/safety/SampleServiceSafetyTest.java
git commit -m "test: Safety 테스트 추가 (무작위 데이터 안정성 검증)"
```

---

### 작업 8: ConsoleView + App 메뉴 루프

**파일:**
- 생성: `src/main/java/com/ssemi/datapersistence/view/ConsoleView.java`
- 생성: `src/main/java/com/ssemi/datapersistence/App.java`

- [ ] **단계 1: ConsoleView 작성**

`src/main/java/com/ssemi/datapersistence/view/ConsoleView.java`:

```java
package com.ssemi.datapersistence.view;

import com.ssemi.datapersistence.model.Sample;

import java.util.List;

public class ConsoleView {

    public void printMenu() {
        System.out.println("\n===== S-Semi 시료 관리 시스템 =====");
        System.out.println("1. 시료 등록 (Create)");
        System.out.println("2. 전체 목록 조회 (Read All)");
        System.out.println("3. ID로 검색 (Find by ID)");
        System.out.println("4. 시료 수정 (Update)");
        System.out.println("5. 시료 삭제 (Delete)");
        System.out.println("0. 종료");
        System.out.print("선택 > ");
    }

    public void printSample(Sample s) {
        System.out.printf("  [%s] %s  |  생산시간: %d분  |  수율: %.2f  |  재고: %d%n",
                s.getId(), s.getName(),
                s.getAverageProductionTime(), s.getYieldRate(), s.getInventory());
    }

    public void printSamples(List<Sample> samples) {
        if (samples.isEmpty()) {
            System.out.println("  등록된 시료가 없습니다.");
            return;
        }
        samples.forEach(this::printSample);
    }

    public void printError(String message) {
        System.out.println("[오류] " + message);
    }

    public void printSuccess(String message) {
        System.out.println("[완료] " + message);
    }
}
```

- [ ] **단계 2: App 메인 클래스 작성**

`src/main/java/com/ssemi/datapersistence/App.java`:

```java
package com.ssemi.datapersistence;

import com.ssemi.datapersistence.model.Sample;
import com.ssemi.datapersistence.repository.SampleRepository;
import com.ssemi.datapersistence.service.SampleService;
import com.ssemi.datapersistence.view.ConsoleView;

import java.io.IOException;
import java.util.Optional;
import java.util.Scanner;

public class App {

    public static void main(String[] args) throws IOException {
        SampleService service = new SampleService(
                new SampleRepository("samples.json"));
        ConsoleView view = new ConsoleView();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            view.printMenu();
            String choice = scanner.nextLine().trim();
            try {
                switch (choice) {
                    case "1" -> handleCreate(service, scanner, view);
                    case "2" -> handleReadAll(service, view);
                    case "3" -> handleFindById(service, scanner, view);
                    case "4" -> handleUpdate(service, scanner, view);
                    case "5" -> handleDelete(service, scanner, view);
                    case "0" -> { System.out.println("종료합니다."); return; }
                    default  -> System.out.println("0~5 사이의 번호를 입력하세요.");
                }
            } catch (IllegalArgumentException e) {
                view.printError(e.getMessage());
            }
        }
    }

    private static void handleCreate(SampleService service,
                                     Scanner scanner, ConsoleView view) throws IOException {
        System.out.print("ID: "); String id = scanner.nextLine().trim();
        System.out.print("이름: "); String name = scanner.nextLine().trim();
        System.out.print("평균 생산시간(분): "); int time = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("수율(0.0~1.0): "); double yield = Double.parseDouble(scanner.nextLine().trim());
        System.out.print("재고 수량: "); int inv = Integer.parseInt(scanner.nextLine().trim());

        service.create(new Sample(id, name, time, yield, inv));
        view.printSuccess("시료 '" + id + "' 이(가) 등록되었습니다.");
    }

    private static void handleReadAll(SampleService service, ConsoleView view) throws IOException {
        view.printSamples(service.findAll());
    }

    private static void handleFindById(SampleService service,
                                       Scanner scanner, ConsoleView view) throws IOException {
        System.out.print("검색할 ID: "); String id = scanner.nextLine().trim();
        Optional<Sample> result = service.findById(id);
        if (result.isPresent()) {
            view.printSample(result.get());
        } else {
            view.printError("ID '" + id + "' 에 해당하는 시료가 없습니다.");
        }
    }

    private static void handleUpdate(SampleService service,
                                     Scanner scanner, ConsoleView view) throws IOException {
        System.out.print("수정할 ID: "); String id = scanner.nextLine().trim();
        System.out.print("새 이름: "); String name = scanner.nextLine().trim();
        System.out.print("새 평균 생산시간(분): "); int time = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("새 수율(0.0~1.0): "); double yield = Double.parseDouble(scanner.nextLine().trim());
        System.out.print("새 재고 수량: "); int inv = Integer.parseInt(scanner.nextLine().trim());

        service.update(id, new Sample(id, name, time, yield, inv));
        view.printSuccess("시료 '" + id + "' 이(가) 수정되었습니다.");
    }

    private static void handleDelete(SampleService service,
                                     Scanner scanner, ConsoleView view) throws IOException {
        System.out.print("삭제할 ID: "); String id = scanner.nextLine().trim();
        service.delete(id);
        view.printSuccess("시료 '" + id + "' 이(가) 삭제되었습니다.");
    }
}
```

- [ ] **단계 3: 전체 빌드 확인**

```powershell
mvn compile
```

기대 결과: `BUILD SUCCESS`

- [ ] **단계 4: 커밋**

```bash
git add src/main/java/com/ssemi/datapersistence/view/ConsoleView.java
git add src/main/java/com/ssemi/datapersistence/App.java
git commit -m "feat: ConsoleView 및 App 메뉴 루프 구현"
```

---

### 작업 9: 전체 테스트 실행 및 최종 검증

**파일:**
- 없음 (기존 파일 실행만)

- [ ] **단계 1: 전체 테스트 실행**

```powershell
mvn test
```

기대 결과: `Tests run: 14, Failures: 0, Errors: 0` (Regression 9건 + Safety 5건)

- [ ] **단계 2: 앱 실행 확인 (수동)**

```powershell
mvn compile exec:java -Dexec.mainClass="com.ssemi.datapersistence.App"
```

또는 IntelliJ IDEA에서 `App.java`의 `main` 메서드를 직접 실행한다.
메뉴가 출력되고, 1번으로 시료를 등록하면 `samples.json`이 생성되는지 확인한다.

- [ ] **단계 3: 최종 커밋**

```bash
git add .
git commit -m "chore: DataPersistence PoC 구현 완료"
```

---

## 체크리스트 — 스펙 커버리지

| 요구사항 | 구현 위치 |
|----------|-----------|
| JSON 파일 기반 데이터 관리 | `JsonFileRepository`, `SampleRepository` |
| Create | `SampleService.create()` + 작업 4 테스트 |
| Read (전체 목록) | `SampleService.findAll()` + 작업 5 테스트 |
| Read (ID 검색) | `SampleService.findById()` + 작업 5 테스트 |
| Update | `SampleService.update()` + 작업 6 테스트 |
| Delete | `SampleService.delete()` + 작업 6 테스트 |
| Regression Test | `SampleServiceTest` (작업 4~6) |
| Safety Test (무작위 안정성) | `SampleServiceSafetyTest` (작업 7) |
| 콘솔 애플리케이션 | `App.java` + `ConsoleView.java` (작업 8) |
