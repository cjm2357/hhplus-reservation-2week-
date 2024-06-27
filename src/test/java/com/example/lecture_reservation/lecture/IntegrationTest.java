package com.example.lecture_reservation.lecture;


import com.example.lecture_reservation.lecture.domain.ApplyHistory;
import com.example.lecture_reservation.lecture.domain.Lecture;
import com.example.lecture_reservation.lecture.dto.ApplyHistoryResponseDto;
import com.example.lecture_reservation.lecture.dto.ApplyRequestDto;
import com.example.lecture_reservation.lecture.dto.LectureResponseDto;
import com.example.lecture_reservation.lecture.repository.ApplyHistoryRepository;
import com.example.lecture_reservation.lecture.repository.ApplyInfoRepository;
import com.example.lecture_reservation.lecture.repository.LectureRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest
public class IntegrationTest {


    @Autowired
    private LectureController lectureController;

    @Autowired
    private LectureService lectureService;

    @Autowired
    ApplyHistoryRepository applyHistoryRepository;

    @Autowired
    ApplyInfoRepository applyInfoRepository;


    @BeforeEach
    void setUp() {
        Lecture lecture = new Lecture();
        lecture.setTitle("TEST LECTURE");
        lecture.setSeat(30);
        lecture.setDate(LocalDateTime.of(2024, 05, 26, 15, 0 , 0));
        lecture.setApplyDate(LocalDateTime.of(2024, 05, 25, 15, 0 , 0));
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


    /**
     * 특강신청
     * 1. 특강신청 성공 후 이력조회
     * 2. 특정인원이상 신청시 실패
     * 3. 같은 날 두번째 수강신청 실패
     *
     * */
    @Test
    void 특강신청_성공후_이력조회() throws Exception {
        //given
        int userId = 1;
        int lectureId = 1;
        ApplyRequestDto dto = new ApplyRequestDto();
        dto.setUserId(userId);
        dto.setLectureId(lectureId);
        lectureController.applyLecture(dto);
        //when
        ApplyHistoryResponseDto responseDto = lectureController.success(userId);

        //then
        assertEquals("TEST LECTURE", responseDto.getHistories().get(0).getLectureTitle());
        assertEquals(true, responseDto.getHistories().get(0).isCompleted());

    }

    @Test
    void 특정인원이상_신청시_실패() throws Exception {
        //given
        Lecture lecture = new Lecture();
        lecture.setTitle("TEST LECTURE2");
        lecture.setSeat(2);
        lecture.setDate(LocalDateTime.of(2024, 05, 26, 15, 0 , 0));
        lecture.setApplyDate(LocalDateTime.of(2024, 05, 25, 15, 0 , 0));
        Lecture registerdLecture = lectureService.registerLecture(lecture);

        int lectureId = registerdLecture.getId();
        int userId = 0;

        for (userId = 1; userId <= 2; userId++) {
            ApplyRequestDto dto = new ApplyRequestDto();
            dto.setUserId(userId);
            dto.setLectureId(lectureId);
            lectureController.applyLecture(dto);
        }
        ApplyRequestDto errorDto = new ApplyRequestDto();
        userId = 6;
        errorDto.setUserId(userId);
        errorDto.setLectureId(lectureId);
        lectureController.applyLecture(errorDto);

        //when
        ApplyHistoryResponseDto responseDto = lectureController.success(userId);


        //then
        assertEquals("TEST LECTURE2",  responseDto.getHistories().get(0).getLectureTitle());
        assertEquals(false,  responseDto.getHistories().get(0).isCompleted());
    }

    @Test
    void 같은날_두번째_강의신청_실패 () throws Exception {
        //given
        int userId = 1;
        Lecture lecture = new Lecture();
        lecture.setTitle("TEST LECTURE2");
        lecture.setSeat(2);
        lecture.setDate(LocalDateTime.of(2024, 05, 26, 15, 0 , 0));
        lecture.setApplyDate(LocalDateTime.of(2024, 05, 25, 15, 0 , 0));
        Lecture registerdLecture = lectureService.registerLecture(lecture);

        ApplyRequestDto dto = new ApplyRequestDto();
        dto.setUserId(userId);
        dto.setLectureId(1);
        lectureController.applyLecture(dto);

        ApplyRequestDto targetDto = new ApplyRequestDto();
        targetDto.setUserId(userId);
        targetDto.setLectureId(registerdLecture.getId());

        //when
        Throwable exception = assertThrows(Exception.class, () -> {
            lectureController.applyLecture(targetDto);
        });

        //then
        assertEquals("같은 날짜에 신청한 강의가 존재합니다.", exception.getMessage());

    }


    /**
     * 조건
     * 1.특강리스트 조회
     * */

    @Test
    void 특강리스트_조회() {
        //given
        Lecture lecture = new Lecture();
        lecture.setTitle("TEST LECTURE2");
        lecture.setSeat(30);
        lecture.setDate(LocalDateTime.of(2024, 05, 26, 15, 0 , 0));
        lecture.setApplyDate(LocalDateTime.of(2024, 05, 25, 15, 0 , 0));
        Lecture registerdLecture = lectureService.registerLecture(lecture);

        //when
        LectureResponseDto responseDto = lectureController.selectLecture();

        //then
        assertEquals(2, responseDto.getLectureList().size());
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
