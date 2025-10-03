package com.github.triprooty.demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class TestDbRunner implements CommandLineRunner {
    private final TestEntityRepository repository;

    public TestDbRunner(TestEntityRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        TestEntity test = new TestEntity();
        test.setMessage("DB 연결 테스트 성공!");
        TestEntity saved = repository.save(test);

        System.out.println("저장된 메시지 ID = " + saved.getId());
        System.out.println("저장된 메시지 내용 = " + saved.getMessage());
    }
}
