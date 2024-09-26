package com.github.supercodingteam1.service;

import com.github.supercodingteam1.repository.UserDetails.CustomUserDetails;
import com.github.supercodingteam1.repository.entity.cart.Cart;
import com.github.supercodingteam1.repository.entity.cart.CartRepository;
import com.github.supercodingteam1.repository.entity.item.Item;
import com.github.supercodingteam1.repository.entity.item.ItemRepository;
import com.github.supercodingteam1.repository.entity.option.Option;
import com.github.supercodingteam1.repository.entity.option.OptionRepository;
import com.github.supercodingteam1.repository.entity.optionCart.OptionCart;
import com.github.supercodingteam1.repository.entity.optionCart.OptionCartRepository;
import com.github.supercodingteam1.repository.entity.order.Order;
import com.github.supercodingteam1.repository.entity.orderDetail.OrderDetail;
import com.github.supercodingteam1.repository.entity.order.OrderRepository;
import com.github.supercodingteam1.repository.entity.orderDetail.OrderDetailRepository;
import com.github.supercodingteam1.repository.entity.user.User;
import com.github.supercodingteam1.repository.entity.user.UserRepository;
import com.github.supercodingteam1.service.Utils.ImageUtils;
import com.github.supercodingteam1.service.security.CustomUserDetailService;
import com.github.supercodingteam1.web.dto.*;
import com.github.supercodingteam1.web.exceptions.NotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartService {
    private final CartRepository cartRepository;
    private final ItemRepository itemRepository;
    private final OptionRepository optionRepository;
    private final UserRepository userRepository;
    private final OptionCartRepository optionCartRepository;
    private final OrderRepository orderRepository;
    private final CustomUserDetailService customUserDetailService;
    private final OrderDetailRepository orderDetailRepository;

    public List<CartResponseDTO> getAllCartItem(CustomUserDetails userDetails) throws NotFoundException {
        Integer userId = userDetails.getUserId();
        User user = userRepository.findByUserId(userId);

        List<OptionCart> userOptionCartList = optionCartRepository.findAllByCart_User(user);
        if (userOptionCartList.isEmpty()) {
            throw new NotFoundException("장바구니가 비었습니다.");
        }

        List<CartResponseDTO> cartResponseList = new ArrayList<>();

        for (OptionCart existingCart : userOptionCartList) {

            Item item= existingCart.getOption().getItem();
            String mainImageUrl= ImageUtils.getMainImageUrl(item);

            CartResponseDTO cartResponseDTO = CartResponseDTO.builder()
                    .cart_id(existingCart.getCart().getCartId())
                    .option_id(existingCart.getOption().getOptionId())
                    .size(existingCart.getOption().getSize())
                    .item_image(mainImageUrl)
                    .item_name(existingCart.getOption().getItem().getItemName())
                    .quantity(existingCart.getCart().getCartQuantity())
                    .price(existingCart.getOption().getItem().getItemPrice())
                    .build();

            cartResponseList.add(cartResponseDTO);
        }

        return cartResponseList;
    }

    @Transactional
    public void addItemToCart(AddToCartDTO addToCartDTO, CustomUserDetails customUserDetails) {
        //TODO : 카트 담을 때 같은 아이템의 같은 옵션을 또 장바구니에 담으면 quantity 만큼만 수량 증가하고 메소드 종료
        //TODO : httpServletRequest에서 헤더 가져와서 token 파싱하여 user 가져와야함.
        String email = customUserDetails.getEmail();

        User user = userRepository.findByEmail(email).orElse(null);

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
    public void modifyCartItem(ModifyCartDTO modifyCartDTO, CustomUserDetails customUserDetails) {
        //TODO : 사용자가 입력한 옵션이나 수량대로 Cart의 quantity 또는 optionCart의 option을 변경
        //TODO : httpServletRequest에서 헤더 가져와서 토큰 파싱해서 user 가져와야함

        String email = customUserDetails.getEmail();

        User user = userRepository.findByEmail(email).orElse(null);

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
    public void deleteCartItem(DeleteCartDTO deleteCartDTO, CustomUserDetails customUserDetails) {
        //TODO : user 관련하여 현재 멈춰있는 상태.
        String email = customUserDetails.getEmail();
        User user = userRepository.findByEmail(email).orElse(null);
        List<Cart> userCartList = cartRepository.findAllByUser(user);

        Cart cart = userCartList.stream().filter(cartItem -> Objects.equals(cartItem.getCartId(), deleteCartDTO.getCart_id())).findFirst().orElse(null);

        cartRepository.delete(cart);
    }

    @Transactional
    public void orderItem(OrderDTO orderDTO, CustomUserDetails customUserDetails) throws Exception {
        //TODO : 물품 주문 시 option에 stock 조정, order 테이블에 주문기록 저장

        try {
            //현재 인증된 user 정보 가져오기
            String email = customUserDetails.getEmail();
            User user = userRepository.findByEmail(email).orElse(null);

            String orderNum = generateOrderId(); //주문번호 생성

            Boolean isFromCart = orderDTO.getIsFromCart();

            // 1. 주문한 물품 정보 가져오기
            List<MyOrderItemDTO> myOrderItemDTOList = orderDTO.getItems(); //주문 아이템 정보 가져와서
            List<OrderDetail> orderDetailList = new ArrayList<>();

            Integer totalPrice = 0;

            for(MyOrderItemDTO myOrderItemDTO : myOrderItemDTOList) { //OrderDetail 객체를 만들기 위함
                OrderDetail orderDetail = OrderDetail.builder()
                        .options(optionRepository.findById(myOrderItemDTO.getOption_id()).orElse(null))
                        .quantity(myOrderItemDTO.getQuantity())
                        .build();
                Item item = optionRepository.findById(myOrderItemDTO.getOption_id()).get().getItem();
                totalPrice += item.getItemPrice() * myOrderItemDTO.getQuantity();
                orderDetailList.add(orderDetail);
            }

            // 5. Order 엔티티 생성하여 DB에 저장
            Order order = Order.builder()
                    .user(user)
                    .orderNum(orderNum)
                    .orderAt(LocalDateTime.now())
                    .name(orderDTO.getName())
                    .payment(orderDTO.getPayment())
                    .orderAddress(orderDTO.getAddress())
                    .phoneNum(orderDTO.getPhone_num())
                    .totalPrice(totalPrice)
                    .build();

            orderRepository.save(order);

            // 3. 주문한 item에 해당하는 option찾아서 stock 감소
            for(OrderDetail orderDetail : orderDetailList) {
                Option option = orderDetail.getOptions();

                //주문한 수량이 재고보다 많으면
                if(option.getStock() < orderDetail.getQuantity()) {
                    System.out.println("재고보다 주문한 수량이 많습니다. 현재재고 : " + option.getStock());
                    throw new IllegalArgumentException(String.format("재고보다 주문한 수량이 많습니다. 현재 재고: %d", option.getStock()));
                }

                option.setStock(option.getStock() - orderDetail.getQuantity());

                Item item = option.getItem();
                item.setTotalSales(item.getTotalSales() + orderDetail.getQuantity());

                orderDetail.setOrder(order);

                itemRepository.save(item);
                optionRepository.save(option);
            }

            orderDetailRepository.saveAll(orderDetailList);

            // 2. 장바구니에서 주문한 것인지?(true) / 바로구매로 주문한 것인지?(false)
            if(isFromCart){ //장바구니에서 주문한것이면
                List<OptionCart> optionCartList = optionCartRepository.findAllByCart_User(user);
                List<Cart> cartList = optionCartList.stream().map(OptionCart::getCart).toList();
                cartRepository.deleteAll(cartList);
            }else{ //바로구매 누른것이면 cart가 없으니까 새로 만들어야함.
                System.out.println("바로구매 + " + orderDTO);
            }
        }catch (IllegalArgumentException e){
            log.error("재고보다 주문한 수량이 많습니다.");
            throw new IllegalArgumentException("재고보다 주문한 수량이 많습니다.");
        }catch (Exception e){
            log.error(e.getMessage());
            throw new Exception(e.getMessage());
        }

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
        return date + time;
    }
}
