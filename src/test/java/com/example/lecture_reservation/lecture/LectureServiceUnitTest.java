package com.example.lecture_reservation.lecture;

import com.example.lecture_reservation.lecture.domain.ApplyHistory;
import com.example.lecture_reservation.lecture.domain.ApplyInfo;
import com.example.lecture_reservation.lecture.domain.Lecture;
import com.example.lecture_reservation.lecture.dto.ApplyHistoryResponseDto;
import com.example.lecture_reservation.lecture.dto.ApplyResponseDto;
import com.example.lecture_reservation.lecture.repository.ApplyHistoryRepository;
import com.example.lecture_reservation.lecture.repository.ApplyInfoRepository;
import com.example.lecture_reservation.lecture.repository.LectureRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LectureServiceUnitTest {


    @Mock
    LectureRepository lectureRepository;
    @Mock
    ApplyInfoRepository applyInfoRepository;
    @Mock
    ApplyHistoryRepository applyHistoryRepository;

    @InjectMocks
    LectureService lectureService;

    /**
     * 특강신청
     * 1. 특강신청 성공
     * 2. 신청날짜 이전 은 실패
     * 3. 특정인원이상 신청시 실패
     * 4. 같은 날 두번째 수강신청 실패
     *
     * */

    //특강 신청 성공
    //case 1
    @Test
    void 특강_신청_성공() throws Exception{
        //given
        int lectureId = 1;
        int userId = 1;
        ApplyInfo applyInfo = new ApplyInfo();
        applyInfo.setLectureId(lectureId);
        applyInfo.setUserCnt(0);
        Lecture lecture = new Lecture();
        lecture.setId(lectureId);
        lecture.setSeat(30);
        lecture.setDate(LocalDateTime.of(2024, 6, 01, 10, 0 ,0));
        lecture.setApplyDate(LocalDateTime.of(2024, 5, 01, 10, 0 ,0));
        lecture.setTitle("TEST");
        Optional<Lecture> optional = Optional.of(lecture);

        when(lectureRepository.findById(anyInt())).thenReturn(optional);
        when(applyInfoRepository.save(any())).thenReturn(applyInfo);

        //when
        ApplyResponseDto dto = lectureService.applyLecture(userId,applyInfo);
        //then
        assertEquals(true, dto.isCompleted());
    }


    //case 2
    @Test
    void 특강_신청_정원초과_실패() throws Exception{
        //given
        int lectureId = 1;
        int userId = 1;
        ApplyInfo applyInfo = new ApplyInfo();
        applyInfo.setLectureId(lectureId);
        applyInfo.setUserCnt(20);
        Lecture lecture = new Lecture();
        lecture.setId(lectureId);
        lecture.setSeat(10);
        lecture.setDate(LocalDateTime.of(2024, 02, 01, 10, 0 ,0));
        lecture.setApplyDate(LocalDateTime.of(2024, 01, 01, 10, 0 ,0));
        lecture.setTitle("TEST");
        Optional<Lecture> optional = Optional.of(lecture);

        when(lectureRepository.findById(anyInt())).thenReturn(optional);
        when(applyInfoRepository.findById(anyInt())).thenReturn(Optional.of(applyInfo));

        //when
        ApplyResponseDto dto = lectureService.applyLecture(userId,applyInfo);
        //then
        assertEquals(false, dto.isCompleted());
    }

    //case 3
    @Test
    void 특강_신청_특정날짜_이전_실패() throws Exception{
        //given
        int lectureId = 1;
        int userId = 1;
        ApplyInfo applyInfo = new ApplyInfo();
        applyInfo.setLectureId(lectureId);
        applyInfo.setUserCnt(20);
        Lecture lecture = new Lecture();
        lecture.setId(lectureId);
        lecture.setSeat(10);
        lecture.setDate(LocalDateTime.of(2024, 12, 12, 10, 0 ,0));
        lecture.setApplyDate(LocalDateTime.of(2024, 12, 11, 10, 0 ,0));
        lecture.setTitle("TEST");
        Optional<Lecture> optional = Optional.of(lecture);

        when(lectureRepository.findById(anyInt())).thenReturn(optional);

        //when
        Throwable exception = assertThrows(Exception.class, () -> {
            lectureService.applyLecture(userId,applyInfo);
        });

        //then
        assertEquals("신청 시간 전 입니다.", exception.getMessage());
    }

    //case 4
    @Test
    void 같은날_다른_특강_신청시_실패() throws Exception{
        //given
        int lectureId = 1;
        int userId = 1;
        ApplyInfo applyInfo = new ApplyInfo();
        applyInfo.setLectureId(lectureId);
        applyInfo.setUserCnt(0);
        Lecture lecture = new Lecture();
        lecture.setId(lectureId);
        lecture.setSeat(30);
        lecture.setDate(LocalDateTime.of(2024, 2, 01, 10, 0 ,0));
        lecture.setApplyDate(LocalDateTime.of(2024, 01, 01, 10, 0 ,0));
        lecture.setTitle("TEST");
        Optional<Lecture> optional = Optional.of(lecture);


        int lectureId2 = 2;
        Lecture lecture2 = new Lecture();
        lecture2.setId(lectureId2);
        lecture2.setSeat(30);
        lecture2.setDate(LocalDateTime.of(2024, 2, 01, 10, 0 ,0));
        lecture2.setApplyDate(LocalDateTime.of(2024, 01, 01, 10, 0 ,0));
        lecture2.setTitle("TEST2");
        ApplyHistory applyHistory = ApplyHistory.builder()
                .id(1)
                .applyTime(LocalDateTime.of(2024, 01, 01, 10, 0 ,0))
                .userId(userId)
                .completed(true)
                .lecture(lecture2)
                .build();

        List<ApplyHistory> applyHistories = new ArrayList<>();
        applyHistories.add(applyHistory);


        when(lectureRepository.findById(anyInt())).thenReturn(optional);
        when(applyHistoryRepository.findByUserId(anyInt())).thenReturn(applyHistories);

        //when
        Throwable exception = assertThrows(Exception.class, () -> {
            lectureService.applyLecture(userId,applyInfo);
        });

        //then
        assertEquals("같은 날짜에 신청한 강의가 존재합니다.", exception.getMessage());
    }

    /**
     * 히스토리 테스트
     * 1. 히스토리 조회 성공 여부
     * */

    //case 1
    @Test
    void 히스토리_조회_성공 () throws Exception {
        //given
        int userId = 1;

        Lecture lecture = new Lecture();
        lecture.setId(1);
        lecture.setSeat(30);
        lecture.setDate(LocalDateTime.of(2024, 2, 01, 10, 0 ,0));
        lecture.setApplyDate(LocalDateTime.of(2024, 01, 01, 10, 0 ,0));
        lecture.setTitle("TEST");

        List<ApplyHistory> expectApplyHistories = new ArrayList<>();
        ApplyHistory applyHistory = ApplyHistory.builder()
                .id(1)
                .lecture(lecture)
                .applyTime(LocalDateTime.of(2024, 01, 01, 10, 10 ,0))
                .completed(true)
                .userId(1)
                .build();
        expectApplyHistories.add(applyHistory);
        when(applyHistoryRepository.findByUserId(anyInt())).thenReturn(expectApplyHistories);

        //when
        ApplyHistoryResponseDto responseDto = lectureService.readApplyHistories(userId);

        //then
        ApplyHistoryResponseDto.History history = responseDto.getHistories().get(0);
        assertEquals("TEST", history.getLectureTitle());
        assertEquals(true, history.isCompleted());

    }

    /**
     * 걍의 조회 테스트
     * 1. 강의 조회 성공
     * */
    @Test
    void 강의_조회_성공() throws Exception {
        //given
        Lecture lecture = new Lecture();
        lecture.setId(1);
        lecture.setSeat(30);
        lecture.setDate(LocalDateTime.of(2024, 2, 01, 10, 0 ,0));
        lecture.setApplyDate(LocalDateTime.of(2024, 01, 01, 10, 0 ,0));
        lecture.setTitle("TEST");
        List<Lecture> expectedLectureList = new ArrayList<>();
        expectedLectureList.add(lecture);
        when(lectureRepository.findAll()).thenReturn(expectedLectureList);
        //when
        List<Lecture> lecturesList = lectureRepository.findAll();
        //then
        assertEquals(expectedLectureList, lecturesList);
    }
}
