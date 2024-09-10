package org.keumbang.module.order.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;
import java.math.BigDecimal;

import org.keumbang.entity.BaseEntity;
import org.keumbang.module.user.entity.User;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(name = "orders")
public class Order extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(nullable = false)
	private String type;  // 구매 or 판매

	@Column(nullable = false)
	private String itemType;  // 99.9% or 99.99%

	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal quantity;

	@Column(nullable = false)
	private Long price;

	@Column(nullable = false)
	private String status;  // 주문 상태

	@Column(nullable = false)
	private String shippingAddress;

	// Order 생성일자, 상태
}


