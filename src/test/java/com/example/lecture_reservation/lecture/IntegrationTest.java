package com.example.lecture_reservation.lecture;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
public class IntegrationTest {


    @Autowired
    private LectureController lectureController;

    @Autowired
    private LectureService lectureService;




    @BeforeEach
    void setUp() {
        Lecture lecture = new Lecture();
        lecture.setTitle("TEST LECTURE");
        lecture.setSeat(30);
        lecture.setDate(LocalDateTime.of(2024, 05, 25, 15, 0 , 0));
        lectureService.registerLecture(lecture);
    }

    //동시성 테스트
    @Test
    void 동시성_30명_성공_테스트() throws Exception{

        // given
        CountDownLatch countDownLatch = new CountDownLatch(100);
        List<Thread> workers = new ArrayList<>();
        int lectureId = 1;

        for (int userId = 1; userId <= 100; userId++) {
            ApplyRequestDto dto = new ApplyRequestDto();
            dto.setUserId(userId);
            dto.setLectureId(lectureId);
            workers.add(new Thread(new ApplyRunner(dto, countDownLatch)));
        }

        //when
        workers.forEach(Thread::start);
        countDownLatch.await();
        List<ApplyHistory> successList = lectureService.readHistoryCompleteUserCnt(1, true);
        List<ApplyHistory> failList = lectureService.readHistoryCompleteUserCnt(1, false);

        // then
        assertEquals(successList.size(), 30);
        assertEquals(failList.size(), 70);


    }

    private class ApplyRunner implements Runnable {
        private ApplyRequestDto dto;
        private CountDownLatch countDownLatch;

        public ApplyRunner(ApplyRequestDto dto, CountDownLatch countDownLatch) {
            this.dto = dto;
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void run() {
            try {
                lectureController.applyLecture(this.dto);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("실패");
            }
            countDownLatch.countDown();
        }
    }
}
