package com.example.lecture_reservation.lecture;

import com.example.lecture_reservation.lecture.domain.ApplyHistory;
import com.example.lecture_reservation.lecture.domain.ApplyInfo;
import com.example.lecture_reservation.lecture.domain.Lecture;
import com.example.lecture_reservation.lecture.dto.ApplyHistoryResponseDto;
import com.example.lecture_reservation.lecture.dto.ApplyResponseDto;
import com.example.lecture_reservation.lecture.dto.LectureResponseDto;
import com.example.lecture_reservation.lecture.repository.ApplyHistoryRepository;
import com.example.lecture_reservation.lecture.repository.ApplyInfoRepository;
import com.example.lecture_reservation.lecture.repository.LectureRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LectureService {

    private LectureRepository lectureRepository;
    private ApplyInfoRepository applyInfoRepository;
    private ApplyHistoryRepository applyHistoryRepository;


    public LectureService (LectureRepository lectureRepository, ApplyInfoRepository applyInfoRepository, ApplyHistoryRepository applyHistoryRepository) {
        this.lectureRepository = lectureRepository;
        this.applyInfoRepository = applyInfoRepository;
        this.applyHistoryRepository = applyHistoryRepository;
    }

    @Transactional
    public ApplyResponseDto applyLecture(int userId , ApplyInfo applyInfo) throws Exception {


        Lecture lecture = getLectureInfo(applyInfo.getLectureId());
        if (lecture == null) throw new Exception("일치하는 강의정보가 없습니다.");
        if (lecture.getApplyDate().isAfter(LocalDateTime.now())) throw new Exception("신청 시간 전 입니다.");

        List<ApplyHistory> applyHistories = applyHistoryRepository.findByUserId(userId);
        applyHistories = applyHistories.stream().filter(h -> {
            Date registeredLectureDate = convertDate(h.getLecture().getDate());
            Date newLectureDate = convertDate(lecture.getDate());
            return registeredLectureDate.equals(newLectureDate);
        }).collect(Collectors.toList());

        if (applyHistories.size() > 0) throw new Exception("같은 날짜에 신청한 강의가 존재합니다.");

        int maxUser = lecture.getSeat();
        int curUserCnt = getApplyInfoCnt(applyInfo.getLectureId());

        boolean isSuccess = false;
        try {
            if (maxUser >  curUserCnt) {
                applyInfo.setUserCnt(curUserCnt+1);
                ApplyInfo result = applyInfoRepository.save(applyInfo);
                if (result != null) {
                    isSuccess = true;
                }
            } else{
                isSuccess = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ApplyHistory applyHistory = ApplyHistory.builder()
                    .userId(userId)
//                    .lectureId(applyInfo.getLectureId())
                    .lecture(lecture)
                    .applyTime(LocalDateTime.now())
                    .completed(isSuccess)
                    .build();

            applyHistoryRepository.save(applyHistory);
        }

        return ApplyResponseDto.builder().completed(isSuccess).build();
    }

    public Lecture getLectureInfo (int lectureId) {
        Optional<Lecture> lecture = lectureRepository.findById(lectureId);
        return lecture.orElse(null);
    }

    public int getApplyInfoCnt (int lectureId) {
        Optional<ApplyInfo> source = applyInfoRepository.findById(lectureId);
        if (source.isEmpty()) return 0;
        return source.get().getUserCnt();
    }

    public Lecture registerLecture(Lecture lecture) {
        Lecture result = lectureRepository.save(lecture);
        ApplyInfo applyInfo = new ApplyInfo(result.getId(), 0);
        applyInfoRepository.save(applyInfo);
        return result;
    }

    public List<ApplyHistory> readHistoryCompleteUserCnt(Integer lectureId, boolean completed) {
        return applyHistoryRepository.findByLectureIdAndCompleted(lectureId, completed);

    }


    public ApplyHistoryResponseDto readApplyHistories(int userId) {
        List<ApplyHistory> applyHistories = applyHistoryRepository.findByUserId(userId);

        return ApplyHistoryResponseDto.builder()
                .applyHistories(applyHistories)
                .build();
    }

    public LectureResponseDto readLectures() {
        return LectureResponseDto.builder()
                    .lectureList(lectureRepository.findAll())
                    .build();
    }

    private Date convertDate(LocalDateTime localDateTime) {
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
        return Date.from(zonedDateTime.toInstant());
    }
}
