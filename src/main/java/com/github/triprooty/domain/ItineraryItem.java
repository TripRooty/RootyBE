package com.github.triprooty.domain;

import com.github.triprooty.domain.common.BaseEntity;
import com.github.triprooty.domain.enums.ItineraryItemType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "itinerary_items",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_itinerary_item_day_order_no", columnNames = {"travel_day_id", "order_no"})
        }
)
public class ItineraryItem extends BaseEntity {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "itinerary_item_id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "travel_day_id", nullable = false)
    private TravelDay travelDay;

    @Enumerated(EnumType.STRING)
    @Column(name = "item_type", nullable = false, length = 30)
    private ItineraryItemType itemType;

    @Column(name = "order_no", nullable = false)
    private Integer orderNo;

    @Column(name = "memo", length = 1000)
    private String memo;

    @SuppressWarnings("unused") // JPA relationship field
    @OneToOne(mappedBy = "itineraryItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private PlaceVisit placeVisit;

    @SuppressWarnings("unused") // JPA relationship field
    @OneToOne(mappedBy = "itineraryItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private MoveSegment moveSegment;

    @Builder
    @SuppressWarnings("unused")
    public ItineraryItem(TravelDay travelDay, ItineraryItemType itemType, Integer orderNo, String memo) {
        this.travelDay = travelDay;
        this.itemType = itemType;
        this.orderNo = orderNo;
        this.memo = memo;
    }

    public void changeOrderNo(int orderNo) {
        this.orderNo = orderNo;
    }

    public void updateMemo(String memo) {
        this.memo = memo;
    }
}
