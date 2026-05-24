package com.example.urlshortener.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class Base62Test {

    @Test
    @DisplayName("generateCode() returns 7 character string by default")
    void generateCode_defaultLength_returnsSevenChars() {
        String code = Base62.generateCode();
        assertEquals(7, code.length());
    }

    @Test
    @DisplayName("generateCode(10) returns 10 character string")
    void generateCode_customLength_returnsCorrectLength() {
        String code = Base62.generateCode(10);
        assertEquals(10, code.length());
    }

    @Test
    @DisplayName("Generated codes contain only alphanumeric characters")
    void generateCode_containsOnlyValidCharacters() {
        String code = Base62.generateCode();
        assertTrue(code.matches("^[0-9A-Za-z]+$"));
    }

    @Test
    @DisplayName("Generated codes are unique across 1000 generations")
    void generateCode_producesUniqueCodes() {
        Set<String> codes = new HashSet<>();
        for (int i = 0; i < 1000; i++) {
            codes.add(Base62.generateCode());
        }
        assertEquals(1000, codes.size());
    }

    @Test
    @DisplayName("generateCode(0) throws IllegalArgumentException")
    void generateCode_zeroLength_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> Base62.generateCode(0));
    }

    @Test
    @DisplayName("generateCode(-1) throws IllegalArgumentException")
    void generateCode_negativeLength_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> Base62.generateCode(-1));
    }
}
