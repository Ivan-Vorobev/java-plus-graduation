import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.dto.HitsStatDTO;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class HitsStatDTOTests {

    private static final String VALID_APP = "ewm-main-service";
    private static final String VALID_URI = "/events/1";
    private static final Long VALID_HITS = 42L;

    private static final String INVALID_APP_BLANK = "";
    private static final String INVALID_APP_NULL = null;
    private static final String INVALID_URI_BLANK = "";
    private static final String INVALID_URI_NULL = null;
    private static final String INVALID_URI_FORMAT = "events/1";
    private static final Long INVALID_HITS_NULL = null;

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    public HitsStatDTOTests() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void tearDown() {
        if (validatorFactory != null) {
            validatorFactory.close();
        }
    }

    @Test
    void testAllFieldsValidThenNoViolations() {

        HitsStatDTO hitsStatDTO = HitsStatDTO.builder()
                .app(VALID_APP)
                .uri(VALID_URI)
                .hits(VALID_HITS)
                .build();

        Set<ConstraintViolation<HitsStatDTO>> violations = validator.validate(hitsStatDTO);

        assertTrue(violations.isEmpty());
    }

    @Test
    void testAppIsBlankThenViolationOccurs() {

        HitsStatDTO hitsStatDTO = HitsStatDTO.builder()
                .app(INVALID_APP_BLANK)
                .uri(VALID_URI)
                .hits(VALID_HITS)
                .build();

        Set<ConstraintViolation<HitsStatDTO>> violations = validator.validate(hitsStatDTO);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("app", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void testAppIsNullThenViolationOccurs() {

        HitsStatDTO hitsStatDTO = HitsStatDTO.builder()
                .app(INVALID_APP_NULL)
                .uri(VALID_URI)
                .hits(VALID_HITS)
                .build();

        Set<ConstraintViolation<HitsStatDTO>> violations = validator.validate(hitsStatDTO);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("app", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void testUriIsBlankThenViolationOccurs() {

        HitsStatDTO hitsStatDTO = HitsStatDTO.builder()
                .app(VALID_APP)
                .uri(INVALID_URI_BLANK)
                .hits(VALID_HITS)
                .build();

        Set<ConstraintViolation<HitsStatDTO>> violations = validator.validate(hitsStatDTO);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("uri", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void testUriIsNullThenViolationOccurs() {

        HitsStatDTO hitsStatDTO = HitsStatDTO.builder()
                .app(VALID_APP)
                .uri(INVALID_URI_NULL)
                .hits(VALID_HITS)
                .build();

        Set<ConstraintViolation<HitsStatDTO>> violations = validator.validate(hitsStatDTO);

        assertFalse(violations.isEmpty());
        assertEquals(2, violations.size());
        assertEquals("uri", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void testUriFormatInvalidThenViolationOccurs() {

        HitsStatDTO hitsStatDTO = HitsStatDTO.builder()
                .app(VALID_APP)
                .uri(INVALID_URI_FORMAT)
                .hits(VALID_HITS)
                .build();

        Set<ConstraintViolation<HitsStatDTO>> violations = validator.validate(hitsStatDTO);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("uri", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void testHitsIsNullThenViolationOccurs() {

        HitsStatDTO hitsStatDTO = HitsStatDTO.builder()
                .app(VALID_APP)
                .uri(VALID_URI)
                .hits(INVALID_HITS_NULL)
                .build();

        Set<ConstraintViolation<HitsStatDTO>> violations = validator.validate(hitsStatDTO);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("hits", violations.iterator().next().getPropertyPath().toString());
    }
}