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

package io.github.emilyydev.emmyson.simple.data;

import io.github.emilyydev.emmyson.data.JsonString;
import net.kyori.examination.string.StringExaminer;
import org.jetbrains.annotations.NotNull;

public class JsonStringImpl implements JsonString {

  private static final long serialVersionUID = -8888634840703976187L;

  public static JsonString emptyOrCreate(final CharSequence str) {
    final String string = str.toString();
    return string.isEmpty() ? Empty.INSTANCE : new JsonStringImpl(string);
  }

  private final String string;

  private JsonStringImpl(final String string) {
    this.string = string;
  }

  @Override
  public final int length() {
    return this.string.length();
  }

  @Override
  public final char charAt(final int index) {
    return this.string.charAt(index);
  }

  @Override
  public final @NotNull JsonString substring(final int start, final int end) {
    return emptyOrCreate(this.string.substring(start, end));
  }

  @Override
  public final String asString() {
    return this.string;
  }

  @Override
  public final boolean equals(final Object other) {
    if (this == other) { return true; }
    if (!(other instanceof JsonString)) { return false; }
    return this.string.equals(((JsonString) other).asString());
  }

  @Override
  public final int hashCode() {
    return this.string.hashCode();
  }

  @Override
  public final String toString() {
    return examine(StringExaminer.simpleEscaping());
  }

  private static final class Empty extends JsonStringImpl {

    private static final JsonString INSTANCE = new Empty();

    private static final long serialVersionUID = -5962354202170844585L;

    private Empty() {
      super("");
    }

    private Object readResolve() {
      return INSTANCE;
    }
  }
}
