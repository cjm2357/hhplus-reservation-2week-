package com.example.lecture_reservation.lecture;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LectureServiceUnitTest {


    @Mock
    LectureRepositoryImpl lectureRepository;
    @Mock
    ApplyInfoRepositoryImpl applyInfoRepository;
    @Mock
    ApplyHistoryRepositoryImpl applyHistoryRepository;

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
        lecture.setDate(LocalDateTime.of(2024, 01, 01, 10, 0 ,0));
        lecture.setTitle("TEST");
        Optional<Lecture> optional = Optional.of(lecture);

        when(lectureRepository.findById(anyInt())).thenReturn(optional);
        when(applyInfoRepository.save(any())).thenReturn(applyInfo);

        //when
        ApplyResponseDto dto = lectureService.applyLecture(userId,applyInfo);
        //then
        assertEquals(true, dto.isCompleted());
    }


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
        lecture.setDate(LocalDateTime.of(2024, 01, 01, 10, 0 ,0));
        lecture.setTitle("TEST");
        Optional<Lecture> optional = Optional.of(lecture);

        when(lectureRepository.findById(anyInt())).thenReturn(optional);
        when(applyInfoRepository.findById(anyInt())).thenReturn(Optional.of(applyInfo));

        //when
        ApplyResponseDto dto = lectureService.applyLecture(userId,applyInfo);
        //then
        assertEquals(false, dto.isCompleted());
    }

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
        lecture.setDate(LocalDateTime.of(2024, 01, 01, 10, 0 ,0));
        lecture.setTitle("TEST");
        Optional<Lecture> optional = Optional.of(lecture);

        when(lectureRepository.findById(anyInt())).thenReturn(optional);
        when(applyInfoRepository.findById(anyInt())).thenReturn(Optional.of(applyInfo));

        //when
        Throwable exception = assertThrows(Exception.class, () -> {
            lectureService.applyLecture(userId,applyInfo);
        });

        //then
        assertEquals("같은 날짜에 신청한 강의가 존재합니다.", exception.getMessage());
    }

    /**
     * 히스토리 테스트
     * 1. 특강신청 성공시 히스토리  성공으로 저장 여부
     * 2. 특강신청 실패시 히스토리 실패로 저장 여부
     * 3. 특강신청 에러시 히스토리 실패로 저장 여부
     * 4. 요청 타입이 다를때 실패
     * 5. lecture_id가 없을때 저장 실패
     * 6. user_id가 없을 때 저장 실패
     *
     * */

    /**
     * 걍의 조회 테스트
     * 1. 강의 조회 성공
     * 2. 특정 날짜에 강의가 없는 경우
     *
     * */
}
