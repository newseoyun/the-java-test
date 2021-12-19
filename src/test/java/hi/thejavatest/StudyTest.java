package hi.thejavatest;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.*;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.AggregateWith;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.aggregator.ArgumentsAggregationException;
import org.junit.jupiter.params.aggregator.ArgumentsAggregator;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.converter.SimpleArgumentConverter;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Duration;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.junit.jupiter.api.Assumptions.assumingThat;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class) // 언더스코어를 공백으로 바꿔서 보여준다
class StudyTest {

    @Test
    @DisplayName("스터디 만들기") // @DisplayNameGeneration 보다 우선순위가 높다
    void create() {
        Study study = new Study(10);
        assertNotNull(study);
        System.out.println("create");

        /*
        assertEquals(StudyStatus.DRAFT, study.getStatus(), "스터디를 처음 만들면 " + StudyStatus.DRAFT + " 상태다.");
        assertEquals(StudyStatus.DRAFT, study.getStatus(), new Supplier<String>() {
            @Override
            public String get() {
                return "스터디를 처음 만들면 DRAFT 상태다.";
            }
        });
        */

        /*

        // 메시지 부분을 이렇게 람다식으로 만들면 테스트가 실패했을 때 연산을 한다. 성능상 이점이 있다.
        assertEquals(StudyStatus.DRAFT, study.getStatus(), () -> "스터디를 처음 만들면 " + StudyStatus.DRAFT + " 상태다.");
        assertTrue(study.getLimit() > 0, "스터디 최대 참석 가능 인원은 0보다 커야 한다.");

         */

        // 위의 경우 앞선 assertEquals 테스트가 깨지면 다음 assertTrue 실행하지 않는다.
        // 모두 확인하고 싶은 경우 assertAll 로 묶어준다.

        assertAll(
                () -> assertNotNull(study),
                () -> assertEquals(StudyStatus.DRAFT, study.getStatus(),
                        () -> "스터디를 처음 만들면 " + StudyStatus.DRAFT + " 상태다."),
                () -> assertTrue(study.getLimit() > 0, "스터디 최대 참석 가능 인원은 0보다 커야 한다."),

                // assertThat 은 assertj 라이브러리가 제공하는 메소드다.
                () -> assertThat(study.getLimit()).isGreaterThan(0)
        );
    }

    @BeforeAll // static 으로만. return 타입 없어야.
    static void beforeAll() {
        System.out.println("before All");
    }

    @AfterAll // static 으로만. return 타입 없어야.
    static void afterAll() {
        System.out.println("after All");
    }

    @BeforeEach
    void beforeEach() {
        System.out.println("Before Each");
    }

    @Test
    @Disabled
    void disabled_test() {
        System.out.println("disabled_test");
    }

    @Test
    @DisplayName("assertThrows 테스트")
    void throws_test() {
//        assertThrows(IllegalArgumentException.class, () -> new Study(-10));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new Study(-10));
        assertEquals("limit 은 0보다 커야한다.", exception.getMessage());
    }

    @Test
    @DisplayName("assertTimeout 테스트. 람다 내부 작업이 해당 시간 안에 수행되어야 한다")
    void timeout_test() {
//        assertTimeout(Duration.ofSeconds(10), () -> new Study(10));

        assertAll(
                // 이 경우 람다 내부 작업(300ms)이 끝나야 테스트도 끝난다.
                () -> assertTimeout(Duration.ofMillis(100), () -> {
                    new Study(10);
                    Thread.sleep(300);
                }),

                // 제한 시간만큼만 수행했으면 할 때.
                // 테스트와 람다식이 별도의 Thread 를 사용하므로, ThreadLocal 사용하는 작업엔 사용하지 않는게 좋다.
                () -> assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
                    new Study(10);
                    Thread.sleep(300);
                })
        );

    }

    @Test
    @DisplayName("assume 테스트")
    void assume_test() {
        String test_env = System.getenv("TEST_ENV");
//        assumeTrue("LOCAL".equalsIgnoreCase(test_env));

        assumingThat("LOCAL".equalsIgnoreCase(test_env), () -> {
            System.out.println("test_env = " + test_env);
            Study study = new Study(100);
            assertThat(study.getLimit()).isGreaterThan(0);
        });

        assumingThat(test_env == null, () -> {
            System.out.println("test_env = " + test_env);
            Study study = new Study(100);
            assertThat(study.getLimit()).isGreaterThan(0);
        });
    }

    @Test
    @DisplayName("Enabled If ~ 애노테이션 테스트")
    @EnabledIfEnvironmentVariable(named = "TEST_ENV", matches = "LOCAL")
    void enabled_if_annotation() {
        Study study = new Study(100);
        assertThat(study.getLimit()).isGreaterThan(0);
    }

    @Test
    @DisplayName("Enabled 애노테이션 테스트")
    @EnabledOnOs(OS.MAC)
    @EnabledOnJre({JRE.JAVA_8, JRE.JAVA_11})
    void enabled_annotation() {
        Study study = new Study(100);
        assertThat(study.getLimit()).isGreaterThan(0);
    }

    @Test
    @DisplayName("Disabled 애노테이션 테스트")
    @DisabledOnOs(OS.MAC)
    @EnabledOnJre({JRE.OTHER})
    void disabled_annotation() {
        Study study = new Study(100);
        assertThat(study.getLimit()).isGreaterThan(0);
    }

    @Test
    @DisplayName("특정 태그를 붙인 테스트만 테스트하거나 제외해서 테스트할 수 있다.")
    @Tag("slow") //  IDE의 Edit configurations에서 설정하거나 pom.xml or build.gradle 에서도 설정 가능.
    void tagging() {
        System.out.println("tagging");
    }

    @Test
    @DisplayName("커스텀 컴포즈 애노테이션")
    @FastTest // 커스텀 애노테이션. @Test와 @Tag를 조합한, 컴포즈 애노테이션이다.
    void custom_annotation() {
        System.out.println("custom_annotation");
    }

    @DisplayName("테스트 반복")
    @RepeatedTest(10) // (value = 10, name = "{displayName}, {currentRepetition}/{totalRepetition}")
    void repeat_test(RepetitionInfo repetitionInfo) {
        System.out.println("TEST " + repetitionInfo.getCurrentRepetition() + "/" +
                repetitionInfo.getTotalRepetitions());
    }

    @ParameterizedTest // (name = "{index} {displayName} message={0}")
    @DisplayName("파라미터를 줄 수 있는 테스트")
    @ValueSource(strings = {"하이", "반갑습니다", "안녕하세요"})
    @EmptySource
    @NullSource
    void ParameterizedTest(String str) {
        System.out.println("str: " + str);
    }

    @ParameterizedTest // (name = "{index} {displayName} message={0}")
    @DisplayName("객체에 파라미터 넘기기")
    @ValueSource(ints = {10, 20, 40})
    void ParameterizedTest2(@ConvertWith(StudyConverter.class) Study study) {
        System.out.println(study.getLimit());
    }

    static class StudyConverter extends SimpleArgumentConverter {
        @Override
        protected Object convert(Object source, Class<?> targetType) throws ArgumentConversionException {
            assertEquals(Study.class, targetType, "Study 타입만 Convert 가능.");
            return new Study(Integer.parseInt(source.toString()));
        }
    }

    @ParameterizedTest // (name = "{index} {displayName} message={0}")
    @DisplayName("객체에 CSV 형태 파라미터 넘기기")
    @CsvSource({"10, '자바 스터디'", "20, '스프링 스터디'"})
    void ParameterizedTest3(Integer limit, String name) {
        Study study = new Study(limit, name);
        System.out.println(study.toString());
    }

    @ParameterizedTest // (name = "{index} {displayName} message={0}")
    @DisplayName("객체에 CSV 형태 파라미터 넘기기 ArgumentsAccessor")
    @CsvSource({"10, '자바 스터디'", "20, '스프링 스터디'"})
    void ParameterizedTest4(ArgumentsAccessor argumentsAccessor) {
        Study study = new Study(argumentsAccessor.getInteger(0), argumentsAccessor.getString(1));
        System.out.println(study);
    }

    @ParameterizedTest // (name = "{index} {displayName} message={0}")
    @DisplayName("객체에 CSV 형태 파라미터 넘기기 Custom ArgumentsAggregator")
    @CsvSource({"10, '자바 스터디'", "20, '스프링 스터디'"})
    void ParameterizedTest5(@AggregateWith(StudyAggregator.class) Study study) {
        System.out.println(study);
    }

    static class StudyAggregator implements ArgumentsAggregator {
        @Override
        public Object aggregateArguments(ArgumentsAccessor accessor, ParameterContext context) throws ArgumentsAggregationException {
            return new Study(accessor.getInteger(0), accessor.getString(1));
        }
    }

}