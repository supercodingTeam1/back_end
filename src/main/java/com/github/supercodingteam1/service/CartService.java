package com.github.supercodingteam1.service;

import com.github.supercodingteam1.repository.entity.cart.Cart;
import com.github.supercodingteam1.repository.entity.cart.CartRepository;
import com.github.supercodingteam1.repository.entity.item.Item;
import com.github.supercodingteam1.repository.entity.item.ItemRepository;
import com.github.supercodingteam1.repository.entity.option.Option;
import com.github.supercodingteam1.repository.entity.option.OptionRepository;
import com.github.supercodingteam1.repository.entity.option_cart.OptionCart;
import com.github.supercodingteam1.repository.entity.option_cart.OptionCartRepository;
import com.github.supercodingteam1.repository.entity.order.Order;
import com.github.supercodingteam1.repository.entity.order.OrderRepository;
import com.github.supercodingteam1.repository.entity.user.User;
import com.github.supercodingteam1.repository.entity.user.UserRepository;
import com.github.supercodingteam1.web.dto.AddToCartDTO;
import com.github.supercodingteam1.web.dto.DeleteCartDTO;
import com.github.supercodingteam1.web.dto.ModifyCartDTO;
import com.github.supercodingteam1.web.dto.OrderDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final ItemRepository itemRepository;
    private final OptionRepository optionRepository;
    private final UserRepository userRepository;
    private final OptionCartRepository optionCartRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public void addItemToCart(AddToCartDTO addToCartDTO, HttpServletRequest httpServletRequest) {
        //TODO : 카트 담을 때 같은 아이템의 같은 옵션을 또 장바구니에 담으면 quantity 만큼만 수량 증가하고 메소드 종료
        //TODO : httpServletRequest에서 헤더 가져와서 token 파싱하여 user 가져와야함.

        User user = userRepository.findById(6).orElse(null);
        Option option = optionRepository.findById(addToCartDTO.getOption_id()).orElse(null);
        Integer quantity = addToCartDTO.getQuantity();

        List<Cart> userCartList = cartRepository.findAllByUser(user); //user의 cart 목록 전부 가져오기

        Cart cart = null;
        OptionCart optionCart = null;

        for(Cart existingCart : userCartList) {
            optionCart = optionCartRepository.findByOptionAndCart(option, existingCart); //optionCart table에서 option과 cart가 일치하는지 확인
            if(optionCart != null) { //일치하는 정보 있으면
                cart = existingCart; //cart에 해당 카트 정보 대입
                break;
            }
        }

        if(cart == null) {
            cart = Cart.builder()
                    .cartQuantity(quantity)
                    .user(user)
                    .build();
            cartRepository.save(cart);
        }

        if(optionCart == null) { //optionCart가 없으면 새로 생성
            optionCart = OptionCart.builder()
                    .option(option)
                    .cart(cart)
                    .build();
            optionCartRepository.save(optionCart);
        }else { //optionCart가 있으면 생성된 cart의 quantity만 증가
            optionCart.getCart().setCartQuantity(optionCart.getCart().getCartQuantity() + quantity);
            optionCartRepository.save(optionCart);
        }
    }

    @Transactional
    public void modifyCartItem(ModifyCartDTO modifyCartDTO, HttpServletRequest httpServletRequest) {
        //TODO : 사용자가 입력한 옵션이나 수량대로 Cart의 quantity 또는 optionCart의 option을 변경
        //TODO : httpServletRequest에서 헤더 가져와서 토큰 파싱해서 user 가져와야함

        User user = userRepository.findById(6).orElse(null);

        Option option = optionRepository.findById(modifyCartDTO.getOption_id()).orElse(null);
        Integer quantity = modifyCartDTO.getQuantity();

        Cart cart = cartRepository.findById(modifyCartDTO.getCart_id()).orElse(null);

        OptionCart optionCart = optionCartRepository.findByCart(cart);

        Objects.requireNonNull(optionCart).setOption(option);
        optionCart.getCart().setCartQuantity(quantity);

        cartRepository.save(Objects.requireNonNull(cart));
        optionCartRepository.save(optionCart);
    }

    @Transactional
    public void deleteCartItem(DeleteCartDTO deleteCartDTO, User user) {
        //TODO : user 관련하여 현재 멈춰있는 상태.
        Cart cart = cartRepository.findById(deleteCartDTO.getCart_id()).orElse(null);

        cartRepository.delete(cart);
    }

    @Transactional
    public void orderCartItem(OrderDTO orderDTO) {
        //TODO : 장바구니에 담긴 물품 주문 시 option에 stock 조정, order테이블에 주문기록 저장
        String orderNum = generateOrderId(); //주문번호 생성

        // 1. 주문한 물품 정보 가져오기
        // 2. 주문한 물품이 cart에 들어있는지 확인
        Cart cart = cartRepository.findById(orderDTO.getCartId()).orElse(null);
        OptionCart optionCart = optionCartRepository.findByCart(cart);
        Option option = optionCart.getOption();
        Item item = optionCart.getOption().getItem();

        // 3. 주문한 item에 해당하는 option찾아서 stock 감소
        // 4. 주문한 item의 totalsales 증가
        Integer quantity = cart.getCartQuantity();
        item.setTotalSales(item.getTotalSales() + quantity);
        option.setStock(option.getStock() - quantity);

        if(option.getStock() < 0)
            throw new IllegalArgumentException(String.format("재고보다 주문한 수량이 많습니다. 현재 재고: %d", option.getStock()));

//        itemRepository.save(item);
//        optionRepository.save(option);

        // 5. Order 엔티티 생성하여 DB에 저장
        Order order = Order.builder()
                .orderNum(orderNum)
                .cart(cart)
                .orderAt(LocalDateTime.now())
                .name(orderDTO.getName())
                .payment(orderDTO.getPayment())
                .orderAddress(orderDTO.getAddress())
                .phoneNum(orderDTO.getPhone_num())
                .build();

//        orderRepository.save(order);
    }

    //주문번호는 날짜(yyyyMMdd) + 현재시간(HHmmssSSS) 으로 구성
    private String generateOrderId() {
        // 현재 날짜와 시간을 가져옵니다.
        LocalDate currentDate = LocalDate.now(); // YYYY-MM-DD 형태
        LocalTime currentTime = LocalTime.now(); // HH:MM:SS 형태

        // 날짜를 "yyyyMMdd" 형식으로 포맷팅
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String date = currentDate.format(dateFormatter);

        // 시간을 "HHmmssSSS" 형식으로 포맷팅 (밀리초까지 포함한 8자리 시간)
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HHmmssSSS");
        String time = currentTime.format(timeFormatter);

        // 날짜와 시간을 결합하여 주문 번호 생성
        String orderNumber = date + time;

        return orderNumber;
    }
}
