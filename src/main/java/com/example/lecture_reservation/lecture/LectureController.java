package com.example.lecture_reservation.lecture;

import com.example.lecture_reservation.lecture.domain.ApplyHistory;
import com.example.lecture_reservation.lecture.domain.Lecture;
import com.example.lecture_reservation.lecture.dto.ApplyHistoryResponseDto;
import com.example.lecture_reservation.lecture.dto.ApplyRequestDto;
import com.example.lecture_reservation.lecture.dto.ApplyResponseDto;
import com.example.lecture_reservation.lecture.dto.LectureResponseDto;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/lectures")
public class LectureController {

    private LectureService lectureService;

    public LectureController(LectureService lectureService) {
        this.lectureService = lectureService;
    }


    @PostMapping("/apply")
    public ApplyResponseDto applyLecture(@RequestBody ApplyRequestDto dto) throws Exception {
        if (dto.getUserId() == null) throw new Exception("강의ID가 없습니다.");
        return lectureService.applyLecture(dto.getUserId(), dto.toEntity());
    }

    /**
     * 조건
     * 1.특정 userId 로 특강 신청 완료 여부를 조회하는 API 를 작성합니다.
     * 2.특강 신청에 성공한 사용자는 성공했음을, 특강 등록자 명단에 없는 사용자는 실패했음을 반환합니다. (true, false)
     * */
    @GetMapping("/application/{userId}")
    public ApplyHistoryResponseDto success(@PathVariable int userId) {
        return lectureService.readApplyHistories(userId);
    }

    /**
     * 조건
     * 1.단 한번의 특강을 위한 것이 아닌 날짜별로 특강이 존재할 수 있는 범용적인 서비스로 변화시켜 봅니다.
     * 2.이를 수용하기 위해, 특강 엔티티의 경우 기존의 설계에서 변경되어야 합니다.
     * 3. 특강의 정원은 30명으로 고정이며, 사용자는 각 특강에 신청하기전 목록을 조회해볼 수 있어야 합니다.
     *  추가고려 -> 추가로 정원이 특강마다 다르다면 어떻게 처리할것인가..? 고민해 보셔라~
     * */
    @GetMapping("/")
    public LectureResponseDto selectLecture() {
        return lectureService.readLectures();
    }

}
