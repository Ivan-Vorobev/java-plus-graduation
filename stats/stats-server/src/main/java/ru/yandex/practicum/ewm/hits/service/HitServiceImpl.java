package ru.yandex.practicum.ewm.hits.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.ewm.mapper.HitMapper;
import ru.yandex.practicum.dto.CreateHitDTO;
import ru.yandex.practicum.ewm.stats.model.Hit;
import ru.yandex.practicum.ewm.stats.repository.HitsRepository;

@Service
@RequiredArgsConstructor
public class HitServiceImpl implements HitService {

    private final HitsRepository hitsRepository;
    private final HitMapper hitMapper;

    @Override
    @Transactional
    public void createHit(CreateHitDTO createHitDTO) {
        Hit newHit = hitMapper.mapToHit(createHitDTO);
        hitsRepository.save(newHit);
    }
}
