package com.github.triprooty.service.travel;

import com.github.triprooty.domain.*;
import com.github.triprooty.domain.enums.ItineraryItemType;
import com.github.triprooty.dto.request.travel.*;
import com.github.triprooty.dto.response.travel.ItineraryItemResponse;
import com.github.triprooty.global.exception.travel.*;
import com.github.triprooty.repository.travel.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItineraryService {

    private final TravelDayRepository travelDayRepository;
    private final ItineraryItemRepository itineraryItemRepository;
    private final PlaceVisitRepository placeVisitRepository;
    private final MoveSegmentRepository moveSegmentRepository;
    private final LocationRepository locationRepository;
    private final RouteCacheDomainService routeCacheDomainService;
    private final TravelService travelService;

    public List<ItineraryItemResponse> getDayItems(UUID userId, UUID travelDayId) {
        TravelDay day = travelDayRepository.findById(travelDayId)
                .orElseThrow(TravelDayNotFoundException::new);
        travelService.getReadableTravelOrThrow(userId, day.getTravel().getId());

        return itineraryItemRepository.findWithDetailByTravelDayIdOrderByOrderNoAsc(travelDayId).stream()
                .sorted(Comparator.comparing(ItineraryItem::getOrderNo))
                .map(ItineraryItemResponse::from)
                .toList();
    }

    @Transactional
    public ItineraryItemResponse addPlaceItem(UUID userId, UUID travelDayId, AddPlaceItemRequest request) {
        TravelDay day = getEditableTravelDayOrThrow(userId, travelDayId);
        shiftOrderForInsert(day.getId(), request.getOrderNo());

        ItineraryItem item = itineraryItemRepository.save(ItineraryItem.builder()
                .travelDay(day)
                .itemType(ItineraryItemType.PLACE)
                .orderNo(request.getOrderNo())
                .memo(request.getMemo())
                .build());

        Location location = resolveLocation(request.getGooglePlaceId(), request.getName(), request.getAddress(), request.getLatitude(), request.getLongitude());

        placeVisitRepository.save(PlaceVisit.builder()
                .itineraryItem(item)
                .location(location)
                .stayMinutes(request.getStayMinutes())
                .note(request.getNote())
                .build());

        ItineraryItem saved = itineraryItemRepository.findWithDetailById(item.getId())
                .orElseThrow(ItineraryItemNotFoundException::new);

        return ItineraryItemResponse.from(saved);
    }

    @Transactional
    public ItineraryItemResponse addMoveItem(UUID userId, UUID travelDayId, AddMoveItemRequest request) {
        TravelDay day = getEditableTravelDayOrThrow(userId, travelDayId);
        shiftOrderForInsert(day.getId(), request.getOrderNo());

        ItineraryItem item = itineraryItemRepository.save(ItineraryItem.builder()
                .travelDay(day)
                .itemType(ItineraryItemType.MOVE)
                .orderNo(request.getOrderNo())
                .memo(request.getMemo())
                .build());

        Location fromLocation = getLocationOrThrow(request.getFromLocationId());
        Location toLocation = getLocationOrThrow(request.getToLocationId());

        RouteCache routeCache = routeCacheDomainService.getOrResolve(
                fromLocation.getId(),
                toLocation.getId(),
                request.getTransportType()
        );

        moveSegmentRepository.save(MoveSegment.builder()
                .itineraryItem(item)
                .fromLocation(fromLocation)
                .toLocation(toLocation)
                .routeCache(routeCache)
                .transportType(request.getTransportType())
                .distanceMeters(routeCache.getDistanceMeters())
                .durationSeconds(routeCache.getDurationSeconds())
                .polyline(routeCache.getPolyline())
                .build());

        ItineraryItem saved = itineraryItemRepository.findWithDetailById(item.getId())
                .orElseThrow(ItineraryItemNotFoundException::new);

        return ItineraryItemResponse.from(saved);
    }

    @Transactional
    public List<ItineraryItemResponse> reorderItems(UUID userId, UUID travelDayId, ReorderItineraryItemsRequest request) {
        TravelDay day = getEditableTravelDayOrThrow(userId, travelDayId);
        List<ItineraryItem> items = itineraryItemRepository.findByTravelDayIdOrderByOrderNoAsc(day.getId());

        if (items.size() != request.getItemIds().size()) {
            throw new TravelException(TravelErrorCode.INVALID_ITEM_REORDER_SIZE);
        }

        Map<UUID, ItineraryItem> map = new HashMap<>();
        for (ItineraryItem item : items) {
            map.put(item.getId(), item);
        }

        for (int i = 0; i < request.getItemIds().size(); i++) {
            UUID itemId = request.getItemIds().get(i);
            ItineraryItem item = map.get(itemId);
            if (item == null) {
                throw new TravelException(TravelErrorCode.INVALID_ITEM_REORDER_ID);
            }
            item.changeOrderNo(i + 1);
        }

        return itineraryItemRepository.findWithDetailByTravelDayIdOrderByOrderNoAsc(travelDayId).stream()
                .sorted(Comparator.comparing(ItineraryItem::getOrderNo))
                .map(ItineraryItemResponse::from)
                .toList();
    }

    @Transactional
    public void deleteItem(UUID userId, UUID itemId) {
        ItineraryItem item = itineraryItemRepository.findWithDetailById(itemId)
                .orElseThrow(ItineraryItemNotFoundException::new);

        TravelDay day = getEditableTravelDayOrThrow(userId, item.getTravelDay().getId());

        int deletedOrderNo = item.getOrderNo();
        itineraryItemRepository.delete(item);

        List<ItineraryItem> remains = itineraryItemRepository.findByTravelDayIdOrderByOrderNoAsc(day.getId());
        int orderNo = 1;
        for (ItineraryItem remain : remains) {
            remain.changeOrderNo(orderNo++);
        }

        if (deletedOrderNo > remains.size() + 1) {
            throw new TravelException(TravelErrorCode.INVALID_DELETED_ORDER_NO);
        }
    }

    @Transactional
    public ItineraryItemResponse updatePlaceItem(UUID userId, UUID itemId, UpdatePlaceItemRequest request) {
        ItineraryItem item = itineraryItemRepository.findWithDetailById(itemId)
                .orElseThrow(ItineraryItemNotFoundException::new);

        if (item.getItemType() != ItineraryItemType.PLACE || item.getPlaceVisit() == null) {
            throw new TravelException(TravelErrorCode.PLACE_ITEM_TYPE_MISMATCH);
        }

        getEditableTravelDayOrThrow(userId, item.getTravelDay().getId());

        item.updateMemo(request.getMemo() == null ? item.getMemo() : request.getMemo());

        PlaceVisit placeVisit = item.getPlaceVisit();
        Location location = placeVisit.getLocation();

        if (request.getGooglePlaceId() != null && !request.getGooglePlaceId().isBlank()) {
            location = resolveLocation(
                    request.getGooglePlaceId(),
                    request.getName() == null ? placeVisit.getLocation().getName() : request.getName(),
                    request.getAddress(),
                    request.getLatitude(),
                    request.getLongitude()
            );
        }

        placeVisit.update(
                location,
                request.getStayMinutes() == null ? placeVisit.getStayMinutes() : request.getStayMinutes(),
                request.getNote() == null ? placeVisit.getNote() : request.getNote()
        );

        ItineraryItem saved = itineraryItemRepository.findWithDetailById(itemId)
                .orElseThrow(ItineraryItemNotFoundException::new);
        return ItineraryItemResponse.from(saved);
    }

    @Transactional
    public ItineraryItemResponse updateMoveItem(UUID userId, UUID itemId, UpdateMoveItemRequest request) {
        ItineraryItem item = itineraryItemRepository.findWithDetailById(itemId)
                .orElseThrow(ItineraryItemNotFoundException::new);

        if (item.getItemType() != ItineraryItemType.MOVE || item.getMoveSegment() == null) {
            throw new TravelException(TravelErrorCode.MOVE_ITEM_TYPE_MISMATCH);
        }

        getEditableTravelDayOrThrow(userId, item.getTravelDay().getId());

        item.updateMemo(request.getMemo() == null ? item.getMemo() : request.getMemo());

        MoveSegment moveSegment = item.getMoveSegment();
        Location from = request.getFromLocationId() == null
                ? moveSegment.getFromLocation()
                : getLocationOrThrow(request.getFromLocationId());
        Location to = request.getToLocationId() == null
                ? moveSegment.getToLocation()
                : getLocationOrThrow(request.getToLocationId());

        var transportType = request.getTransportType() == null ? moveSegment.getTransportType() : request.getTransportType();

        RouteCache routeCache = routeCacheDomainService.getOrResolve(from.getId(), to.getId(), transportType);

        moveSegment.update(
                from,
                to,
                routeCache,
                transportType,
                routeCache.getDistanceMeters(),
                routeCache.getDurationSeconds(),
                routeCache.getPolyline()
        );

        ItineraryItem saved = itineraryItemRepository.findWithDetailById(itemId)
                .orElseThrow(ItineraryItemNotFoundException::new);

        return ItineraryItemResponse.from(saved);
    }

    private TravelDay getEditableTravelDayOrThrow(UUID userId, UUID travelDayId) {
        TravelDay day = travelDayRepository.findById(travelDayId)
                .orElseThrow(TravelDayNotFoundException::new);

        travelService.getEditableTravelOrThrow(userId, day.getTravel().getId());
        return day;
    }

    private void shiftOrderForInsert(UUID travelDayId, Integer orderNo) {
        if (orderNo == null || orderNo <= 0) {
            throw new TravelException(TravelErrorCode.INVALID_ORDER_NO);
        }

        List<ItineraryItem> items = itineraryItemRepository.findByTravelDayIdOrderByOrderNoAsc(travelDayId);
        if (orderNo > items.size() + 1) {
            throw new TravelException(TravelErrorCode.ORDER_NO_OUT_OF_RANGE);
        }

        for (ItineraryItem item : items) {
            if (item.getOrderNo() >= orderNo) {
                item.changeOrderNo(item.getOrderNo() + 1);
            }
        }
    }

    private Location resolveLocation(String googlePlaceId, String name, String address, java.math.BigDecimal latitude, java.math.BigDecimal longitude) {
        return locationRepository.findByGooglePlaceId(googlePlaceId)
                .map(location -> {
                    location.refresh(name, address, latitude, longitude);
                    return location;
                })
                .orElseGet(() -> locationRepository.save(Location.builder()
                        .googlePlaceId(googlePlaceId)
                        .name(name)
                        .address(address)
                        .latitude(latitude)
                        .longitude(longitude)
                        .build()));
    }

    private Location getLocationOrThrow(UUID locationId) {
        return locationRepository.findById(locationId)
                .orElseThrow(LocationNotFoundException::new);
    }
}
