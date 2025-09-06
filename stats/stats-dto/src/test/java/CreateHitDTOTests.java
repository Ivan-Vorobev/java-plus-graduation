import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.dto.CreateHitDTO;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CreateHitDTOTests {

    private static final String VALID_APP = "ewm-main-service";
    private static final String VALID_URI = "/events/1";
    private static final String VALID_URI_WITH_QUERY_PARAMS = "/events/1?param=true";
    private static final String VALID_IP = "192.168.1.1";
    private static final LocalDateTime VALID_TIMESTAMP = LocalDateTime.now();

    private static final String INVALID_APP_BLANK = "";
    private static final String INVALID_APP_NULL = null;
    private static final String INVALID_URI_BLANK = "";
    private static final String INVALID_URI_NULL = null;
    private static final String INVALID_URI_FORMAT = "events/1";
    private static final String INVALID_URI_WITH_EMPTY_QUERY_PARAMS = "/events/1?param=";
    private static final String INVALID_URI_WITH_EMPTY_PARAMS = "/events/1?";
    private static final String INVALID_IP = "256.168.1.1";
    private static final LocalDateTime INVALID_TIMESTAMP_NULL = null;

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    public CreateHitDTOTests() {
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

        CreateHitDTO request = CreateHitDTO.builder()
                .app(VALID_APP)
                .uri(VALID_URI)
                .ip(VALID_IP)
                .timestamp(VALID_TIMESTAMP)
                .build();

        CreateHitDTO request2 = CreateHitDTO.builder()
                .app(VALID_APP)
                .uri(VALID_URI_WITH_QUERY_PARAMS)
                .ip(VALID_IP)
                .timestamp(VALID_TIMESTAMP)
                .build();

        Set<ConstraintViolation<CreateHitDTO>> violations = validator.validate(request);
        Set<ConstraintViolation<CreateHitDTO>> violations2 = validator.validate(request2);

        assertTrue(violations.isEmpty());
        assertTrue(violations2.isEmpty());
    }

    @Test
    void testAppIsBlankThenViolationOccurs() {

        CreateHitDTO request = CreateHitDTO.builder()
                .app(INVALID_APP_BLANK)
                .uri(VALID_URI)
                .ip(VALID_IP)
                .timestamp(VALID_TIMESTAMP)
                .build();

        Set<ConstraintViolation<CreateHitDTO>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("app", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void testAppIsNullThenViolationOccurs() {

        CreateHitDTO request = CreateHitDTO.builder()
                .app(INVALID_APP_NULL)
                .uri(VALID_URI)
                .ip(VALID_IP)
                .timestamp(VALID_TIMESTAMP)
                .build();

        Set<ConstraintViolation<CreateHitDTO>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("app", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void testUriIsBlankThenViolationOccurs() {

        CreateHitDTO request = CreateHitDTO.builder()
                .app(VALID_APP)
                .uri(INVALID_URI_BLANK)
                .ip(VALID_IP)
                .timestamp(VALID_TIMESTAMP)
                .build();

        Set<ConstraintViolation<CreateHitDTO>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("uri", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void testInvalidUriWithEmptyQueryParamsThenViolationOccurs() {

        CreateHitDTO request = CreateHitDTO.builder()
                .app(VALID_APP)
                .uri(INVALID_URI_WITH_EMPTY_QUERY_PARAMS)
                .ip(VALID_IP)
                .timestamp(VALID_TIMESTAMP)
                .build();

        Set<ConstraintViolation<CreateHitDTO>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("uri", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void testInvalidUriWithEmptyParamsThenViolationOccurs() {

        CreateHitDTO request = CreateHitDTO.builder()
                .app(VALID_APP)
                .uri(INVALID_URI_WITH_EMPTY_PARAMS)
                .ip(VALID_IP)
                .timestamp(VALID_TIMESTAMP)
                .build();

        Set<ConstraintViolation<CreateHitDTO>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("uri", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void testUriIsNullThenViolationOccurs() {

        CreateHitDTO request = CreateHitDTO.builder()
                .app(VALID_APP)
                .uri(INVALID_URI_NULL)
                .ip(VALID_IP)
                .timestamp(VALID_TIMESTAMP)
                .build();

        Set<ConstraintViolation<CreateHitDTO>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertEquals(2, violations.size());
        assertEquals("uri", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void testUriFormatInvalidThenViolationOccurs() {

        CreateHitDTO request = CreateHitDTO.builder()
                .app(VALID_APP)
                .uri(INVALID_URI_FORMAT)
                .ip(VALID_IP)
                .timestamp(VALID_TIMESTAMP)
                .build();

        Set<ConstraintViolation<CreateHitDTO>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("uri", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void testIpFormatInvalidThenViolationOccurs() {

        CreateHitDTO request = CreateHitDTO.builder()
                .app(VALID_APP)
                .uri(VALID_URI)
                .ip(INVALID_IP)
                .timestamp(VALID_TIMESTAMP)
                .build();

        Set<ConstraintViolation<CreateHitDTO>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("ip", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void testTimestampIsNullThenViolationOccurs() {

        CreateHitDTO request = CreateHitDTO.builder()
                .app(VALID_APP)
                .uri(VALID_URI)
                .ip(VALID_IP)
                .timestamp(INVALID_TIMESTAMP_NULL)
                .build();

        Set<ConstraintViolation<CreateHitDTO>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("timestamp", violations.iterator().next().getPropertyPath().toString());
    }
}