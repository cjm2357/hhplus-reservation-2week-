package com.example.lecture_reservation.lecture;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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

    @BeforeEach
    void setUp() {

    }


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
        mvc.perform(post("/lectures/apply"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.completed").value(true));

    }

    @Test
    void 히스토리_조회_성공 () throws Exception {
        //given
        Integer userId = 1;
        when(lectureService.checkSuccess(anyInt())).thenReturn(true);

        //when
        //then
        mvc.perform(get("/lectures/application/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }

    @Test
    void 강의_조회() throws Exception{
        //given
        Lecture lecture1 = new Lecture();
        lecture1.setId(1);
        lecture1.setTitle("A");
        lecture1.setDate(LocalDateTime.of(2024,05,01,12,0,0));
        lecture1.setSeat(30);

        Lecture lecture2 = new Lecture();
        lecture2.setId(2);
        lecture2.setTitle("B");
        lecture2.setDate(LocalDateTime.of(2024,06,01,12,0,0));
        lecture2.setSeat(20);
        List<Lecture> expectList = new ArrayList<>();
        expectList.add(lecture1);
        expectList.add(lecture2);
        when(lectureService.readLectures()).thenReturn(expectList);

        //when
        //then
        mvc.perform(get("/lectures/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(2)));

    }


}
