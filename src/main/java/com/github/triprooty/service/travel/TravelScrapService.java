package com.github.triprooty.service.travel;

import com.github.triprooty.domain.Travel;
import com.github.triprooty.domain.TravelScrap;
import com.github.triprooty.domain.User;
import com.github.triprooty.global.exception.travel.TravelErrorCode;
import com.github.triprooty.global.exception.travel.TravelException;
import com.github.triprooty.global.exception.travel.TravelNotFoundException;
import com.github.triprooty.repository.UserRepository;
import com.github.triprooty.repository.travel.TravelRepository;
import com.github.triprooty.repository.travel.TravelScrapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TravelScrapService {

    private final TravelScrapRepository travelScrapRepository;
    private final TravelRepository travelRepository;
    private final UserRepository userRepository;

    @Transactional
    public void scrap(UUID userId, UUID travelId) {
        if (travelScrapRepository.findByTravelIdAndUserId(travelId, userId).isPresent()) {
            return;
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new TravelException(TravelErrorCode.USER_NOT_FOUND));
        Travel travel = travelRepository.findById(travelId)
                .orElseThrow(TravelNotFoundException::new);

        travelScrapRepository.save(TravelScrap.builder()
                .travel(travel)
                .user(user)
                .build());
    }

    @Transactional
    public void unscrap(UUID userId, UUID travelId) {
        travelScrapRepository.findByTravelIdAndUserId(travelId, userId)
                .ifPresent(travelScrapRepository::delete);
    }
}
