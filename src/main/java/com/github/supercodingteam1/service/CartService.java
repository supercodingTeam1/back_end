package com.github.supercodingteam1.service;

import com.github.supercodingteam1.repository.cart.Cart;
import com.github.supercodingteam1.repository.cart.CartRepository;
import com.github.supercodingteam1.repository.item.Item;
import com.github.supercodingteam1.repository.item.ItemRepository;
import com.github.supercodingteam1.repository.option.Option;
import com.github.supercodingteam1.repository.option.OptionRepository;
import com.github.supercodingteam1.repository.user.User;
import com.github.supercodingteam1.repository.user.UserRepository;
import com.github.supercodingteam1.web.dto.AddToCartDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final ItemRepository itemRepository;
    private final OptionRepository optionRepository;
    private final UserRepository userRepository;

    public void addItemToCart(AddToCartDTO addToCartDTO) {
        Option option = optionRepository.findById(addToCartDTO.getOption_id())
                .orElseThrow(null);
        Item item = option.getItem();
        Cart newCart = Cart.builder()
                .cartQuantity(addToCartDTO.getQuantity())
                .user(userRepository.findById(4).orElseThrow(null))
                .build();

        cartRepository.save(newCart);
    }
}
