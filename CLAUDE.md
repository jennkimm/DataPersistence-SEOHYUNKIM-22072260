# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

S-Semi 반도체 시료(Sample) 데이터를 JSON 파일로 영속화하는 CRUD 콘솔 애플리케이션 PoC.

- **언어:** Java 21
- **빌드:** Gradle 8.7
- **IDE:** IntelliJ IDEA
- **Remote:** git@github.com:jennkimm/DataPersistence-SEOHYUNKIM-22072260.git

## Project Requirements
- 데이터를 JSON 파일로 관리하는 CRUD(Create, Read, Update, Delete) 콘솔 애플리케이션을 PoC로 개발한다.
- PoC에서 사용된 코드 구조를 유지한 상태로 CRUD를 구현한다.
- CRUD 기능:
  - Create: 새로운 데이터를 입력 받아 JSON 파일에 저장
  - Read: 전체 목록 보기 및 특정 ID/키 값으로 검색
  - Update: 기존 데이터를 선택하여 특정 필드 수정
  - Delete: 특정 데이터를 안전하게 삭제
- Regression Test와 Safety Test (무작위 데이터로 안정성을 테스트) 를 작성한다.

## Architecture

```
src/main/java/com/ssemi/datapersistence/
  model/Sample.java                  ← 시료 도메인 모델 (id, name, averageProductionTime, yieldRate, inventory)
  repository/JsonFileRepository.java ← 제네릭 JSON 파일 읽기/쓰기 (Jackson)
  repository/SampleRepository.java   ← Sample 특화 저장소
  service/SampleService.java         ← CRUD 비즈니스 로직
  view/ConsoleView.java              ← 콘솔 출력 유틸
  App.java                           ← 메인 진입점, 메뉴 루프

src/test/java/com/ssemi/datapersistence/
  service/SampleServiceTest.java     ← Regression Tests (9건)
  safety/SampleServiceSafetyTest.java ← Safety Tests (5건, 무작위 데이터)
```

데이터는 프로젝트 루트의 `samples.json`에 저장된다.

## Build & Run

> **Windows에서 gradlew 실행 시** JAVA_HOME을 명시해야 할 수 있다:
> ```powershell
> $env:JAVA_HOME = "C:\Users\User\.gradle\jdks\eclipse_adoptium-21-amd64-windows.2"
> $env:PATH = "$env:JAVA_HOME\bin;" + $env:PATH
> ```

```bash
./gradlew compileJava                          # 컴파일
./gradlew test                                 # 전체 테스트 (14건)
./gradlew test --tests "*.SampleServiceTest"   # Regression 테스트만
./gradlew test --tests "*.SampleServiceSafetyTest"  # Safety 테스트만
./gradlew run                                  # 콘솔 앱 실행
```

IntelliJ IDEA에서는 `App.java`의 `main` 메서드를 직접 실행해도 된다.

## File Encoding

Java 소스 파일 작성 시 반드시 **UTF-8 without BOM**을 사용하라. PowerShell의 `Set-Content`는 BOM을 추가하므로 다음 방법을 사용한다:

```powershell
[System.IO.File]::WriteAllText("경로", $content, [System.Text.UTF8Encoding]::new($false))
```
