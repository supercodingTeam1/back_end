package com.github.supercodingteam1.repository.image;

import com.github.supercodingteam1.repository.item.Item;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "images")
@Entity
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id", nullable = false, length = 11)
    private Integer imageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Column(name = "image_link",nullable = false, length = 255)
    private String imageLink;

    @Column(name="image_first", nullable = false)
    private Boolean imageFirst;

}

/*
JPA에서는 JDBC( Mybatis )를 사용했을 때와 달리 연관 관계에 있는 상대 테이블의 PK를 멤버변수로 갖지 않고, 엔티티 객체 자체를 통째로 참조합니다.
https://victorydntmd.tistory.com/208
 */
