package com.github.supercodingteam1.service;

import com.github.supercodingteam1.repository.entity.option.OptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OptionService {
    private final OptionRepository optionRepository;


}
