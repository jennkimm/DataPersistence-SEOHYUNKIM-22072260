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