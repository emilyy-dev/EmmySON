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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.UncheckedIOException;

import static java.util.Map.entry;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class StandardJsonWriterTest {

  private static DataFactory dataFactory;

  @BeforeAll
  public static void prepare() {
    dataFactory = DataFactory.findDataFactory().orElseThrow();
  }

  private static void assertWriteEquals(final String expected, final JsonData jsonData) {
    final StringBuilder buffer = new StringBuilder();
    dataFactory.write(buffer, jsonData)
        .map(UncheckedIOException::new)
        .ifPresent(exception -> { throw exception; });
    assertEquals(expected, buffer.toString());
  }

  @Test
  public void write_nullLiteral() {
    assertWriteEquals("null", dataFactory.nil());
  }

  @Test
  public void write_boolean_true() {
    assertWriteEquals("true", dataFactory.bool(true));
  }

  @Test
  public void write_boolean_false() {
    assertWriteEquals("false", dataFactory.bool(false));
  }

  @Test
  public void write_number_integer() {
    assertWriteEquals("123456789", dataFactory.number(123_456_789L));
  }

  @Test
  public void write_number_decimal() {
    assertWriteEquals("123456.789", dataFactory.number(123_456.789));
  }

  @Test
  public void write_string_empty() {
    assertWriteEquals("\"\"", dataFactory.string(""));
  }

  @Test
  public void write_string_notEmpty() {
    assertWriteEquals("\"Hello, world!\"", dataFactory.string("Hello, world!"));
  }

  @Test
  public void write_string_escapedCharacters() {
    assertWriteEquals("\"Hello, world!\\n\\/\\\\\"", dataFactory.string("Hello, world!\n/\\"));
  }

  @Test
  public void write_string_funnyNonAsciiCharacters() {
    assertWriteEquals("\" abc123~\\u00b1\\u03b1\\ud83d\\udc68\\u200d\\ud83e\\uddb2\"", dataFactory.string(" abc123~±α👨‍🦲"));
  }

  @Test
  public void write_array_empty() {
    assertWriteEquals("[]", dataFactory.arrayOf());
  }

  @Test
  public void write_array_simple() {
    assertWriteEquals(
        "[null,true,false,123,\"Hello, world!\\n\\/\\\\\"]",
        dataFactory.arrayOf(
            dataFactory.nil(),
            dataFactory.bool(true),
            dataFactory.bool(false),
            dataFactory.number(123),
            dataFactory.string("Hello, world!\n/\\")
        )
    );
  }

  @Test
  public void write_array_nested() {
    assertWriteEquals(
        "[null,true,false,123,\"Hello, world!\\n\\/\\\\\",[\"another array :0\",123.456,[\"this is getting\",\"\",789.0,\"out of hand\"]]]",
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
        )
    );
  }

  @Test
  public void write_object_empty() {
    assertWriteEquals("{}", dataFactory.objectOf());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void write_object_simple() {
    assertWriteEquals(
        "{\"first\":null,\"second\":true,\"third\":false,\"number uwu\":123,\"fourth\":\"Hello, world!\\n\\/\\\\\"}",
        dataFactory.objectOf(
            entry("first", dataFactory.nil()),
            entry("second", dataFactory.bool(true)),
            entry("third", dataFactory.bool(false)),
            entry("number uwu", dataFactory.number(123)),
            entry("fourth", dataFactory.string("Hello, world!\n/\\"))
        )
    );
  }

  @Test
  @SuppressWarnings("unchecked")
  public void write_object_nested() {
    assertWriteEquals(
        "{\"first\":null,\"second\":true,\"third\":false,\"number uwu\":123,\"fourth\":\"Hello, world!\\n\\/\\\\\",\"nested owo\":{\"first\":null,\"second\":true,\"third\":false,\"number uwu\":123.456,\"fourth\":\"Hello, world!\\n\\/\\\\\",\"nested owo\":{\"first\":null,\"second\":true,\"third\":false,\"number uwu\":789.0,\"fourth\":\"Hello, world!\\n\\/\\\\\"}}}",
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
        )
    );
  }
}
