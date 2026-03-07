package com.github.triprooty.domain;

import com.github.triprooty.domain.common.BaseEntity;
import com.github.triprooty.domain.enums.TravelVisibility;
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
@Table(name = "travels")
public class Travel extends BaseEntity {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "travel_id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "visibility", nullable = false, length = 30)
    private TravelVisibility visibility;

    @OneToMany(mappedBy = "travel", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderNo ASC")
    private List<TravelDay> days = new ArrayList<>();

    @Builder
    public Travel(User owner,
                  String title,
                  String description,
                  LocalDate startDate,
                  LocalDate endDate,
                  TravelVisibility visibility) {
        this.owner = owner;
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.visibility = visibility;
    }

    public void update(String title, String description, LocalDate startDate, LocalDate endDate, TravelVisibility visibility) {
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.visibility = visibility;
    }

    public void addDay(TravelDay travelDay) {
        days.add(travelDay);
    }
}
