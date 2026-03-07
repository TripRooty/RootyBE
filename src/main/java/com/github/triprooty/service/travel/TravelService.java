package com.github.triprooty.service.travel;

import com.github.triprooty.domain.*;
import com.github.triprooty.domain.enums.TravelParticipantRole;
import com.github.triprooty.domain.enums.TravelParticipantStatus;
import com.github.triprooty.dto.request.travel.TravelCreateRequest;
import com.github.triprooty.dto.request.travel.TravelDayCreateRequest;
import com.github.triprooty.dto.request.travel.TravelDayReorderRequest;
import com.github.triprooty.dto.request.travel.TravelUpdateRequest;
import com.github.triprooty.dto.response.travel.ItineraryItemResponse;
import com.github.triprooty.dto.response.travel.TravelDayResponse;
import com.github.triprooty.dto.response.travel.TravelDetailResponse;
import com.github.triprooty.dto.response.travel.TravelSummaryResponse;
import com.github.triprooty.global.exception.travel.TravelErrorCode;
import com.github.triprooty.global.exception.travel.TravelException;
import com.github.triprooty.global.exception.travel.TravelNotFoundException;
import com.github.triprooty.repository.UserRepository;
import com.github.triprooty.repository.travel.FollowRepository;
import com.github.triprooty.repository.travel.TravelDayRepository;
import com.github.triprooty.repository.travel.TravelParticipantRepository;
import com.github.triprooty.repository.travel.TravelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TravelService {

    private final TravelRepository travelRepository;
    private final TravelDayRepository travelDayRepository;
    private final TravelParticipantRepository travelParticipantRepository;
    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    @Transactional
    public TravelSummaryResponse createTravel(UUID userId, TravelCreateRequest request) {
        validateDateRange(request.getStartDate(), request.getEndDate());

        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new TravelException(TravelErrorCode.USER_NOT_FOUND));

        Travel travel = Travel.builder()
                .owner(owner)
                .title(request.getTitle())
                .description(request.getDescription())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .visibility(request.getVisibility())
                .build();

        travelRepository.save(travel);

        travelParticipantRepository.save(TravelParticipant.builder()
                .travel(travel)
                .user(owner)
                .role(TravelParticipantRole.OWNER)
                .status(TravelParticipantStatus.ACCEPTED)
                .build());

        autoCreateTravelDays(travel, request.getStartDate(), request.getEndDate());

        return TravelSummaryResponse.builder()
                .travelId(travel.getId())
                .build();
    }

    public TravelDetailResponse getTravelDetail(UUID userId, UUID travelId) {
        Travel travel = getReadableTravelOrThrow(userId, travelId);
        List<TravelDay> days = travelDayRepository.findWithItemsByTravelIdOrderByOrderNoAsc(travelId);

        List<TravelDayResponse> dayResponses = days.stream()
                .map(day -> TravelDayResponse.from(day,
                        day.getItineraryItems().stream()
                                .sorted(Comparator.comparing(ItineraryItem::getOrderNo))
                                .map(ItineraryItemResponse::from)
                                .toList()
                ))
                .toList();

        return TravelDetailResponse.from(travel, dayResponses);
    }

    public List<TravelDayResponse> getTravelDays(UUID userId, UUID travelId) {
        getReadableTravelOrThrow(userId, travelId);
        return travelDayRepository.findByTravelIdOrderByOrderNoAsc(travelId).stream()
                .map(day -> TravelDayResponse.from(day, List.of()))
                .toList();
    }

    @Transactional
    public TravelDayResponse createTravelDay(UUID userId, UUID travelId, TravelDayCreateRequest request) {
        Travel travel = getEditableTravelOrThrow(userId, travelId);

        TravelDay day = TravelDay.builder()
                .travel(travel)
                .orderNo(request.getOrderNo())
                .travelDate(request.getTravelDate())
                .build();

        travelDayRepository.save(day);
        return TravelDayResponse.from(day, List.of());
    }

    @Transactional
    public List<TravelDayResponse> reorderTravelDays(UUID userId, UUID travelId, TravelDayReorderRequest request) {
        getEditableTravelOrThrow(userId, travelId);

        List<TravelDay> days = travelDayRepository.findByTravelIdOrderByOrderNoAsc(travelId);
        if (days.size() != request.getDayIds().size()) {
            throw new TravelException(TravelErrorCode.INVALID_DAY_REORDER_SIZE);
        }

        Map<UUID, TravelDay> dayMap = new HashMap<>();
        for (TravelDay day : days) {
            dayMap.put(day.getId(), day);
        }

        for (int i = 0; i < request.getDayIds().size(); i++) {
            UUID dayId = request.getDayIds().get(i);
            TravelDay day = dayMap.get(dayId);
            if (day == null) {
                throw new TravelException(TravelErrorCode.INVALID_DAY_REORDER_ID);
            }
            day.update(i + 1, day.getTravelDate());
        }

        return travelDayRepository.findByTravelIdOrderByOrderNoAsc(travelId).stream()
                .map(day -> TravelDayResponse.from(day, List.of()))
                .toList();
    }

    @Transactional
    public void updateTravel(UUID userId, UUID travelId, TravelUpdateRequest request) {
        Travel travel = getEditableTravelOrThrow(userId, travelId);

        LocalDate startDate = request.getStartDate() == null ? travel.getStartDate() : request.getStartDate();
        LocalDate endDate = request.getEndDate() == null ? travel.getEndDate() : request.getEndDate();
        validateDateRange(startDate, endDate);

        travel.update(
                request.getTitle() == null ? travel.getTitle() : request.getTitle(),
                request.getDescription() == null ? travel.getDescription() : request.getDescription(),
                startDate,
                endDate,
                request.getVisibility() == null ? travel.getVisibility() : request.getVisibility()
        );
    }

    @Transactional
    public void deleteTravel(UUID userId, UUID travelId) {
        Travel travel = getEditableTravelOrThrow(userId, travelId);
        travelRepository.delete(travel);
    }

    public Travel getReadableTravelOrThrow(UUID userId, UUID travelId) {
        Travel travel = travelRepository.findWithOwnerById(travelId)
                .orElseThrow(TravelNotFoundException::new);

        boolean isOwner = travel.getOwner().getId().equals(userId);
        boolean isParticipant = travelParticipantRepository.existsByTravelIdAndUserId(travelId, userId);

        if (isOwner || isParticipant) {
            return travel;
        }

        return switch (travel.getVisibility()) {
            case PUBLIC -> travel;
            case FOLLOWER -> {
                boolean followsOwner = followRepository.existsByFollowerIdAndFollowingId(userId, travel.getOwner().getId());
                if (!followsOwner) {
                    throw new TravelException(TravelErrorCode.READ_ACCESS_DENIED);
                }
                yield travel;
            }
            case PRIVATE -> throw new TravelException(TravelErrorCode.READ_ACCESS_DENIED);
        };
    }

    public Travel getEditableTravelOrThrow(UUID userId, UUID travelId) {
        Travel travel = travelRepository.findWithOwnerById(travelId)
                .orElseThrow(TravelNotFoundException::new);

        boolean isOwner = travel.getOwner().getId().equals(userId);
        if (isOwner) {
            return travel;
        }

        TravelParticipant participant = travelParticipantRepository.findByTravelIdAndUserId(travelId, userId)
                .orElseThrow(() -> new TravelException(TravelErrorCode.EDIT_ACCESS_DENIED));

        if (participant.getStatus() != TravelParticipantStatus.ACCEPTED) {
            throw new TravelException(TravelErrorCode.EDIT_ACCESS_DENIED);
        }
        if (participant.getRole() == TravelParticipantRole.VIEWER) {
            throw new TravelException(TravelErrorCode.EDIT_ACCESS_DENIED);
        }

        return travel;
    }

    private void autoCreateTravelDays(Travel travel, LocalDate startDate, LocalDate endDate) {
        long totalDays = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        for (int i = 0; i < totalDays; i++) {
            TravelDay day = TravelDay.builder()
                    .travel(travel)
                    .orderNo(i + 1)
                    .travelDate(startDate.plusDays(i))
                    .build();
            travelDayRepository.save(day);
            travel.addDay(day);
        }
    }

    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new TravelException(TravelErrorCode.INVALID_DATE_RANGE);
        }
    }
}
