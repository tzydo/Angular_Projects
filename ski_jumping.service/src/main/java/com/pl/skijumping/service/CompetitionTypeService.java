package com.pl.skijumping.service;

import com.pl.skijumping.domain.entity.CompetitionType;
import com.pl.skijumping.domain.entity.QCompetitionType;
import com.pl.skijumping.domain.repository.CompetitionTypeRepository;
import com.pl.skijumping.dto.CompetitionTypeDTO;
import com.pl.skijumping.service.mapper.CompetitionTypeMapper;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CompetitionTypeService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CompetitionTypeService.class);
    private final CompetitionTypeRepository competitionTypeRepository;
    private final CompetitionTypeMapper competitionTypeMapper;

    public CompetitionTypeService(CompetitionTypeRepository competitionTypeRepository,
                                  CompetitionTypeMapper competitionTypeMapper) {
        this.competitionTypeRepository = competitionTypeRepository;
        this.competitionTypeMapper = competitionTypeMapper;
    }

    public CompetitionTypeDTO save(CompetitionTypeDTO competitionTypeDTO) {
        if (competitionTypeDTO == null) {
            LOGGER.error("Cannot save null competition type");
            return null;
        }

        CompetitionType competitionType = competitionTypeMapper.fromDTO(competitionTypeDTO);
        competitionType = competitionTypeRepository.save(competitionType);
        return competitionTypeMapper.toDTO(competitionType);
    }

    public Optional<CompetitionTypeDTO> find(Long id) {
        if (id == null) {
            LOGGER.error("Cannot find competition type for null id");
            return Optional.empty();
        }

        CompetitionType competitionType = competitionTypeRepository.findOne(id);
        if (competitionType == null) {
            return Optional.empty();
        }

        return Optional.of(competitionTypeMapper.toDTO(competitionType));
    }

    public Optional<CompetitionTypeDTO> findByTypeAndName(String type, String name) {
        if (type == null || name == null) {
            return Optional.empty();
        }

        QCompetitionType qCompetitionType = QCompetitionType.competitionType1;
        BooleanExpression pattern = qCompetitionType.competitionName.eq(name)
                .and(qCompetitionType.competitionType.eq(type));

        CompetitionType competitionType = (CompetitionType) competitionTypeRepository.findOne(pattern);
        if (competitionType == null) {
            return Optional.empty();
        }

        return Optional.of(competitionTypeMapper.toDTO(competitionType));
    }
}
