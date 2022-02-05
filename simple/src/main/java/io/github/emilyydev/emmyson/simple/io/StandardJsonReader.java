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

package io.github.emilyydev.emmyson.simple.io;

import io.github.emilyydev.emmyson.data.DataFactory;
import io.github.emilyydev.emmyson.data.JsonArray;
import io.github.emilyydev.emmyson.data.JsonBoolean;
import io.github.emilyydev.emmyson.data.JsonData;
import io.github.emilyydev.emmyson.data.JsonNull;
import io.github.emilyydev.emmyson.data.JsonNumber;
import io.github.emilyydev.emmyson.data.JsonObject;
import io.github.emilyydev.emmyson.data.JsonString;
import io.github.emilyydev.emmyson.exception.JsonParseException;
import io.github.emilyydev.emmyson.exception.MalformedJsonException;
import io.github.emilyydev.emmyson.io.JsonReader;
import io.github.emilyydev.emmyson.simple.Stuff.Escapable;
import io.github.emilyydev.emmyson.simple.Stuff.Literal;
import io.github.emilyydev.emmyson.simple.Stuff.Token;
import io.github.emilyydev.emmyson.simple.Stuff.Whitespace;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedHashMap;

import static io.github.emilyydev.emmyson.simple.CharStringStuffDoNotTouch.codePointToString;

public final class StandardJsonReader implements JsonReader {

  private static JsonParseException asJsonParseException(final IOException exception) {
    if (exception instanceof JsonParseException) {
      return (JsonParseException) exception;
    } else {
      return new JsonParseException(exception);
    }
  }

  private static JsonParseException reachedEndOfStream() {
    return new JsonParseException("Reached end of stream");
  }

  private static JsonParseException expectedTokenButGot(final int hint, final int read, final String at) {
    return new JsonParseException(
        "Expected token '" + codePointToString(hint) + "' at " + at
        + ", got '" + codePointToString(read) + "' instead"
    );
  }

  private static BufferedReader asBufferedReader(final Readable in) {
    if (in instanceof BufferedReader) {
      return (BufferedReader) in;
    } else if (in instanceof Reader) {
      return new BufferedReader((Reader) in);
    } else {
      return new BufferedReader(new ReadableReader(in));
    }
  }

  private final DataFactory factory;
  private final BufferedReader in;
  private final Deque<Token> expectedTokenToBeFoundForHasNextElementToReturnFalseDeque = new ArrayDeque<>();
  private long line = 0, column = 0, lineMark = 0, columnMark = 0;

  public StandardJsonReader(final Readable in, final DataFactory factory) {
    this.in = asBufferedReader(in);
    this.factory = factory;
  }

  @Override
  public JsonData read() throws JsonParseException {
    try {
      consumeWhitespaces(true);
      final Token nextToken = Token.determineNextToken(this.in);
      final JsonData data;
      if (Token.STRING == nextToken) {
        data = readString();
      } else if (Token.BOOLEAN == nextToken) {
        data = readBoolean();
      } else if (Token.NULL == nextToken) {
        data = readNull();
      } else if (Token.NUMBER == nextToken) {
        data = readNumber();
      } else if (Token.BEGIN_OBJECT == nextToken) {
        data = readObject();
      } else if (Token.BEGIN_ARRAY == nextToken) {
        data = readArray();
      } else {
        throw new MalformedJsonException("Unknown or unexpected token '" + nextToken.name() + "' at " + at());
      }
      consumeWhitespaces(false);
      return data;
    } catch (final IOException exception) {
      throw asJsonParseException(exception);
    } catch (final MalformedJsonException exception) {
      throw exception;
    } catch (final Exception exception) {
      throw new MalformedJsonException(exception);
    }
  }

  @Override
  public JsonNull readNull() throws IOException {
    final String result = readMany(4).toString();
    if (Literal.NULL.equals(result)) { return this.factory.nil(); }
    throw new JsonParseException("Expected 'null' at " + at() + ", got '" + result + '\'');
  }

  @Override
  public JsonBoolean readBoolean() throws IOException {
    final StringBuilder buffer = new StringBuilder(5);
    for (int i = 0; i < 4; ++i) {
      final int read = readNext();
      if (Token.UNKNOWN.hint == read) { throw reachedEndOfStream(); }
      buffer.appendCodePoint(read);
    }

    if (Literal.TRUE.equals(buffer.toString())) { return this.factory.bool(true); }
    final int read = readNext();
    if (Token.UNKNOWN.hint == read) { throw reachedEndOfStream(); }
    buffer.appendCodePoint(read);

    final String result = buffer.toString();
    if (Literal.FALSE.equals(result)) { return this.factory.bool(false); }
    throw new JsonParseException("Expected a boolean value at " + at() + ", got '" + result + "' instead");
  }

  @Override
  public JsonNumber readNumber() throws IOException {
    final StringBuilder buffer = new StringBuilder();
    boolean isDecimal = false;

    while (true) {
      mark(1);
      final int read = readNext();

      if ('0' <= read && '9' >= read) {
        buffer.appendCodePoint(read);
        continue;
      }

      if ('.' == read && !isDecimal) {
        isDecimal = true;
        buffer.appendCodePoint(read);
        continue;
      }

      reset();
      break;
    }

    final String result = buffer.toString();
    return isDecimal ? this.factory.number(Double.parseDouble(result)) : this.factory.number(Long.parseLong(result));
  }

  @Override
  public JsonString readString() throws IOException {
    final StringBuilder buffer = new StringBuilder();
    {
      final int read = readNext();
      if (Token.UNKNOWN.hint == read) {
        throw reachedEndOfStream();
      } else if (Token.STRING.hint != read) {
        throw new JsonParseException("Expected a string value at " + at());
      }
    }

    while (true) {
      final int read = readNext();
      if (Token.UNKNOWN.hint == read) {
        throw reachedEndOfStream();
      } else if (Token.STRING.hint == read) {
        return this.factory.string(buffer);
      } else if (Escapable.BACKSLASH.codePoint == read) {
        final int control = readNext();
        if (Token.UNKNOWN.hint == control) {
          throw reachedEndOfStream();
        } else if ('u' == control) {
          buffer.appendCodePoint(readCodePoint());
        } else if (!Escapable.readMatching(control, buffer)) {
          throw new JsonParseException(
              "Expected control character at " + at() + ", got '" + codePointToString(control) + "' instead"
          );
        }

      } else {
        buffer.appendCodePoint(read);
      }
    }
  }

  private boolean hasNextElement() throws IOException {
    consumeWhitespaces(true);
    final Token nextToken = Token.determineNextToken(this.in);
    if (Token.UNKNOWN == nextToken) {
      throw reachedEndOfStream();
    } else if (this.expectedTokenToBeFoundForHasNextElementToReturnFalseDeque.peek() == nextToken) {
      return false;
    } else if (Token.SEPARATOR == nextToken) {
      readNext();
      consumeWhitespaces(true);
      return true;
    } else {
      // TODO what to do when the next token isn't any of the above
      return true;
    }
  }

  private void beginArray() throws IOException {
    final int read = readNext();
    final int hint = Token.BEGIN_ARRAY.hint;
    if (Token.UNKNOWN.hint == read) {
      throw reachedEndOfStream();
    } else if (hint != read) {
      throw expectedTokenButGot(hint, read, at());
    } else {
      this.expectedTokenToBeFoundForHasNextElementToReturnFalseDeque.addFirst(Token.END_ARRAY);
    }
  }

  private void endArray() throws IOException {
    final int read = readNext();
    final int hint = this.expectedTokenToBeFoundForHasNextElementToReturnFalseDeque.removeFirst().hint;
    if (Token.UNKNOWN.hint == read) {
      throw reachedEndOfStream();
    } else if (hint != read) {
      throw expectedTokenButGot(hint, read, at());
    }
  }

  @Override
  public JsonArray readArray() throws IOException {
    beginArray();
    final ArrayList<JsonData> list = new ArrayList<>();
    while (hasNextElement()) { list.add(read()); }
    endArray();
    return this.factory.arrayOf(list);
  }

  private void beginObject() throws IOException {
    final int read = readNext();
    final int hint = Token.BEGIN_OBJECT.hint;
    if (Token.UNKNOWN.hint == read) {
      throw reachedEndOfStream();
    } else if (hint != read) {
      throw expectedTokenButGot(hint, read, at());
    } else {
      this.expectedTokenToBeFoundForHasNextElementToReturnFalseDeque.addFirst(Token.END_OBJECT);
    }
  }

  private String nextName() throws IOException {
    consumeWhitespaces(true);
    final JsonString name = readString();
    consumeUntil(Token.OBJECT_MAPPER);
    consumeWhitespaces(true);
    return name.toString();
  }

  private void endObject() throws IOException {
    final int read = readNext();
    final int hint = this.expectedTokenToBeFoundForHasNextElementToReturnFalseDeque.removeFirst().hint;
    if (Token.UNKNOWN.hint == read) {
      throw reachedEndOfStream();
    } else if (hint != read) {
      throw expectedTokenButGot(hint, read, at());
    }
  }

  @Override
  public JsonObject readObject() throws IOException {
    beginObject();
    final LinkedHashMap<String, JsonData> map = new LinkedHashMap<>();
    while (hasNextElement()) { map.put(nextName(), read()); }
    endObject();
    return this.factory.objectOf(map);
  }

  private String at() {
    return this.line + ":" + this.column;
  }

  private int readNext() throws IOException {
    final int read = this.in.read();
    if (Token.UNKNOWN.hint != read) {
      if (Whitespace.LINEFEED.codePoint == read) {
        this.line++;
        this.column = 0;
      } else {
        this.column++;
      }
    }
    return read;
  }

  private void mark(final int mark) throws IOException {
    this.in.mark(mark);
    this.lineMark = this.line;
    this.columnMark = this.column;
  }

  private void reset() throws IOException {
    this.in.reset();
    this.line = this.lineMark;
    this.column = this.columnMark;
  }

  private CharBuffer readMany(final int capacity) throws IOException {
    final CharBuffer buffer = CharBuffer.allocate(capacity);
    if (Token.UNKNOWN.hint == this.in.read(buffer.mark())) { throw reachedEndOfStream(); }
    for (final char c : buffer.array()) {
      if (Whitespace.LINEFEED.codePoint == c) {
        this.line++;
        this.column = 0;
      } else {
        this.column++;
      }
    }
    return buffer.reset();
  }

  private int readCodePoint() throws IOException {
    try {
      return Integer.parseUnsignedInt(readMany(4), 0, 4, 16);
    } catch (final NumberFormatException exception) {
      throw new JsonParseException(exception);
    }
  }

  private void consumeWhitespaces(final boolean throwOnEOS) throws IOException {
    while (true) {
      mark(1);
      final int read = readNext();
      if (throwOnEOS && Token.UNKNOWN.hint == read) {
        throw reachedEndOfStream();
      } else if (!Whitespace.isWhitespace(read)) {
        reset();
        break;
      }
    }
  }

  private void consumeUntil(final Token until) throws IOException {
    consumeWhitespaces(true);
    final int read = readNext();
    if (until.hint != read) {
      throw new JsonParseException(
          "Expected separator '" + codePointToString(until.hint) + "' at " + at()
          + ", got '" + codePointToString(read) + "' instead"
      );
    }
  }

  @Override
  public void close() throws IOException {
    try {
      // assert end-of-stream was reached
      mark(1);
      if (Token.UNKNOWN.hint != readNext()) {
        throw new MalformedJsonException("Expected end of stream to be reached");
      }
      reset();
    } finally {
      this.in.close();
    }
  }
}
