package hi.thejavatest;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.*;

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
    @DisplayName("Enabled 애노테이션 테스트")
    @EnabledOnOs(OS.MAC)
    @EnabledOnJre({JRE.JAVA_8, JRE.JAVA_11})
    void enabled_annotation() {
        Study study = new Study(100);
        assertThat(study.getLimit()).isGreaterThan(0);
    }

    @Test
    @DisplayName("Enabled If ~ 애노테이션 테스트")
    @EnabledIfEnvironmentVariable(named = "TEST_ENV", matches = "LOCAL")
    void enabled_if_annotation() {
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
    void create_new_study() {
        System.out.println("create2");
    }

    @Test
    @Disabled
    void create_disabled() {
        System.out.println("create1");
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

}