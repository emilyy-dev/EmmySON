//
// Simple implementation for the EmmySON API
// Copyright (C) 2022  emilyy-dev
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU Lesser General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public License
// along with this program.  If not, see <https://www.gnu.org/licenses/>.
//

package io.github.emilyydev.emmyson.test;

import io.github.emilyydev.emmyson.data.DataFactory;
import io.github.emilyydev.emmyson.data.JsonData;
import io.github.emilyydev.emmyson.exception.JsonParseException;
import io.github.emilyydev.emmyson.exception.MalformedJsonException;
import io.github.emilyydev.emmyson.io.JsonReader;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

import static java.util.Map.entry;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class StandardJsonReaderTest {

  private static DataFactory dataFactory;

  @BeforeAll
  public static void prepare() {
    dataFactory = DataFactory.findDataFactory().orElseThrow();
  }

  private static InputStream resource(final String name) {
    return StandardJsonReaderTest.class.getResourceAsStream(name);
  }

  private static void assertReadEquals(final JsonData expected, final String in) throws IOException {
    try (final JsonReader reader = dataFactory.createReader(in)) {
      assertEquals(expected, reader.read());
    }
  }

  private static void assertReadResourceEquals(final JsonData expected, final String name) throws IOException {
    try (
        final InputStream stream = resource(name);
        final JsonReader reader = dataFactory.createReader(stream)
    ) {
      assertEquals(expected, reader.read());
    }
  }

  @Test
  public void read_nullLiteral() throws IOException {
    assertReadEquals(dataFactory.nil(), "null");
  }

  @Test
  public void read_nullLiteral_leadingWhitespaces() throws IOException {
    assertReadEquals(dataFactory.nil(), " \t\n\rnull");
  }

  @Test
  public void read_nullLiteral_trailingWhitespaces() throws IOException {
    assertReadEquals(dataFactory.nil(), "null \t\n\r");
  }

  @Test
  public void read_invalidLiteral() {
    assertThrows(MalformedJsonException.class, () -> {
      try (final JsonReader reader = dataFactory.createReader("invalid")) {
        reader.read();
      }
    });
  }

  @Test
  public void read_boolean_true() throws IOException {
    assertReadEquals(dataFactory.bool(true), "true");
  }

  @Test
  public void read_boolean_false() throws IOException {
    assertReadEquals(dataFactory.bool(false), "false");
  }

  @Test
  public void read_number_integer() throws IOException {
    assertReadEquals(dataFactory.number(123_456_789L), "123456789");
  }

  @Test
  public void read_number_decimal() throws IOException {
    assertReadEquals(dataFactory.number(123_456.789), "123456.789");
  }

  @Test
  public void read_number_integerMalformed() {
    assertThrows(MalformedJsonException.class, () -> {
      try (final JsonReader reader = dataFactory.createReader("123456789ad")) {
        reader.read();
      }
    });
  }

  @Test
  public void read_number_decimalMalformed() {
    assertThrows(MalformedJsonException.class, () -> {
      try (final JsonReader reader = dataFactory.createReader("123456.789,")) {
        reader.read();
      }
    });
  }

  @Test
  public void read_string_empty() throws IOException {
    assertReadEquals(dataFactory.string(""), "\"\"");
  }

  @Test
  public void read_string_notEmpty() throws IOException {
    assertReadEquals(dataFactory.string("Hello, world!"), "\"Hello, world!\"");
  }

  @Test
  public void read_string_escapedCharacters() throws IOException {
    assertReadEquals(dataFactory.string("Hello, world!\n/\\"), "\"Hello, world!\\n\\/\\\\\"");
  }

  @Test
  public void read_string_funnyNonAsciiCharacters() throws IOException {
    assertReadEquals(dataFactory.string(" abc123~±α👨‍🦲"), "\" abc123~\\u00b1\\u03b1\\ud83d\\udc68\\u200d\\ud83e\\uddb2\"");
  }

  @Test
  public void read_invalidString() {
    assertThrows(JsonParseException.class, () -> {
      try (final JsonReader reader = dataFactory.createReader("\"invalid")) {
        reader.read();
      }
    });
  }

  @Test
  public void read_array_empty() throws IOException {
    assertReadEquals(dataFactory.arrayOf(), "[]");
  }

  @Test
  public void read_array_emptyWithWhitespaces() throws IOException {
    assertReadEquals(dataFactory.arrayOf(), "[ \t\n\r]");
  }

  @Test
  public void read_array_simple() throws IOException {
    assertReadResourceEquals(
        dataFactory.arrayOf(
            dataFactory.nil(),
            dataFactory.bool(true),
            dataFactory.bool(false),
            dataFactory.number(123),
            dataFactory.string("Hello, world!\n/\\")
        ),
        "simple-array.json"
    );
  }

  @Test
  public void read_array_nested() throws IOException {
    assertReadResourceEquals(
        dataFactory.arrayOf(
            dataFactory.nil(),
            dataFactory.bool(true),
            dataFactory.bool(false),
            dataFactory.number(123),
            dataFactory.string("Hello, world!\n/\\"),
            dataFactory.arrayOf(
                dataFactory.string("another array :0"),
                dataFactory.number(123.456),
                dataFactory.arrayOf(
                    dataFactory.string("this is getting"),
                    dataFactory.string(""),
                    dataFactory.number(789.0),
                    dataFactory.string("out of hand")
                )
            )
        ),
        "nested-array.json"
    );
  }

  @Test
  public void read_array_malformed() {
    assertThrows(JsonParseException.class, () -> {
      try (final JsonReader reader = dataFactory.createReader("[ null ")) {
        reader.read();
      }
    });
  }

  @Test
  public void read_object_empty() throws IOException {
    assertReadEquals(dataFactory.objectOf(), "{}");
  }

  @Test
  public void read_object_emptyWithWhitespaces() throws IOException {
    assertReadEquals(dataFactory.objectOf(), "{ \t\n\r}");
  }

  @Test
  @SuppressWarnings("unchecked")
  public void read_object_simple() throws IOException {
    assertReadResourceEquals(
        dataFactory.objectOf(
            entry("first", dataFactory.nil()),
            entry("second", dataFactory.bool(true)),
            entry("third", dataFactory.bool(false)),
            entry("number uwu", dataFactory.number(123)),
            entry("fourth", dataFactory.string("Hello, world!\n/\\"))
        ),
        "simple-object.json"
    );
  }

  @Test
  @SuppressWarnings("unchecked")
  public void read_object_nested() throws IOException {
    assertReadResourceEquals(
        dataFactory.objectOf(
            entry("first", dataFactory.nil()),
            entry("second", dataFactory.bool(true)),
            entry("third", dataFactory.bool(false)),
            entry("number uwu", dataFactory.number(123)),
            entry("fourth", dataFactory.string("Hello, world!\n/\\")),
            entry("nested owo", dataFactory.objectOf(
                entry("first", dataFactory.nil()),
                entry("second", dataFactory.bool(true)),
                entry("third", dataFactory.bool(false)),
                entry("number uwu", dataFactory.number(123.456)),
                entry("fourth", dataFactory.string("Hello, world!\n/\\")),
                entry("nested owo", dataFactory.objectOf(
                    entry("first", dataFactory.nil()),
                    entry("second", dataFactory.bool(true)),
                    entry("third", dataFactory.bool(false)),
                    entry("number uwu", dataFactory.number(789.0)),
                    entry("fourth", dataFactory.string("Hello, world!\n/\\"))
                ))
            ))
        ),
        "nested-object.json"
    );
  }

  @Test
  public void read_object_malformed() {
    assertThrows(JsonParseException.class, () -> {
      try (final JsonReader reader = dataFactory.createReader("{ \"first\": null ")) {
        reader.read();
      }
    });
  }

  @Test
  public void mock() {
    assertDoesNotThrow(() -> {
      try (
          final InputStream stream = resource("mock.json");
          final JsonReader reader = dataFactory.createReader(stream)
      ) {
        reader.readArray();
      }
    });
  }
}
