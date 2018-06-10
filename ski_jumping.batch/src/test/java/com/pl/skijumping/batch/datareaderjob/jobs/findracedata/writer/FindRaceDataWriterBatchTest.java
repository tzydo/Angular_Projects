package com.pl.skijumping.batch.datareaderjob.jobs.findracedata.writer;

import com.pl.skijumping.batch.BatchApplicationTest;
import com.pl.skijumping.diagnosticmonitor.DiagnosticMonitor;
import com.pl.skijumping.dto.CompetitionTypeDTO;
import com.pl.skijumping.dto.DataRaceDTO;
import com.pl.skijumping.service.CompetitionTypeService;
import com.pl.skijumping.service.DataRaceService;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("test")
@SpringBootTest(classes = BatchApplicationTest.class)
public class FindRaceDataWriterBatchTest {

    @MockBean
    private DiagnosticMonitor diagnosticMonitor;
    @Autowired
    private CompetitionTypeService competitionTypeService;
    @Autowired
    private DataRaceService dataRaceService;

    @Test
    @Transactional
    public void writeWhenNullTest() {
        List<DataRaceDTO> expectedDataRaceDTOList = dataRaceService.findAll();
        FindRaceDataWriterBatch findRaceDataWriterBatch =
                new FindRaceDataWriterBatch(competitionTypeService, dataRaceService, diagnosticMonitor);
        findRaceDataWriterBatch.write(null);
        findRaceDataWriterBatch.write(new ArrayList<>());

        List<DataRaceDTO> actualDataRaceDTOList = dataRaceService.findAll();
        Assertions.assertThat(expectedDataRaceDTOList).isEmpty();
        Assertions.assertThat(expectedDataRaceDTOList).containsAll(actualDataRaceDTOList);
    }

    @Test
    @Transactional
    public void writeTest() {
        DataRaceDTO dataRaceDTO = new DataRaceDTO().builder().raceId(1L).date(LocalDate.now()).competitionName("name").competitionType("type")
                .city("city").shortCountryName("sName").build();

        FindRaceDataWriter findRaceDataWriter = new FindRaceDataWriter(competitionTypeService,
                dataRaceService, diagnosticMonitor);
        findRaceDataWriter.write(Arrays.asList(dataRaceDTO));

        List<CompetitionTypeDTO> actualCompetitionTypeDTOList = competitionTypeService.findAll();
        Assertions.assertThat(actualCompetitionTypeDTOList).isNotEmpty();
        Assertions.assertThat(actualCompetitionTypeDTOList).hasSize(1);
        Assertions.assertThat(actualCompetitionTypeDTOList.get(0).getCompetitionName()).isEqualTo("name");
        Assertions.assertThat(actualCompetitionTypeDTOList.get(0).getCompetitionType()).isEqualTo("type");

        List<DataRaceDTO> actualDataRaceDTOList = dataRaceService.findAll();
        Assertions.assertThat(actualDataRaceDTOList).isNotEmpty();
        Assertions.assertThat(actualDataRaceDTOList).hasSize(1);

        dataRaceDTO.setId(actualCompetitionTypeDTOList.get(0).getId());
        Assertions.assertThat(actualCompetitionTypeDTOList.get(0)).isEqualToComparingFieldByFieldRecursively(dataRaceDTO);
    }
}