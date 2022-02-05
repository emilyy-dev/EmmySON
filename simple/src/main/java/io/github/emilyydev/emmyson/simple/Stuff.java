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

package io.github.emilyydev.emmyson.simple;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.CharBuffer;

public interface Stuff {

  enum Whitespace {
    SPACE(' '),
    TAB('\t'),
    LINEFEED('\n'),
    CARRIAGE_RETURN('\r');

    private static final Whitespace[] WHITESPACES = values();

    public static boolean isWhitespace(final int codePoint) {
      for (final Whitespace whitespace : WHITESPACES) {
        if (whitespace.codePoint == codePoint) {
          return true;
        }
      }
      return false;
    }

    public final int codePoint;

    Whitespace(final int codePoint) {
      this.codePoint = codePoint;
    }
  }

  enum Token {
    UNKNOWN(-1),
    SEPARATOR(Tokens.SEPARATOR),
    OBJECT_MAPPER(Tokens.OBJECT_MAPPER),
    BEGIN_OBJECT(Tokens.BEGIN_OBJECT),
    END_OBJECT(Tokens.END_OBJECT),
    BEGIN_ARRAY(Tokens.BEGIN_ARRAY),
    END_ARRAY(Tokens.END_ARRAY),
    STRING(Tokens.QUOTE),
    NUMBER {
      @Override
      boolean isPossiblyValidInput(final Reader in) throws IOException {
        in.mark(1);
        try {
          final int read = in.read();
          return '-' == read || ('0' <= read && '9' >= read);
        } finally {
          in.reset();
        }
      }
    }, NULL {
      @Override
      boolean isPossiblyValidInput(final Reader in) throws IOException {
        in.mark(4);
        try {
          final CharBuffer buffer = CharBuffer.allocate(4);
          in.read(buffer.mark());
          return Literal.NULL.equals(buffer.reset().toString());
        } finally {
          in.reset();
        }
      }
    }, BOOLEAN {
      @Override
      boolean isPossiblyValidInput(final Reader in) throws IOException {
        in.mark(5);
        try {
          final StringBuilder buffer = new StringBuilder(5);
          for (int i = 0; i < 4; ++i) { buffer.appendCodePoint(in.read()); }
          if (Literal.TRUE.equals(buffer.toString())) { return true; }
          buffer.appendCodePoint(in.read());
          return Literal.FALSE.equals(buffer.toString());
        } finally {
          in.reset();
        }
      }
    };

    private static final Token[] TOKENS = values();

    public static Token determineNextToken(final Reader in) throws IOException {
      for (final Token token : TOKENS) {
        if (token.isPossiblyValidInput(in)) {
          return token;
        }
      }

      return UNKNOWN;
    }

    public final int hint;

    Token() {
      this(Integer.MIN_VALUE);
    }

    Token(final int hint) {
      this.hint = hint;
    }

    boolean isPossiblyValidInput(final Reader in) throws IOException {
      in.mark(1);
      try {
        return this.hint == in.read();
      } finally {
        in.reset();
      }
    }
  }

  enum Escapable {
    QUOTE('"'),
    BACKSLASH('\\'),
    FORWARD_SLASH('/'),
    BACKSPACE('\b', 'b'),
    FORM_FEED('\f', 'f'),
    LINEFEED('\n', 'n'),
    CARRIAGE_RETURN('\r', 'r'),
    TAB('\t', 't');

    private static final Escapable[] ESCAPABLES = values(); // adjectives don't have a plural counterpart but...

    public static void writeMatchingOrWrite(final int codePoint, final Writer out) throws IOException {
      for (final Escapable escapable : ESCAPABLES) {
        if (escapable.codePoint == codePoint) {
          out.write(BACKSLASH.codePoint);
          out.write(escapable.control);
          return;
        }
      }
      out.write(codePoint);
    }

    public static boolean readMatching(final int control, final StringBuilder buffer) {
      for (final Escapable escapable : ESCAPABLES) {
        if (escapable.control == control) {
          buffer.appendCodePoint(escapable.codePoint);
          return true;
        }
      }
      return false;
    }

    public final int codePoint, control;

    Escapable(final int codePoint) {
      this(codePoint, codePoint);
    }

    Escapable(final int codePoint, final int control) {
      this.codePoint = codePoint;
      this.control = control;
    }
  }

  interface Literal {

    String NULL = "null";
    String TRUE = "true";
    String FALSE = "false";
  }

  // TODO: does this *really* need to be a separate thing from Token? They don't precisely represent "tokens" but...
  interface Tokens {

    int BEGIN_ARRAY = '[';
    int END_ARRAY = ']';
    int BEGIN_OBJECT = '{';
    int OBJECT_MAPPER = ':';
    int END_OBJECT = '}';
    int SEPARATOR = ',';
    int QUOTE = '"';
  }
}
