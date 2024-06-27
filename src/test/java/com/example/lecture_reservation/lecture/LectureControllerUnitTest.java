package com.example.lecture_reservation.lecture;

import com.example.lecture_reservation.lecture.domain.ApplyHistory;
import com.example.lecture_reservation.lecture.domain.Lecture;
import com.example.lecture_reservation.lecture.dto.ApplyHistoryResponseDto;
import com.example.lecture_reservation.lecture.dto.ApplyRequestDto;
import com.example.lecture_reservation.lecture.dto.ApplyResponseDto;
import com.example.lecture_reservation.lecture.dto.LectureResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(LectureController.class)
@ExtendWith(MockitoExtension.class)
public class LectureControllerUnitTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    LectureService lectureService;

    @Autowired
    private ObjectMapper objectMapper;


    //특강 신청 성공
    @Test
    void 특강신청성공 () throws Exception{
        //given
        ApplyRequestDto requestDto = new ApplyRequestDto();
        requestDto.setUserId(1);
        requestDto.setLectureId(1);

        ApplyResponseDto responseDto = new ApplyResponseDto();
        responseDto.setCompleted(true);

        when(lectureService.applyLecture(anyInt(), any())).thenReturn(responseDto);


        //when
        //then
        mvc.perform(post("/lectures/apply")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.completed").value(true));

    }

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

        ApplyHistoryResponseDto responseDto = ApplyHistoryResponseDto.builder()
                .applyHistories(expectApplyHistories)
                .build();

        when(lectureService.readApplyHistories(anyInt())).thenReturn(responseDto);

        //when
        //then
        mvc.perform(get("/lectures/application/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.histories[0].completed").value(true));
    }

    @Test
    void 강의_조회() throws Exception{
        //given
        Lecture lecture1 = new Lecture();
        lecture1.setId(1);
        lecture1.setTitle("A");
        lecture1.setDate(LocalDateTime.of(2024,05,01,13,0,0));
        lecture1.setApplyDate(LocalDateTime.of(2024,05,01,12,0,0));
        lecture1.setSeat(30);

        Lecture lecture2 = new Lecture();
        lecture2.setId(2);
        lecture2.setTitle("B");
        lecture2.setDate(LocalDateTime.of(2024,06,02,12,0,0));
        lecture2.setApplyDate(LocalDateTime.of(2024,06,01,12,0,0));
        lecture2.setSeat(20);
        List<Lecture> expectList = new ArrayList<>();
        expectList.add(lecture1);
        expectList.add(lecture2);

        LectureResponseDto responseDto = LectureResponseDto.builder()
                .lectureList(expectList)
                .build();
        when(lectureService.readLectures()).thenReturn(responseDto);

        //when
        //then
        mvc.perform(get("/lectures/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lectureList", Matchers.hasSize(2)));

    }


}
