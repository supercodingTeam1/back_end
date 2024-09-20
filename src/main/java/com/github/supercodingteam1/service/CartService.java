package com.github.supercodingteam1.service;

import com.github.supercodingteam1.repository.entity.cart.Cart;
import com.github.supercodingteam1.repository.entity.cart.CartRepository;
import com.github.supercodingteam1.repository.entity.item.ItemRepository;
import com.github.supercodingteam1.repository.entity.option.Option;
import com.github.supercodingteam1.repository.entity.option.OptionRepository;
import com.github.supercodingteam1.repository.entity.option_cart.OptionCart;
import com.github.supercodingteam1.repository.entity.option_cart.OptionCartRepository;
import com.github.supercodingteam1.repository.entity.user.User;
import com.github.supercodingteam1.repository.entity.user.UserRepository;
import com.github.supercodingteam1.web.dto.AddToCartDTO;
import com.github.supercodingteam1.web.dto.DeleteCartDTO;
import com.github.supercodingteam1.web.dto.ModifyCartDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    public void addItemToCart(AddToCartDTO addToCartDTO) {
        //TODO : 카트 담을 때 같은 아이템의 같은 옵션을 또 장바구니에 담으면 quantity 만큼만 수량 증가하고 메소드 종료

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

    public void modifyCartItem(ModifyCartDTO modifyCartDTO, User user) {
        //TODO : 사용자가 입력한 옵션이나 수량대로 Cart의 quantity 또는 optionCart의 option을 변경
        Option option = optionRepository.findById(modifyCartDTO.getOption_id()).orElse(null);
        Integer quantity = modifyCartDTO.getQuantity();

        List<Cart> userCartList = cartRepository.findAllByUser(user);

        Cart cart = null;
        OptionCart optionCart = null;

        for(Cart existingCart : userCartList) {
            optionCart = optionCartRepository.findByOptionAndCart(option, existingCart); //optionCart table에서 option과 cart가 일치하는지 확인
            if(optionCart != null) { //일치하는 정보 있으면
                cart = existingCart; //cart에 해당 카트 정보 대입
                break;
            }
        }

        Objects.requireNonNull(optionCart).setOption(option);
        optionCart.getCart().setCartQuantity(quantity);

        optionCartRepository.save(optionCart);
    }

    public void deleteCartItem(DeleteCartDTO deleteCartDTO, User user) {
        //TODO : user 관련하여 현재 멈춰있는 상태.
        Option option = optionRepository.findById(deleteCartDTO.getOption_id()).orElse(null);
        OptionCart optionCart = optionCartRepository.findByOptionAndCart_User(option,user);

        Cart cart = optionCart.getCart();

        cartRepository.delete(cart);
    }
}
