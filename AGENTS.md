# AGENTS.md

반도체 시료 생산주문관리 시스템 — AI 에이전트 작업 지침서

---

## 프로젝트 개요

가상의 반도체 회사 "S-Semi"의 시료(Sample) 생산·주문·재고·출고를 관리하는 콘솔 기반 시스템이다.
전체 구현은 아래 **4개의 PoC Repository**와 **1개의 메인 시스템 Repository**로 구성된다.

---

## PoC Repository 목록

| # | Repository 명 | 목적 | url                                                                |
|---|---|---|--------------------------------------------------------------------|
| 1 | `ConsoleMVC-SEOHYUNKIM-22072260` | MVC 패키지 구조 및 역할 분리 스켈레톤 | git@github.com:jennkimm/ConsoleMVC-SEOHYUNKIM-22072260.git         |
| 2 | `DataPersistence-SEOHYUNKIM-22072260` | 파일/JSON/DB 기반 데이터 영속성 + CRUD | git@github.com:jennkimm/DataPersistence-SEOHYUNKIM-22072260.git    
 |
| 3 | `DataMonitor-SEOHYUNKIM-22072260` | 저장된 데이터 실시간 콘솔 조회 도구 | git@github.com:jennkimm/DataMonitor-SEOHYUNKIM-22072260.git        
 |
| 4 | `DummyDataGenerator-SEOHYUNKIM-22072260` | 테스트용 Dummy 데이터 자동 생성·저장 도구 | git@github.com:jennkimm/DummyDataGenerator-SEOHYUNKIM-22072260.git |

## Main System
메인 시스템: `SampleOrderSystem-SEOHYUNKIM-22072260`

---

## 도메인 핵심 규칙

### 주문 상태 흐름

```
RESERVED → (승인, 재고 충분)  → CONFIRMED → RELEASE
         → (승인, 재고 부족)  → PRODUCING → CONFIRMED → RELEASE
         → (거절)             → REJECTED
```

- 모니터링에서 `REJECTED` 상태는 제외한다.
- 상태 전이는 위 흐름 외의 경로를 허용하지 않는다.

### 생산량·시간 계산 공식

```
실 생산량   = ceil(부족분 / (수율 × 0.9))
총 생산시간 = 평균생산시간 × 실 생산량
```

### 재고 상태 표기 (모니터링)

| 상태 | 조건 |
|------|------|
| 여유 | 주문 대비 재고 충분 |
| 부족 | 주문 대비 재고 부족 |
| 고갈 | 현재 재고 = 0 |

### 생산 스케줄링

생산 큐는 **FIFO** 구조다. 재고 부족 주문이 승인되면 자동으로 큐에 등록된다.

---

## 기능 범위 요약

| 메뉴 | 주요 기능 |
|------|-----------|
| 시료 관리 | 등록 (ID·이름·평균생산시간·수율), 전체 조회(재고 포함), 검색 |
| 주문 접수 | 시료 예약 → `RESERVED` 생성 (시료ID·고객명·수량 입력) |
| 주문 승인/거절 | RESERVED 목록 조회, 승인(재고 분기 자동처리), 거절 |
| 모니터링 | 상태별 주문량, 시료별 재고량·재고 상태 표기 |
| 생산 라인 | 생산 현황 조회, 생산 큐 확인, 생산 완료 처리 |
| 출고 처리 | CONFIRMED 주문 출고 → `RELEASE` 전환 및 재고 차감 |

---

## 개발 방법론 준수 사항

- **CLAUDE.md / PRD.md** 문서 관리: 각 Repository에 유지
- **테스트 코드**: 회귀검증·리팩토링 내성·유지보수성을 고려하여 작성
- **Commit 이력**: 기능 단위로 명확한 메시지 유지
- **Clean Code**: 역할 분리, 명확한 네이밍 준수
