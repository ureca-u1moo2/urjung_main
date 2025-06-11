package com.eureka.ip.team1.urjung_main.user.service;

import com.eureka.ip.team1.urjung_main.common.exception.InternalServerErrorException;
import com.eureka.ip.team1.urjung_main.user.dto.UsageRequestDto;
import com.eureka.ip.team1.urjung_main.user.dto.UsageResponseDto;
import com.eureka.ip.team1.urjung_main.user.repository.UsageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsageServiceImpl implements UsageService {

    private final UsageRepository usageRepository;

    @Override
    public List<UsageResponseDto> getAllUsagesByUserId(UsageRequestDto usageRequestDto) {
        try {
            return usageRepository.findAllUsagesByUserId(usageRequestDto.getUserId());
        } catch (Exception e) {
            log.info("error : {}", e.getMessage());
            throw new InternalServerErrorException();
        }
    }

    @Override
    public List<UsageResponseDto> getAllUsagesByUserIdAndMonth(UsageRequestDto usageRequestDto) {
        try {
            return usageRepository.findAllUsagesByUserIdAndMonth(
                    usageRequestDto.getUserId(),
                    usageRequestDto.getYear(),
                    usageRequestDto.getMonth()
            );
        } catch (Exception e) {
            log.info("error : {}", e.getMessage());
            throw new InternalServerErrorException();
        }
    }

    @Override
    public List<UsageResponseDto> getCurrentMonthUsagesByUserId(UsageRequestDto usageRequestDto) {
        try {
            return usageRepository.findCurrentMonthUsagesByUserId(usageRequestDto.getUserId());
        } catch (Exception e) {
            log.info("error : {}", e.getMessage());
            throw new InternalServerErrorException();
        }
    }

    @Override
    public Optional<UsageResponseDto> getUsageByLineIdAndMonth(UsageRequestDto usageRequestDto) {
        try {
            return usageRepository.findUsageByLineIdAndMonth(
                    usageRequestDto.getLineId(),
                    usageRequestDto.getYear(),
                    usageRequestDto.getMonth()
            );
        } catch (Exception e) {
            log.info("error : {}", e.getMessage());
            throw new InternalServerErrorException();
        }
    }

    @Override
    public Optional<UsageResponseDto> getUsageByUserIdAndPlanIdAndMonth(UsageRequestDto usageRequestDto) {
        try {
            return usageRepository.findUsageByUserIdAndPlanIdAndMonth(
                    usageRequestDto.getUserId(),
                    usageRequestDto.getPlanId(),
                    usageRequestDto.getYear(),
                    usageRequestDto.getMonth()
            );
        } catch (Exception e) {
            log.info("error : {}", e.getMessage());
            throw new InternalServerErrorException();
        }
    }
}
