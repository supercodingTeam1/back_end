package com.github.supercodingteam1.service;

import com.github.supercodingteam1.repository.cart.Cart;
import com.github.supercodingteam1.repository.cart.CartRepository;
import com.github.supercodingteam1.repository.item.Item;
import com.github.supercodingteam1.repository.item.ItemRepository;
import com.github.supercodingteam1.repository.option.Option;
import com.github.supercodingteam1.repository.option.OptionRepository;
import com.github.supercodingteam1.repository.option_cart.OptionCart;
import com.github.supercodingteam1.repository.option_cart.OptionCartRepository;
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
    private final OptionCartRepository optionCartRepository;

    public void addItemToCart(AddToCartDTO addToCartDTO) {
        Option option = optionRepository.findById(addToCartDTO.getOption_id())
                .orElseThrow(null);
        Cart newCart = Cart.builder()
                .cartQuantity(addToCartDTO.getQuantity())
                .user(userRepository.findById(4).orElseThrow(null))
                .build();
        Item item = option.getItem();
        OptionCart optionCart = OptionCart.builder()
                        .cart(newCart)
                        .option(option)
                        .build();

        cartRepository.save(newCart);
        optionCartRepository.save(optionCart);

        System.out.println("option_id : "+option.getOptionId());
        System.out.println("option_name : "+option.getSize());
        System.out.println("item_image : "+item.getImageList().get(0).getImageLink());
        System.out.println("item_name : "+item.getItemName());
        System.out.println("quantity : "+newCart.getCartQuantity());
        System.out.println("price : "+item.getItemPrice());

    }
}
