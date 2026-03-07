package com.github.triprooty.domain;

import com.github.triprooty.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "travel_days",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_travel_day_travel_order_no", columnNames = {"travel_id", "order_no"}),
                @UniqueConstraint(name = "uk_travel_day_travel_date", columnNames = {"travel_id", "travel_date"})
        }
)
public class TravelDay extends BaseEntity {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "travel_day_id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "travel_id", nullable = false)
    private Travel travel;

    @Column(name = "order_no", nullable = false)
    private Integer orderNo;

    @Column(name = "travel_date", nullable = false)
    private LocalDate travelDate;

    @OneToMany(mappedBy = "travelDay", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderNo ASC")
    private List<ItineraryItem> itineraryItems = new ArrayList<>();

    @Builder
    public TravelDay(Travel travel, Integer orderNo, LocalDate travelDate) {
        this.travel = travel;
        this.orderNo = orderNo;
        this.travelDate = travelDate;
    }

    public void update(Integer orderNo, LocalDate travelDate) {
        this.orderNo = orderNo;
        this.travelDate = travelDate;
    }

    public void addItem(ItineraryItem item) {
        itineraryItems.add(item);
    }
}
