package com.example.lecture_reservation.lecture;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class LectureService {

    private LectureRepositoryImpl lectureRepository;
    private ApplyInfoRepositoryImpl applyInfoRepository;
    private ApplyHistoryRepositoryImpl applyHistoryRepository;


    public LectureService (LectureRepositoryImpl lectureRepository, ApplyInfoRepositoryImpl applyInfoRepository, ApplyHistoryRepositoryImpl applyHistoryRepository) {
        this.lectureRepository = lectureRepository;
        this.applyInfoRepository = applyInfoRepository;
        this.applyHistoryRepository = applyHistoryRepository;
    }

    @Transactional
    public ApplyResponseDto applyLecture(int userId ,ApplyInfo applyInfo) throws Exception {

//        ApplyHistory applyHistory = applyHistoryRepository.

        Lecture lecture = getLectureInfo(applyInfo.getLectureId());
        if (lecture == null) throw new Exception("일치하는 강의정보가 없습니다.");
        if (lecture.getDate().isAfter(LocalDateTime.now())) throw new Exception("신청 시간 전 입니다.");

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
                    .lectureId(applyInfo.getLectureId())
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


    public boolean checkSuccess(int userId) {
        List<ApplyHistory> applyHistories = applyHistoryRepository.findByUserId(userId);
        if (applyHistories.isEmpty()) return false;
        return true;
    }

    public List<Lecture> readLectures() {
        return lectureRepository.findAll();
    }
}
