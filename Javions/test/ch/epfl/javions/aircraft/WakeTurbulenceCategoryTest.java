package ch.epfl.javions.aircraft;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WakeTurbulenceCategoryTest {

    @Test
    void wakeTurbulenceCategoryWorksWithValidString(){
        WakeTurbulenceCategory a = WakeTurbulenceCategory.of("L");
        assertEquals(WakeTurbulenceCategory.LIGHT, a);
        WakeTurbulenceCategory b = WakeTurbulenceCategory.of("");
        assertEquals(WakeTurbulenceCategory.UNKNOWN, b);
    }
}