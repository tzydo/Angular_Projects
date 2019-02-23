package com.pl.skijumping.service;

import com.pl.skijumping.diagnosticmonitor.DiagnosticMonitor;
import com.pl.skijumping.domain.entity.DataRace;
import com.pl.skijumping.domain.repository.DataRaceRepository;
import com.pl.skijumping.dto.CompetitionTypeDTO;
import com.pl.skijumping.service.mapper.CompetitionTypeMapper;
import com.pl.skijumping.service.mapper.DataRaceMapper;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("test")
@SpringBootTest(classes = ApplicationTest.class)
public class DataRaceServiceTest {
    @Autowired
    private DataRaceRepository dataRaceRepository;
    @Autowired
    private DataRaceMapper dataRaceMapper;
    @MockBean
    private DiagnosticMonitor diagnosticMonitor;
    @Autowired
    private CompetitionTypeService competitionTypeService;
    @Autowired
    private CompetitionTypeMapper competitionTypeMapper;


    @Test
    @Transactional
    public void findByDataRaceTest() {
        CompetitionTypeDTO competitionTypeDTO = competitionTypeService.save(new CompetitionTypeDTO(null, "type", null));

        DataRace dataRace = new DataRace()
                .date(LocalDate.now())
                .city("city")
                .shortCountryName("pol")
                .competitionType(competitionTypeMapper.fromDTO(competitionTypeDTO))
                .raceId(1L);

        dataRace = dataRaceRepository.save(dataRace);


        dataRaceRepository.save(new DataRace()
                .date(LocalDate.now())
                .city("city2")
                .shortCountryName("pol2")
                .competitionType(competitionTypeMapper.fromDTO(competitionTypeDTO))
                .raceId(2L));


        dataRaceRepository.save(new DataRace()
                .date(LocalDate.now())
                .city("city3")
                .shortCountryName("pol3")
                .competitionType(competitionTypeMapper.fromDTO(competitionTypeDTO))
                .raceId(3L));


        DataRaceService dataRaceService = new DataRaceService(dataRaceRepository,
                dataRaceMapper, diagnosticMonitor);
        Optional<DataRace> actualDataRace = dataRaceService.findByDataRace(dataRace);
        Assertions.assertThat(actualDataRace.isPresent()).isTrue();
        Assertions.assertThat(actualDataRace.get()).isEqualToComparingFieldByFieldRecursively(dataRace);
    }

    @Test
    public void findByDataRaceWhenDoesNotExistTest() {
        DataRace dataRace = new DataRace()
                .date(LocalDate.now())
                .city("city")
                .shortCountryName("pol")
                .raceId(1L);


        DataRaceService dataRaceService = new DataRaceService(dataRaceRepository,
                dataRaceMapper, diagnosticMonitor);
        Optional<DataRace> actualDataRace = dataRaceService.findByDataRace(dataRace);
        Assertions.assertThat(actualDataRace.isPresent()).isFalse();
        Assertions.assertThat(actualDataRace).isEqualTo(Optional.empty());
    }

//    @Test
//    @Transactional
//    public void getRaceDataIdListTest() {
//        dataRaceRepository.save(new DataRace(null, LocalDate.now(), "test", "t", null, null, 1L, null));
//
//        dataRaceRepository.save(new DataRace(null, LocalDate.now(), "test", "t", null, null, 2L, null));
//
//        List<Long> actualRaceDataList = dataRaceRepository.getRaceDataList();
//        Assertions.assertThat(actualRaceDataList).hasSize(2);
//        Assertions.assertThat(actualRaceDataList).containsAll(Arrays.asList(1L, 2L));
//    }
}
