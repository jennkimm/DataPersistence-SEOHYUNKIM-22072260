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