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

import io.github.emilyydev.emmyson.data.JsonData;
import io.github.emilyydev.emmyson.io.JsonWriter;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static java.util.Map.entry;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class StandardJsonWriterTest implements AbstractStandardTest {

  private void assertWriteEquals(final String expected, final JsonData jsonData) throws IOException {
    final StringBuilder buffer = new StringBuilder();
    try (final JsonWriter writer = FACTORY.createWriter(buffer)) {
      writer.write(jsonData);
    }
    assertEquals(expected, buffer.toString());
  }

  @Test
  public void write_nullLiteral() throws IOException {
    assertWriteEquals("null", FACTORY.nil());
  }

  @Test
  public void write_boolean_true() throws IOException {
    assertWriteEquals("true", FACTORY.bool(true));
  }

  @Test
  public void write_boolean_false() throws IOException {
    assertWriteEquals("false", FACTORY.bool(false));
  }

  @Test
  public void write_number_integer() throws IOException {
    assertWriteEquals("123456789", FACTORY.number(123_456_789L));
  }

  @Test
  public void write_number_decimal() throws IOException {
    assertWriteEquals("123456.789", FACTORY.number(123_456.789));
  }

  @Test
  public void write_string_empty() throws IOException {
    assertWriteEquals("\"\"", FACTORY.string(""));
  }

  @Test
  public void write_string_notEmpty() throws IOException {
    assertWriteEquals("\"Hello, world!\"", FACTORY.string("Hello, world!"));
  }

  @Test
  public void write_string_escapedCharacters() throws IOException {
    assertWriteEquals("\"Hello, world!\\n\\/\\\\\"", FACTORY.string("Hello, world!\n/\\"));
  }

  @Test
  public void write_string_funnyNonAsciiCharacters() throws IOException {
    assertWriteEquals("\" abc123~\\u00b1\\u03b1\\ud83d\\udc68\\u200d\\ud83e\\uddb2\"", FACTORY.string(" abc123~±α👨‍🦲"));
  }

  @Test
  public void write_array_empty() throws IOException {
    assertWriteEquals("[]", FACTORY.arrayOf());
  }

  @Test
  public void write_array_simple() throws IOException {
    assertWriteEquals(
        "[null,true,false,123,\"Hello, world!\\n\\/\\\\\"]",
        FACTORY.arrayOf(
            FACTORY.nil(),
            FACTORY.bool(true),
            FACTORY.bool(false),
            FACTORY.number(123),
            FACTORY.string("Hello, world!\n/\\")
        )
    );
  }

  @Test
  public void write_array_nested() throws IOException {
    assertWriteEquals(
        "[null,true,false,123,\"Hello, world!\\n\\/\\\\\",[\"another array :0\",123.456,[\"this is getting\",\"\",789.0,\"out of hand\"]]]",
        FACTORY.arrayOf(
            FACTORY.nil(),
            FACTORY.bool(true),
            FACTORY.bool(false),
            FACTORY.number(123),
            FACTORY.string("Hello, world!\n/\\"),
            FACTORY.arrayOf(
                FACTORY.string("another array :0"),
                FACTORY.number(123.456),
                FACTORY.arrayOf(
                    FACTORY.string("this is getting"),
                    FACTORY.string(""),
                    FACTORY.number(789.0),
                    FACTORY.string("out of hand")
                )
            )
        )
    );
  }

  @Test
  public void write_object_empty() throws IOException {
    assertWriteEquals("{}", FACTORY.objectOf());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void write_object_simple() throws IOException {
    assertWriteEquals(
        "{\"first\":null,\"second\":true,\"third\":false,\"number uwu\":123,\"fourth\":\"Hello, world!\\n\\/\\\\\"}",
        FACTORY.objectOf(
            entry("first", FACTORY.nil()),
            entry("second", FACTORY.bool(true)),
            entry("third", FACTORY.bool(false)),
            entry("number uwu", FACTORY.number(123)),
            entry("fourth", FACTORY.string("Hello, world!\n/\\"))
        )
    );
  }

  @Test
  @SuppressWarnings("unchecked")
  public void write_object_nested() throws IOException {
    assertWriteEquals(
        "{\"first\":null,\"second\":true,\"third\":false,\"number uwu\":123,\"fourth\":\"Hello, world!\\n\\/\\\\\",\"nested owo\":{\"first\":null,\"second\":true,\"third\":false,\"number uwu\":123.456,\"fourth\":\"Hello, world!\\n\\/\\\\\",\"nested owo\":{\"first\":null,\"second\":true,\"third\":false,\"number uwu\":789.0,\"fourth\":\"Hello, world!\\n\\/\\\\\"}}}",
        FACTORY.objectOf(
            entry("first", FACTORY.nil()),
            entry("second", FACTORY.bool(true)),
            entry("third", FACTORY.bool(false)),
            entry("number uwu", FACTORY.number(123)),
            entry("fourth", FACTORY.string("Hello, world!\n/\\")),
            entry("nested owo", FACTORY.objectOf(
                entry("first", FACTORY.nil()),
                entry("second", FACTORY.bool(true)),
                entry("third", FACTORY.bool(false)),
                entry("number uwu", FACTORY.number(123.456)),
                entry("fourth", FACTORY.string("Hello, world!\n/\\")),
                entry("nested owo", FACTORY.objectOf(
                    entry("first", FACTORY.nil()),
                    entry("second", FACTORY.bool(true)),
                    entry("third", FACTORY.bool(false)),
                    entry("number uwu", FACTORY.number(789.0)),
                    entry("fourth", FACTORY.string("Hello, world!\n/\\"))
                ))
            ))
        )
    );
  }
}
