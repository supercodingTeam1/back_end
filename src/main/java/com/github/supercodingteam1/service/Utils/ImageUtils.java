package com.github.supercodingteam1.service.Utils;

import com.github.supercodingteam1.repository.entity.image.Image;
import com.github.supercodingteam1.repository.entity.item.Item;
import org.springframework.context.annotation.Bean;

import static com.amazonaws.services.ec2.model.ResourceType.Image;

public class ImageUtils {
    public static String getMainImageUrl(Item item) {
        return item.getImageList().stream()
                .filter(com.github.supercodingteam1.repository.entity.image.Image::getImageFirst)
                .map(com.github.supercodingteam1.repository.entity.image.Image::getImageLink)
                .findFirst()
                .orElse(null);
    }
}
