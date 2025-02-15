package pl.byczazagroda.trackexpensesappbackend.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.byczazagroda.trackexpensesappbackend.dto.FinancialTransactionCategoryDTO;
import pl.byczazagroda.trackexpensesappbackend.dto.FinancialTransactionCategoryUpdateDTO;
import pl.byczazagroda.trackexpensesappbackend.exception.AppRuntimeException;
import pl.byczazagroda.trackexpensesappbackend.exception.ErrorCode;
import pl.byczazagroda.trackexpensesappbackend.mapper.FinancialTransactionCategoryModelMapper;
import pl.byczazagroda.trackexpensesappbackend.model.FinancialTransactionCategory;
import pl.byczazagroda.trackexpensesappbackend.model.FinancialTransactionType;
import pl.byczazagroda.trackexpensesappbackend.repository.FinancialTransactionCategoryRepository;

import java.time.Instant;
import java.util.Optional;

import static java.util.Optional.empty;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FTCategoryUpdateServiceImplTest {

    private static final long VALID_ID = 1L;

    private static final long INVALID_ID = 10L;

    private static final String NAME = "test_name";

    private static final FinancialTransactionType TYPE_INCOME = FinancialTransactionType.INCOME;

    private static final FinancialTransactionCategory VALID_CATEGORY
            = FinancialTransactionCategory.builder()
            .id(VALID_ID)
            .name(NAME)
            .type(TYPE_INCOME)
            .creationDate(Instant.now())
            .build();

    private static final FinancialTransactionCategoryDTO VALID_CATEGORY_DTO
            = new FinancialTransactionCategoryDTO(VALID_ID, NAME, TYPE_INCOME);

    private static final FinancialTransactionCategoryUpdateDTO VALID_UPDATE_CATEGORY_DTO
            = new FinancialTransactionCategoryUpdateDTO(NAME, TYPE_INCOME);

    @Mock
    private FinancialTransactionCategoryRepository repository;

    @Mock
    private FinancialTransactionCategoryModelMapper mapper;

    @InjectMocks
    private FinancialTransactionCategoryServiceImpl service;

    @Test
    @DisplayName("update the FT category and return the category object if the ID is correct")
    void testUpdateFTCategoryById_WhenIdIsCorrect_ThenReturnCategoryEntity() {
        when(repository.findById(VALID_ID)).thenReturn(Optional.of(VALID_CATEGORY));
        when(mapper.mapFinancialTransactionCategoryEntityToFinancialTransactionCategoryDTO(any())).thenReturn(VALID_CATEGORY_DTO);
        FinancialTransactionCategoryDTO dto
                = service.updateFinancialTransactionCategory(VALID_ID, VALID_UPDATE_CATEGORY_DTO);
        assertEquals(dto, VALID_CATEGORY_DTO);
    }

    @Test
    @DisplayName("do not update FT Category if id is incorrect then throw AppRuntimeException and does not update entity")
    void testUpdateFTCategoryById_WhenIdIsIncorrect_ThenThrowError() {
        when(repository.findById(INVALID_ID)).thenReturn(empty());
        AppRuntimeException exception = assertThrows(
                AppRuntimeException.class,
                () -> service.updateFinancialTransactionCategory(INVALID_ID, VALID_UPDATE_CATEGORY_DTO)
        );
        assertEquals(ErrorCode.FTC001.getBusinessStatusCode(), exception.getBusinessStatusCode());
        verify(repository, never()).save(any());
    }

}
