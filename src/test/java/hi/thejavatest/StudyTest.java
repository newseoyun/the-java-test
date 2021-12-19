package hi.thejavatest;

import org.junit.jupiter.api.*;

import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class) // 언더스코어를 공백으로 바꿔준다
class StudyTest {

    @Test
    @DisplayName("스터디 만들기") // @DisplayNameGeneration 보다 우선순위가 높다
    void create_new_study() {
        Study study = new Study(-10);
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

        // 메시지 부분을 이렇게 람다식으로 만들면 테스트가 실패했을 때 연산을 한다. 성능상 이점.
        assertEquals(StudyStatus.DRAFT, study.getStatus(), () -> "스터디를 처음 만들면 " + StudyStatus.DRAFT + " 상태다.");
        assertTrue(study.getLimit() > 0, "스터디 최대 참석 가능 인원은 0보다 커야 한다.");

         */

        // 위의 경우 앞선 assertEquals 테스트가 깨지면 다음 assertTrue 실행하지 않는다.
        // 모두 확인하고 싶은 경우 assertAll 로 묶어준다.

        assertAll(
                () -> assertNotNull(study),
                () -> assertEquals(StudyStatus.DRAFT, study.getStatus(),
                        () -> "스터디를 처음 만들면 " + StudyStatus.DRAFT + " 상태다."),
                () -> assertTrue(study.getLimit() > 0, "스터디 최대 참석 가능 인원은 0보다 커야 한다.")
        );


    }

    @Test
    void create1() {
        System.out.println("create1");
    }

    @Test
    void create_new_study_2() {
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