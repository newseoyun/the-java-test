package hi.thejavatest;

import org.aspectj.lang.annotation.After;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class StudyTest {

    @Test
    void create() {
        Study study = new Study();
        assertNotNull(study);
        System.out.println("create");
    }

    @Test
    @Disabled
    void create1() {
        Study study = new Study();
        assertNotNull(study);
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