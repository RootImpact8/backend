package com.example.rootimpact.domain.diary.service;

import com.example.rootimpact.domain.diary.dto.FarmDiaryRequest;
import com.example.rootimpact.domain.diary.dto.FarmDiaryResponse;
import com.example.rootimpact.domain.diary.entity.FarmDiary;
import com.example.rootimpact.domain.diary.repository.FarmDiaryRepository;
import com.example.rootimpact.domain.user.entity.User;
import com.example.rootimpact.domain.user.repository.UserRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FarmDiaryService {

    private final FarmDiaryRepository farmDiaryRepository;
    private final UserRepository userRepository;

    // 일기 생성 메서드
    @Transactional
    public FarmDiaryResponse create(FarmDiaryRequest request) {
        User user = userRepository.findById(request.getUserId()).orElseThrow(()->new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 엔티티 생성
        FarmDiary farmDiary =  FarmDiary.builder()
                                       .user(user)
                                       .content(request.getContent())
                                       .writeDate(request.getWriteDate())
                                       .build();

        // 저장 후 응답 DTO 변환
        FarmDiary saved = farmDiaryRepository.save(farmDiary);
        return new FarmDiaryResponse(saved);
    }

    // 일기 수정 메서드
    @Transactional
    public FarmDiaryResponse update(Long id, FarmDiaryRequest request) {
        FarmDiary farmDiary = farmDiaryRepository.findById(id).orElseThrow(()->new IllegalArgumentException("일기를 찾을 수 없습니다."));

        //작물 조회
        //작업 조회

        // 수정
        farmDiary.update(request.getContent(), request.getWriteDate());

        return new FarmDiaryResponse(farmDiary);
    }

    // 일기 ID로 일기 조회
    public FarmDiaryResponse findById(Long id) {
        FarmDiary farmDiary = farmDiaryRepository.findById(id).orElseThrow(()->new IllegalArgumentException("일기를 찾을 수 없습니다."));
        return new FarmDiaryResponse(farmDiary);
    }

    // 사용자 ID로 일기 조회
    public List<FarmDiaryResponse> findByUserId(Long userId) {
        List<FarmDiary> diaries = farmDiaryRepository.findByUserId(userId);
        return diaries.stream().map(FarmDiaryResponse::new)
                       .collect(Collectors.toList());
    }

    // 작성일로 일기 조회
    public List<FarmDiaryResponse> findByWriteDate(LocalDate writeDate) {
        List<FarmDiary> diaries = farmDiaryRepository.findByWriteDate(writeDate);
        return diaries.stream().map(FarmDiaryResponse::new)
                       .collect(Collectors.toList());
    }

    // 일기 삭제
    @Transactional
    public void delete(Long id) {
        FarmDiary farmDiary = farmDiaryRepository.findById(id).orElseThrow(()->new IllegalArgumentException("일기를 찾을 수 없습니다."));
        farmDiaryRepository.delete(farmDiary);
    }




}
